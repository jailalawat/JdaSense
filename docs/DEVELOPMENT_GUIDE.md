# JdaSense: Comprehensive Development & Deployment Guide

This guide covers the setup, development, and production deployment process for the JdaSense Heart Sound Diagnostic System.

---

## 🏗 Repository Structure
*   `/mobile`: Native Android App (Kotlin/MVVM).
*   `/ai`: Training pipeline, preprocessing, and model export (PyTorch/Python).
*   `/backend`: Serverless API and inference engine (FastAPI/Docker/AWS).
*   `/docs`: Architecture plans and implementation details.

---

## 📱 1. Mobile App (Android)

### Development Setup
1.  **IDE:** Install Android Studio (Iguana or later).
2.  **SDK:** Ensure Android SDK 34 (Android 14) is installed.
3.  **Build Guide:** Refer to the **[Mobile Build Guide](MOBILE_BUILD_GUIDE.md)** for APK generation instructions.
4.  **Local Testing:**
    *   The app includes a `MockInterceptor` enabled by default.
    *   Toggle `USE_MOCK = true` in `mobile/app/src/main/kotlin/com/jdasense/app/di/NetworkModule.kt` to test without a backend.

### Key Features
*   **Audio Core:** Captures 16-bit PCM @ 8000Hz via `AudioRecord`.
*   **Noise Cancellation:** Always-on hardware effects + software Band-pass filter (20Hz-600Hz).
*   **UI:** Professional hospital theme with real-time waveform visualization.

### Build & Production
*   Increment `versionCode` in `app/build.gradle` for updates.
*   The `signingConfigs` block is prepared for release; replace placeholders with your `.jks` file for Play Store deployment.

---

## 🤖 2. AI Pipeline (Python)

### Development Setup
1.  **Environment:** Python 3.10+
2.  **Install Dependencies:**
    ```bash
    cd ai
    pip install -r requirements.txt
    ```
3.  **Data Acquisition:**
    ```bash
    python download_data.py
    ```
    This downloads all enabled verified sources listed in `ai/verified_sources.json`.

### Training Workflow
1.  **Preprocessing:** Convert raw `.wav` to Mel-Spectrograms.
    ```bash
    python preprocess.py
    ```
    This step is incremental and skips processing when raw audio is unchanged (`ai/preprocess_state.json`).
2.  **Training:** Train the ResNet18 classifier.
    ```bash
    python train.py
    ```
    For stronger validation and medical-priority tuning:
    ```bash
    python train.py --cv-folds 5 --target-sensitivity 0.90
    ```
    This writes `ai/cv_report.json` with fold-wise metrics.
3.  **Export:** Convert the PyTorch model (`.pth`) to ONNX for production.
    ```bash
    python export_onnx.py
    ```
4.  **Metrics Check:** Print holdout/CV metrics from training artifacts.
    ```bash
    python check_accuracy.py
    ```

### AI Commands (Recommended)
```bash
# Normal incremental run (auto-skip when unchanged)
CV_FOLDS=5 TARGET_SENSITIVITY=0.90 python ai/automate_retrain.py

# Force full retrain even if unchanged
FORCE_RETRAIN=1 CV_FOLDS=5 TARGET_SENSITIVITY=0.90 python ai/automate_retrain.py

# Check current model metrics
python ai/check_accuracy.py
```

### Fresh Data / New-Only Training Controls
```bash
# Force fresh data pull (clear old raw+processed)
FORCE_DATA_REFRESH=1 CV_FOLDS=5 TARGET_SENSITIVITY=0.90 python ai/automate_retrain.py

# Train only if newly synced files exist
TRAIN_ON_NEW_DATA_ONLY=1 CV_FOLDS=5 TARGET_SENSITIVITY=0.90 python ai/automate_retrain.py

# Clear processed/trained segments before preprocessing
RESET_PROCESSED_BEFORE_TRAIN=1 CV_FOLDS=5 TARGET_SENSITIVITY=0.90 python ai/automate_retrain.py
```
State model:
* `ai/training_lock.json` is the canonical lock artifact (portable across fresh clones).
* `ai/data/sync_state.json` and `ai/preprocess_state.json` are local cache/runtime state.

### Weekly Update Plan (AI + AWS)
```bash
# 1) Refresh verified datasets
python ai/download_data.py

# 2) Incremental retrain + ONNX export (new data only)
TRAIN_ON_NEW_DATA_ONLY=1 CV_FOLDS=5 TARGET_SENSITIVITY=0.90 python ai/automate_retrain.py

# 3) Optional cleanup of local training data after success
CLEAN_LOCAL_DATA_AFTER_SUCCESS=1 CV_FOLDS=5 TARGET_SENSITIVITY=0.90 python ai/automate_retrain.py

# 4) Review accuracy/metrics
python ai/check_accuracy.py

# 5) Deploy latest model/backend
cd backend
sam build
sam deploy
```

### 🔄 Dynamic Data Retraining (Always Fresh AI)
The system is designed to stay updated with verified open-source datasets (e.g., PhysioNet CirCor + PhysioNet CinC 2016).
*   **Verified Sources:** Managed in `ai/verified_sources.json`.
*   **Label Adapters:** Each source defines a `labeling` block in `ai/verified_sources.json` (CSV-based mapping), so new compatible datasets can be added without editing `train.py`.
*   **Automation:** Run the fetcher to sync new data and auto-trigger retraining:
    ```bash
    python ai/dynamic_data_fetcher.py
    ```
