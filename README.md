# JdaSense: AI-Powered Heart Sound Diagnostic System

**JdaSense** is a high-fidelity heart sound diagnostic system that allows users to capture uncompressed heart sounds using their Android smartphone, transmit the data to a serverless AI backend, and receive real-time analysis for potential heart abnormalities (e.g., murmurs).

---

## 🚀 Objective
Build an end-to-end "Data Flywheel" for medical diagnostics:
1.  **Mobile Capture:** High-quality, uncompressed audio recording with hardware/software noise cancellation.
2.  **AI Inference:** Deep learning model (ResNet18) for classifying heart sounds via Mel-Spectrograms.
3.  **Serverless Cloud:** Scalable, cost-effective inference and data storage for continuous retraining.

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
*   **Dataset:** PhysioNet CirCor DigiScope (5,000+ labeled recordings).
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

### 🏗️ AI Model & Training Pipeline (In Progress)
*   **Milestone 1:** Environment and Data Acquisition.
*   **Milestone 2:** Preprocessing & Spectrogram Generation.
*   **Milestone 3:** Model Training & ONNX Export.

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
    *   Run `python download_data.py` to fetch the PhysioNet dataset.

---

## 🔒 Security & Privacy
*   **Anonymity:** No PII (Personally Identifiable Information) collected.
*   **Encryption:** All data transmitted via HTTPS.
*   **Consent:** Explicit opt-in required before audio is uploaded for AI training.

---

## 📧 Contact
For research inquiries or support, contact **Jai Lalawat** at jailalawat@gmail.com.
