# Implementation Plan: JdaSense Clone (Cloud-Based Heart Sound Diagnostic)

## Objective
Build a complete, end-to-end system that allows users to record their heart sounds using an Android smartphone microphone, sends the data to a serverless backend, and utilizes an open-source AI model to detect heart abnormalities (like murmurs). The system is designed with a "Data Flywheel" to continuously improve accuracy as more data is collected.

## Scope & Architecture

The architecture consists of three main pillars:
1. **Mobile Frontend:** Native Android app built with Kotlin.
2. **Serverless Backend:** Cloud infrastructure to handle API requests, run the AI model, and store data.
3. **AI Pipeline:** Python-based training environment using open-source datasets and models.

## Proposed Tech Stack

### 1. Mobile App (Android / Kotlin)
* **Language:** Kotlin
* **Audio Capture:** Android `AudioRecord` API (capturing uncompressed 16-bit PCM `.wav` files at 4000Hz or 8000Hz).
* **Networking:** Retrofit & OkHttp (for sending the `.wav` file to the backend).
* **Architecture:** MVVM (Model-View-ViewModel) with Coroutines for asynchronous network calls.

### 2. AI Model & Training Pipeline
* **Framework:** PyTorch (Industry standard for audio/vision research).
* **Audio Processing:** `librosa` (to convert audio into Mel-Spectrogram images).
* **Model Architecture:** A Convolutional Neural Network (CNN) like **ResNet18** or **MobileNetV2**. These are highly efficient at classifying images (spectrograms).
* **Initial Datasets:** 
  * PhysioNet CirCor DigiScope (5,000+ recordings, Murmur detection).
  * PhysioNet CinC 2016 (Noisy, real-world data).

### 3. Serverless Backend (AWS)
* **API/Compute:** AWS Lambda (Python) with API Gateway OR Google Cloud Run. (Serverless means you only pay when someone uses the app).
* **Web Framework:** FastAPI (wrapped in `Mangum` for AWS Lambda compatibility).
* **Storage (The Data Flywheel):** Amazon S3 (to store the uploaded `.wav` files for future training).
* **Database:** DynamoDB or PostgreSQL (to store metadata, anonymous user IDs, and diagnosis results).

---

## Phased Implementation Plan

### Phase 1: AI Model Development (Local)
1. **Data Preparation:** Download the PhysioNet datasets.
2. **Preprocessing:** Write a Python script using `librosa` to clean the audio (band-pass filter 25Hz-400Hz) and convert it into Mel-Spectrograms.
3. **Model Training:** Train a PyTorch ResNet18 model to classify spectrograms into binary classes: `Normal` vs `Abnormal/Murmur`.
4. **Export:** Export the trained model to `ONNX` or `TorchScript` format for fast serverless execution.

### Phase 2: Serverless Backend Deployment
1. **API Development:** Create a FastAPI application with a `/predict` endpoint that accepts `.wav` file uploads.
2. **Inference Logic:** Within the endpoint, process the audio into a spectrogram, pass it to the trained model, and generate a confidence score.
3. **Data Flywheel:** Add logic to securely save the uploaded `.wav` file to an S3 bucket with an anonymous UUID for future model training.
4. **Deploy:** Deploy the FastAPI app to AWS Lambda via AWS SAM or Serverless Framework.

### Phase 3: Android App Development (Kotlin)
1. **Permissions:** Implement runtime permissions for `RECORD_AUDIO` and `INTERNET`.
2. **Recording UI:** Build a simple UI with a "Record" button. Provide visual feedback (waveform) during the 7-10 second recording.
3. **Audio Engine:** Use `AudioRecord` to capture high-quality, uncompressed audio and save it locally as a `.wav` file.
4. **API Integration:** Use Retrofit to POST the `.wav` file to the Serverless endpoint and display the JSON result (e.g., "Normal - 98% Confidence").

### Phase 4: The Data Flywheel & MLOps
1. **Data Accumulation:** As users use the app, real-world `.wav` files accumulate in the S3 bucket.
2. **Labeling:** Set up an open-source labeling tool like **Label Studio** connected to S3 to periodically review borderline or unconfident predictions.
3. **Retraining:** Periodically download the new data, re-run Phase 1, and redeploy the updated model to the Serverless backend with zero downtime.

---

## Security & Privacy Considerations (Crucial)
* **No PII:** The app will not require names or emails. Device IDs will be anonymized.
* **Encryption:** All audio data transmitted via HTTPS.
* **Consent:** Explicit opt-in required in the Android app before audio is uploaded and stored for research/AI training.

## Alternatives Considered
* **On-Device AI (TensorFlow Lite):** Rejected for V1. While better for privacy and offline use, it prevents the collection of data needed for the "Data Flywheel" and requires heavy model optimization. Can be introduced in V2.