*   **Incremental Runs:** `automate_retrain.py` now skips training/export when processed data + label config + training settings are unchanged.
*   **Force Full Run:** Set `FORCE_RETRAIN=1` to bypass skip logic.
    ```bash
    FORCE_RETRAIN=1 CV_FOLDS=5 TARGET_SENSITIVITY=0.90 python ai/automate_retrain.py
    ```
*   **Verification:** Only datasets marked as `verified: true` are ingested.
*   **Source Control:** Set `enabled: true/false` per dataset in `ai/verified_sources.json`.

---

### Production (The Data Flywheel)
*   Use `flywheel_ingest.py` to pull low-confidence data from S3 for labeling.
*   Use `automate_retrain.py` to run the entire pipeline in one command.

---

## ☁️ 3. Serverless Backend (AWS)

### Prerequisites
*   **AWS CLI:** Configured with administrative access.
*   **Docker Desktop:** Must be running (required for AI library compilation).
*   **AWS SAM CLI:** Installed via Homebrew (`brew install aws-sam-cli`).

### Local Development
Run the FastAPI server locally for testing:
```bash
cd backend
uvicorn app.main:app --reload
```

### Production Deployment (Containerized)
Due to the size of AI libraries (`librosa`, `onnxruntime`), we deploy via Docker to handle up to 10GB of dependencies.

#### 🚀 First-Time Deployment
Run this command from the `backend/` folder. It automatically creates the necessary S3 buckets and ECR repositories:
```bash
cd backend
sam build
sam deploy \
    --stack-name jdasense-backend \
    --resolve-s3 \
    --resolve-image-repos \
    --region ap-south-1 \
    --capabilities CAPABILITY_IAM \
    --confirm-changeset
```

#### 🔄 Updating the Backend (Next Time)
Whenever you change the code (`main.py`) or the AI model (`.onnx`), follow these three steps:

1.  **Build:**
    ```bash
    sam build
    ```
2.  **Deploy:**
    ```bash
    sam deploy
    ```
    *(Because you used the flags in the first deployment, SAM remembers the settings. It will build a new Docker image, push it to ECR, and update the Lambda function automatically.)*

3.  **Sync (Fast Testing):**
    For quick code changes without full re-deployment, you can use:
    ```bash
    sam sync --stack-name jdasense-backend --watch
    ```

---

### Infrastructure Components:
*   **Lambda:** Docker-based FastAPI app + ONNX inference.
*   **API Gateway:** Secure HTTPS endpoint requiring `x-api-key`.
*   **ECR:** Managed repository for your backend Docker images.
*   **S3 (`jdasense-recordings`):** Audio storage for the Data Flywheel.
*   **DynamoDB:** User registry, Audit logs, and Prediction history.


---

## 🚀 Production Operations & Verification

### 1. Deployment (AWS ap-south-1)
To deploy or update the entire stack (Lambda, API Gateway, S3, DynamoDB) in the Mumbai region:
```bash
cd backend
sam build
sam deploy --stack-name jdasense-backend --resolve-s3 --resolve-image-repos --region ap-south-1 --capabilities CAPABILITY_IAM --confirm-changeset
```

### 2. Authentication & Admin Access
**Default Admin Credentials:**
*   **Email:** `jailalawat@gmail.com`
*   **Password:** `admin123`

#### Get the API Key Value:
CloudFormation output shows the Key ID. Use this command to get the actual secret value for headers:
```bash
# Replace 't8dot6taql' with the ID from your SAM output
aws apigateway get-api-key --api-key t8dot6taql --include-value --region ap-south-1 --query "value" --output text
```

#### Get a JWT Token:
```bash
curl -X POST https://v0vo91g9da.execute-api.ap-south-1.amazonaws.com/Prod/auth/login \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -H "x-api-key: <YOUR_API_KEY_VALUE>" \
     -d "username=jailalawat@gmail.com&password=admin123"
```

### 3. End-to-End Production Testing
To verify the live API, S3 capture, and DynamoDB logging:
1.  **Configure:** Open `backend/tests/production_test.py` and paste your API Key and JWT Token.
2.  **Run:**
    ```bash
    pip install requests boto3 "botocore[crt]"
    python3 backend/tests/production_test.py
    ```

### 4. Useful Troubleshooting Commands

#### View Lambda Error Logs:
```bash
aws logs describe-log-groups --region ap-south-1 --query "logGroups[?contains(logGroupName,'JdaSenseApiFunction')].logGroupName" --output text | xargs -I {} aws logs tail {} --region ap-south-1
```

#### Verify Data in S3:
```bash
# List all captured recordings
aws s3 ls s3://jdasense-recordings/raw/ --region ap-south-1
```

#### Check DynamoDB Logs:
```bash
aws dynamodb get-item \
    --table-name jdasense-predictions \
    --key '{"record_id": {"S": "<RECORD_ID_FROM_TEST>"}}' \
    --region ap-south-1
```
