from fastapi import FastAPI, UploadFile, File, HTTPException
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

app = FastAPI(title="JdaSense API")
handler = Mangum(app)

# AWS Configuration (from environment variables)
S3_BUCKET = os.getenv("S3_BUCKET", "jdasense-recordings")
DYNAMODB_TABLE = os.getenv("DYNAMODB_TABLE", "jdasense-predictions")
MODEL_PATH = "model/heart_sound_model.onnx"

s3 = boto3.client("s3")
dynamodb = boto3.resource("dynamodb")
table = dynamodb.Table(DYNAMODB_TABLE)

# Preprocessing Constants
SAMPLE_RATE = 8000
N_MELS = 128
HOP_LENGTH = 512
FMIN = 20
FMAX = 600

class PredictionResponse(BaseModel):
    result: str
    is_anomaly: bool
    confidence: float
    record_id: str

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
    # Load from memory
    with open("/tmp/temp.wav", "wb") as f:
        f.write(file_content)
    y, sr = librosa.load("/tmp/temp.wav", sr=SAMPLE_RATE)
    
    # Filter
    y_filtered = apply_bandpass_filter(y, fs=SAMPLE_RATE)
    
    # Segment (first 5s)
    samples = 5 * SAMPLE_RATE
    y_segment = np.pad(y_filtered, (0, max(0, samples - len(y_filtered))))[:samples]
        
    # Mel-Spectrogram
    spec = librosa.feature.melspectrogram(
        y=y_segment, sr=SAMPLE_RATE, n_mels=N_MELS, hop_length=HOP_LENGTH, fmin=FMIN, fmax=FMAX
    )
    spec_dB = librosa.power_to_db(spec, ref=np.max)
    
    # Normalize & Shape for ResNet (1, 3, 128, 79)
    spec_norm = (spec_dB - np.mean(spec_dB)) / (np.std(spec_dB) + 1e-6)
    return spec_norm[np.newaxis, np.newaxis, ...].repeat(3, axis=1).astype(np.float32)

@app.post("/predict")
async def predict(audio: UploadFile = File(...)):
    if not audio.filename.endswith(".wav"):
        raise HTTPException(status_code=400, detail="Only .wav files are supported")

    record_id = str(uuid.uuid4())
    content = await audio.read()
    
    # 1. Save to S3 (The Data Flywheel)
    s3_key = f"raw/{record_id}.wav"
    s3.put_object(Bucket=S3_BUCKET, Key=s3_key, Body=content)

    # 2. Run Inference
    if not os.path.exists(MODEL_PATH):
        # Fallback for testing if model not yet deployed in Lambda layer
        return {"result": "Mock: Normal", "is_anomaly": False, "confidence": 0.99, "record_id": record_id}

    input_data = preprocess_audio(content)
    ort_session = onnxruntime.InferenceSession(MODEL_PATH)
    ort_inputs = {ort_session.get_inputs()[0].name: input_data}
    logits = ort_session.run(None, ort_inputs)[0]
    
    probs = np.exp(logits) / np.sum(np.exp(logits))
    prediction_idx = np.argmax(probs)
    result = "Normal" if prediction_idx == 0 else "Anomaly Detected"
    confidence = float(np.max(probs))

    # 3. Log to DynamoDB
    table.put_item(Item={
        "record_id": record_id,
        "timestamp": datetime.now().isoformat(),
        "s3_key": s3_key,
        "prediction": result,
        "is_anomaly": bool(prediction_idx == 1),
        "confidence": str(confidence)
    })

    return {
        "result": result,
        "is_anomaly": bool(prediction_idx == 1),
        "confidence": confidence,
        "record_id": record_id
    }

@app.get("/health")
def health():
    return {"status": "healthy"}
