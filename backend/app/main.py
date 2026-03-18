from fastapi import FastAPI, UploadFile, File, HTTPException, Depends, status
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm
from mangum import Mangum
import boto3
import uuid
import os
import onnxruntime
import numpy as np
import librosa
import scipy.signal as signal
from pydantic import BaseModel
from datetime import datetime
from typing import List, Optional

# Import local modules
from app.auth import verify_password, get_password_hash, create_access_token, decode_access_token
from app.database import (
    get_user_by_email, create_user, soft_delete_user, 
    get_all_users, log_audit, get_audit_logs, dynamodb
)

app = FastAPI(title="JdaSense API with RBAC")
handler = Mangum(app)

# --- Configuration & Constants ---
S3_BUCKET = os.getenv("S3_BUCKET", "jdasense-recordings")
DYNAMODB_TABLE = os.getenv("DYNAMODB_TABLE", "jdasense-predictions")
MODEL_PATH = "model/heart_sound_model.onnx"
s3 = boto3.client("s3")
predictions_table = dynamodb.Table(DYNAMODB_TABLE)

SAMPLE_RATE = 8000
N_MELS = 128
HOP_LENGTH = 512
FMIN = 20
FMAX = 600

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="auth/login")

# --- Roles ---
ROLE_SUPERADMIN = "SuperAdmin"
ROLE_HOSPITALOWNER = "HospitalOwner"
ROLE_STAFF = "StaffUser"

# --- Models ---
class PredictionResponse(BaseModel):
    result: str
    is_anomaly: bool
    confidence: float
    record_id: str

class UserCreate(BaseModel):
    email: str
    password: str
    role: str
    name: str
    hospital_id: Optional[str] = None

class UserResponse(BaseModel):
    email: str
    role: str
    name: str
    hospital_id: str
    is_deleted: bool

class Token(BaseModel):
    access_token: str
    token_type: str

# --- Startup Event (Seed Default Admin) ---
@app.on_event("startup")
def startup_event():
    # Only seed if running in an environment where we want a default
    default_email = "jailalawat@gmail.com"
    if not get_user_by_email(default_email):
        print(f"Seeding default SuperAdmin: {default_email}")
        create_user(
            email=default_email,
            password_hash=get_password_hash("admin123"), # Force change on first login in real app
            role=ROLE_SUPERADMIN,
            name="Jai Lalawat"
        )

# --- Auth Dependencies ---
async def get_current_user(token: str = Depends(oauth2_scheme)):
    payload = decode_access_token(token)
    if payload is None:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid token")
    
    email = payload.get("sub")
    user = get_user_by_email(email)
    if user is None or user.get("is_deleted", False):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="User not found or inactive")
    return user

async def require_admin(current_user: dict = Depends(get_current_user)):
    if current_user["role"] != ROLE_SUPERADMIN:
        raise HTTPException(status_code=403, detail="SuperAdmin access required")
    return current_user

async def require_management_access(current_user: dict = Depends(get_current_user)):
    if current_user["role"] not in [ROLE_SUPERADMIN, ROLE_HOSPITALOWNER]:
        raise HTTPException(status_code=403, detail="Management access required")
    return current_user

# --- Authentication Endpoints ---
@app.post("/auth/login", response_model=Token)
async def login(form_data: OAuth2PasswordRequestForm = Depends()):
    user = get_user_by_email(form_data.username)
    if not user or user.get("is_deleted", False):
        raise HTTPException(status_code=400, detail="Incorrect email or password")
    
    if not verify_password(form_data.password, user["password_hash"]):
        raise HTTPException(status_code=400, detail="Incorrect email or password")
    
    access_token = create_access_token(data={"sub": user["email"], "role": user["role"], "hospital_id": user["hospital_id"]})
    return {"access_token": access_token, "token_type": "bearer"}

# --- User Management Endpoints ---
@app.post("/users", response_model=UserResponse)
async def create_new_user(user: UserCreate, current_user: dict = Depends(require_management_access)):
    # HospitalOwner can only create StaffUser for their own hospital
    if current_user["role"] == ROLE_HOSPITALOWNER:
        if user.role != ROLE_STAFF:
            raise HTTPException(status_code=403, detail="Hospital Owners can only create Staff Users")
        user.hospital_id = current_user["hospital_id"]
    
    if get_user_by_email(user.email):
        raise HTTPException(status_code=400, detail="Email already registered")
        
    hashed_password = get_password_hash(user.password)
    new_user = create_user(user.email, hashed_password, user.role, user.hospital_id, user.name)
    
    log_audit(current_user["email"], "CREATE_USER", user.email, current_user["hospital_id"])
    return new_user

