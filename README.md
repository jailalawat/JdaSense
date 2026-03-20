# JdaSense: AI-Powered Heart Sound Diagnostic System
gemini --resume 63ce3442-b0ee-4a61-977a-63ae1f422724
codex resume 019d041b-ec49-7792-9de9-a3d7ef0daabf
**JdaSense** is a high-fidelity heart sound diagnostic system that allows users to capture uncompressed heart sounds using their Android smartphone, transmit the data to a serverless AI backend, and receive real-time analysis for potential heart abnormalities (e.g., murmurs).

---

## 🚀 Objective
Build an end-to-end "Data Flywheel" for medical diagnostics:
1.  **Mobile Capture:** High-quality, uncompressed audio recording with hardware/software noise cancellation.
2.  **AI Inference:** Deep learning model (ResNet18) for classifying heart sounds via Mel-Spectrograms.
3.  **Serverless Cloud:** Scalable, cost-effective inference and data storage for continuous retraining.

---

## 📖 Documentation
For detailed setup and deployment instructions, refer to the **[Development & Deployment Guide](docs/DEVELOPMENT_GUIDE.md)**.

---

## 🏛️ Architecture & Tech Stack

### 1. Mobile Frontend (Android / Kotlin)
*   **Audio Engine:** `AudioRecord` API (16-bit PCM, 8000Hz).
*   **Noise Cancellation:** Triple hardware effects (Suppressor, Echo Canceler, Gain Control) + Software Band-pass Filter (20Hz-600Hz).
*   **UI/UX:** MVVM Architecture with Hilt, Retrofit, and ViewBinding. Features a professional "Hospital" theme and real-time waveform visualizer.
*   **Testing:** Integrated Mock Server for end-to-end flow verification without a live backend.

### 2. AI Pipeline (PyTorch / Python)
*   **Framework:** PyTorch (Backbone: ResNet18).
*   **Preprocessing:** Librosa & SciPy (Butterworth Band-pass Filter, Mel-Spectrogram generation).
*   **Datasets:** Verified PhysioNet sources managed via `ai/verified_sources.json` (currently CirCor DigiScope + CinC 2016), including per-source `labeling` rules for config-driven integration.
*   **Deployment:** ONNX Format for high-speed cloud inference.

### 3. Serverless Backend (AWS / FastAPI)
*   **Compute:** AWS Lambda (Python) with FastAPI & Mangum.
*   **Data Flywheel:** Amazon S3 for secure audio storage and future retraining cycles.

---

## 📍 Current Project Status

### ✅ Mobile App Implementation (Complete)
*   **Project Initialization:** Hilt, MVVM, and Android 14 (API 34) setup.
*   **Audio Core:** Uncompressed WAV capture with Noise Gate.
*   **Noise Cancellation:** Robust hardware/software filtering.
*   **Networking:** Retrofit + Multipart upload logic.
*   **End-to-End Testing:** Mock server with random success/anomaly results and retry logic.

### ✅ Serverless Backend Deployment (Complete)
*   **API Framework:** FastAPI with Mangum for AWS Lambda compatibility.
*   **AI Inference:** ONNX Runtime integrated for high-speed cloud diagnostics.
*   **Infrastructure:** AWS SAM template for automated deployment (Lambda, S3, DynamoDB).
*   **Data Flywheel:** Automatic S3 capture and DynamoDB logging of all predictions.

### ✅ AI Model & Training Pipeline (Complete)
*   **Preprocessing:** Butterworth Band-pass filtering and Mel-Spectrogram generation.
*   **Model:** ResNet18 backbone with weighted cross-entropy for anomaly detection.
*   **Automation:** End-to-end retraining script and ONNX export.
*   **Flywheel Logic:** Automated S3 ingestion and Label Studio integration.

---

## 🛠️ Getting Started

### Prerequisites
*   Android Studio (Iguana or later) for the mobile app.
*   Python 3.10+ for the AI pipeline.

### Installation
1.  **Clone the Repository:**
    ```bash
    git clone git@github.com:jailalawat/JdaSense.git
    cd JdaSense
    ```
2.  **Mobile App:**
    *   Open the `mobile/` folder in Android Studio.
    *   Build and run on an Android device (Target API 34).
3.  **AI Pipeline:**
    *   `cd ai`
    *   `pip install -r requirements.txt`
    *   Run `python download_data.py` to fetch all enabled verified sources.
    *   Run `python preprocess.py` (incremental; unchanged raw data is auto-skipped).
    *   Run stronger training with:
      `python train.py --cv-folds 5 --target-sensitivity 0.90`
    *   Check latest model metrics quickly:
      `python check_accuracy.py`
    *   Retrain automation is incremental: unchanged data/settings will skip re-training on next run.
    *   Force full retrain when needed:
      `FORCE_RETRAIN=1 CV_FOLDS=5 TARGET_SENSITIVITY=0.90 python ai/automate_retrain.py`

### AI Commands (Recommended)
```bash
# Normal incremental run (auto-skip when unchanged)
CV_FOLDS=5 TARGET_SENSITIVITY=0.90 python ai/automate_retrain.py

# Force full retrain even if unchanged
FORCE_RETRAIN=1 CV_FOLDS=5 TARGET_SENSITIVITY=0.90 python ai/automate_retrain.py

# Check current model metrics
python ai/check_accuracy.py
```

### Data Freshness / New-Only Options
```bash
# 1) Force fresh data pull (clear old raw+processed first)
FORCE_DATA_REFRESH=1 CV_FOLDS=5 TARGET_SENSITIVITY=0.90 python ai/automate_retrain.py

# 2) Train only when new synced data exists (skip if no new files)
TRAIN_ON_NEW_DATA_ONLY=1 CV_FOLDS=5 TARGET_SENSITIVITY=0.90 python ai/automate_retrain.py

# 3) Always clear processed/trained segments before preprocessing
RESET_PROCESSED_BEFORE_TRAIN=1 CV_FOLDS=5 TARGET_SENSITIVITY=0.90 python ai/automate_retrain.py
```

### Pipeline State Files
* `ai/training_lock.json`:
  Canonical pipeline lock (portable). Tracks trained data fingerprint, settings, and artifact hashes.
  This file can be committed as the source-of-truth snapshot for fresh clones/environments.
* `ai/trained_data_manifest.json`:
  Canonical trained-input manifest. Raw files listed here are treated as already consumed by the current model and will be skipped on future runs.
* `ai/data/sync_state.json` and `ai/preprocess_state.json`:
  Local runtime cache/state for speed only (ignored by git).

### Weekly Update Plan (AI + AWS Deploy)
```bash
# 1) Refresh verified datasets
python ai/download_data.py

# 2) Incremental retrain + export ONNX (sync + train only on new data)
TRAIN_ON_NEW_DATA_ONLY=1 CV_FOLDS=5 TARGET_SENSITIVITY=0.90 python ai/automate_retrain.py

# 3) Optional: free local training data after successful run
CLEAN_LOCAL_DATA_AFTER_SUCCESS=1 CV_FOLDS=5 TARGET_SENSITIVITY=0.90 python ai/automate_retrain.py

# 4) Review latest metrics
python ai/check_accuracy.py

# 5) Deploy latest backend/model to AWS
cd backend
sam build
sam deploy
```

---

## 🔒 Security & Privacy
*   **Anonymity:** No PII (Personally Identifiable Information) collected.
*   **Encryption:** All data transmitted via HTTPS.
*   **Consent:** Explicit opt-in required before audio is uploaded for AI training.

---

## 📧 Contact
For research inquiries or support, contact **Jai Lalawat** at jailalawat@gmail.com.
