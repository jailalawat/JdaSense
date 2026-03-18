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
3.  **Local Testing:**
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

### Training Workflow
1.  **Preprocessing:** Convert raw `.wav` to Mel-Spectrograms.
    ```bash
    python preprocess.py
    ```
2.  **Training:** Train the ResNet18 classifier.
    ```bash
    python train.py
    ```
3.  **Export:** Convert the PyTorch model (`.pth`) to ONNX for production.
    ```bash
    python export_onnx.py
    ```

### 🔄 Dynamic Data Retraining (Always Fresh AI)
The system is designed to stay updated with verified open-source datasets (e.g., PhysioNet).
*   **Verified Sources:** Managed in `ai/verified_sources.json`.
*   **Automation:** Run the fetcher to sync new data and auto-trigger retraining:
    ```bash
    python ai/dynamic_data_fetcher.py
    ```
*   **Verification:** Only datasets marked as `verified: true` are ingested.

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
Due to the size of AI libraries (`librosa`, `onnxruntime`), we deploy via Docker.

1.  **Build the Container:**
    ```bash
    cd backend
    sam build
    ```
2.  **Initial Deployment:**
    ```bash
    sam deploy --guided
    ```
    *Note: During guided setup, allow SAM to create an ECR repository for your image.*

3.  **Infrastructure Components:**
    *   **Lambda:** Runs the FastAPI app and ONNX inference.
    *   **API Gateway:** Exposes the `/predict` endpoint.
    *   **S3 (`jdasense-recordings`):** Stores all uploaded audio for future training.
    *   **DynamoDB (`jdasense-predictions`):** Logs prediction results and confidence.

---

## 🔄 End-to-End Workflow
1.  **Capture:** User records heart sound on Mobile.
2.  **Noise Cancellation:** Audio is cleaned in real-time.
3.  **Upload:** Mobile app POSTs `.wav` to AWS API Gateway.
4.  **Inference:** Lambda processes audio into a spectrogram and runs ONNX inference.
5.  **Storage:** Lambda saves `.wav` to S3 and result to DynamoDB.
6.  **Diagnosis:** Mobile app displays "Normal" or "Anomaly" with a medical disclaimer.
7.  **Flywheel:** AI Pipeline pulls S3 data to retrain and improve accuracy.
