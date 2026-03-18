# JdaSense Mobile App: Detailed Implementation Plan (Android/Kotlin)

## Goal
Build a high-fidelity Android application that captures uncompressed heart sounds, transmits them to a serverless AI backend, and displays the diagnostic results in real-time.

---

## 1. Project Initialization & Dependencies ✅
*   **Language:** Kotlin ✅
*   **Target SDK:** Android 14 (API 34) ✅
*   **Architecture:** MVVM (Model-View-ViewModel) ✅
*   **Dependencies:**
    *   **Retrofit & OkHttp:** For REST API communication. ✅
    *   **Coroutines & Flow:** For asynchronous tasks and reactive UI updates. ✅
    *   **Hilt:** For Dependency Injection (DI). ✅
    *   **Lifecycle (ViewModel, LiveData):** To handle configuration changes. ✅
    *   **ViewBinding:** For safe access to UI elements. ✅
    *   **Lottie/Canvas:** For real-time waveform visualization during recording. ✅

## 2. Permissions & Security ✅
*   **RECORD_AUDIO:** Essential for heart sound capture. ✅
*   **INTERNET:** To send data to the Cloud AI. ✅
*   **READ/WRITE_EXTERNAL_STORAGE (Optional):** Handled via internal cache for security. ✅
*   **Privacy:** Implement an onboarding screen explaining how data is anonymized and used for AI training. ✅

## 3. The Audio Engine (Core Module) 🏗️
*   **Capture Strategy:** Use `AudioRecord` (not `MediaRecorder`) to get raw, uncompressed 16-bit PCM data. ✅
*   **Sampling Rate:** 8,000Hz (Optimized for low-frequency heart sounds). ✅
*   **WAV Writer:** A custom utility to wrap raw PCM data into a `.wav` header. ✅
*   **Noise Gate:** Basic software logic to ensure recording only starts when a signal threshold is met. ✅
*   **Noise Cancellation:** Implement hardware/software suppression to eliminate background hum and environmental noise. 🆕

## 4. UI/UX Design ✅
*   **Screen 1: Landing/Onboarding** ✅
    *   Brief explanation of JdaSense. ✅
    *   Instructions on where to place the phone (e.g., "Left side of chest, directly on skin"). ✅
*   **Screen 2: Recording** ✅
    *   Large "Start" button. ✅
    *   Real-time **Waveform Visualizer** (to give the user confidence that the mic is picking up the heart). ✅
    *   10-second countdown timer. ✅
*   **Screen 3: Analysis/Result** ✅
    *   "Analyzing..." loading state (Lottie animation). ✅
    *   Clear result: "Normal" or "Anomaly Detected - Consult a Professional." ✅
    *   Disclaimer that this is not a final medical diagnosis. ✅

## 5. Networking & API Integration ✅
*   **Endpoint:** `POST /predict` ✅
*   **Request Type:** `MultipartBody` (sending the `.wav` file). ✅
*   **Response Handling:** Parse JSON result and handle network errors (retry logic). ✅
*   **Testing:** MockInterceptor for end-to-end simulation. ✅

## 6. Development Milestones
*   **Milestone 1:** Basic project setup + Permission handling. ✅
*   **Milestone 2:** Implement `AudioRecord` and verify we can save a playable `.wav` file. ✅
*   **Milestone 3:** Build the Recording UI with Waveform. ✅
*   **Milestone 4:** Integrate Retrofit with a "mock" API. ✅
*   **Milestone 5:** Connect to the real Serverless Backend and test end-to-end. ✅
*   **Milestone 6:** Implement **Noise Cancellation** and refine Audio Engine. 🆕

---

## Next Action:
1.  Implement `NoiseSuppressor` in `AudioRecorder.kt` to filter environmental noise.
2.  Test audio quality improvements.