@app.get("/users")
async def list_users(current_user: dict = Depends(require_management_access)):
    if current_user["role"] == ROLE_SUPERADMIN:
        users = get_all_users(include_deleted=True)
    else:
        users = get_all_users(hospital_id=current_user["hospital_id"], include_deleted=False)
        
    # Strip passwords before returning
    for u in users:
        u.pop("password_hash", None)
    return users

@app.delete("/users/{email}")
async def delete_user(email: str, current_user: dict = Depends(require_management_access)):
    target_user = get_user_by_email(email)
    if not target_user:
        raise HTTPException(status_code=404, detail="User not found")
        
    if current_user["role"] == ROLE_HOSPITALOWNER and target_user["hospital_id"] != current_user["hospital_id"]:
        raise HTTPException(status_code=403, detail="Cannot delete users from other hospitals")
        
    soft_delete_user(email)
    log_audit(current_user["email"], "SOFT_DELETE_USER", email, current_user["hospital_id"])
    return {"message": f"User {email} successfully deactivated"}

# --- Audit Endpoints ---
@app.get("/audit")
async def view_audit_logs(current_user: dict = Depends(require_management_access)):
    if current_user["role"] == ROLE_SUPERADMIN:
        return get_audit_logs()
    else:
        return get_audit_logs(hospital_id=current_user["hospital_id"])

# --- Prediction Endpoint (Protected) ---
def butter_bandpass(lowcut, highcut, fs, order=5):
    nyquist = 0.5 * fs
    low = lowcut / nyquist
    high = highcut / nyquist
    b, a = signal.butter(order, [low, high], btype="band")
    return b, a

def apply_bandpass_filter(data, lowcut=25, highcut=400, fs=8000, order=5):
    b, a = butter_bandpass(lowcut, highcut, fs, order=order)
    return signal.lfilter(b, a, data)

def preprocess_audio(file_content):
    with open("/tmp/temp.wav", "wb") as f:
        f.write(file_content)
    y, sr = librosa.load("/tmp/temp.wav", sr=SAMPLE_RATE)
    y_filtered = apply_bandpass_filter(y, fs=SAMPLE_RATE)
    samples = 5 * SAMPLE_RATE
    y_segment = np.pad(y_filtered, (0, max(0, samples - len(y_filtered))))[:samples]
    spec = librosa.feature.melspectrogram(
        y=y_segment, sr=SAMPLE_RATE, n_mels=N_MELS, hop_length=HOP_LENGTH, fmin=FMIN, fmax=FMAX
    )
    spec_dB = librosa.power_to_db(spec, ref=np.max)
    spec_norm = (spec_dB - np.mean(spec_dB)) / (np.std(spec_dB) + 1e-6)
    return spec_norm[np.newaxis, np.newaxis, ...].repeat(3, axis=1).astype(np.float32)

@app.post("/predict", response_model=PredictionResponse)
async def predict(audio: UploadFile = File(...), current_user: dict = Depends(get_current_user)):
    if not audio.filename.endswith(".wav"):
        raise HTTPException(status_code=400, detail="Only .wav files are supported")

    record_id = str(uuid.uuid4())
    content = await audio.read()
    
    s3_key = f"raw/{record_id}.wav"
    s3.put_object(Bucket=S3_BUCKET, Key=s3_key, Body=content)

    if not os.path.exists(MODEL_PATH):
        return {"result": "Mock: Normal", "is_anomaly": False, "confidence": 0.99, "record_id": record_id}

    input_data = preprocess_audio(content)
    ort_session = onnxruntime.InferenceSession(MODEL_PATH)
    ort_inputs = {ort_session.get_inputs()[0].name: input_data}
    logits = ort_session.run(None, ort_inputs)[0]
    
    probs = np.exp(logits) / np.sum(np.exp(logits))
    prediction_idx = np.argmax(probs)
    result = "Normal" if prediction_idx == 0 else "Anomaly Detected"
    confidence = float(np.max(probs))

    # Log to DynamoDB
    predictions_table.put_item(Item={
        "record_id": record_id,
        "user_email": current_user["email"],
        "hospital_id": current_user["hospital_id"],
        "timestamp": datetime.now().isoformat(),
        "s3_key": s3_key,
        "prediction": result,
        "is_anomaly": bool(prediction_idx == 1),
        "confidence": str(confidence)
    })
    
    log_audit(current_user["email"], "RUN_DIAGNOSTIC", record_id, current_user["hospital_id"])

    return {
        "result": result,
        "is_anomaly": bool(prediction_idx == 1),
        "confidence": confidence,
        "record_id": record_id
    }

@app.get("/health")
def health():
    return {"status": "healthy"}
