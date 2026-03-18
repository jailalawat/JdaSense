# JdaSense AI Pipeline: Detailed Implementation Plan

## Goal
Develop a robust deep learning pipeline to classify heart sounds into `Normal` vs `Abnormal` (Murmur detection) using Mel-Spectrograms and a CNN architecture.

---

## 1. Environment & Data Acquisition ✅
*   **Step 1: Setup Workspace** ✅
    *   Initialize `ai/` directory with `requirements.txt` (Torch, Librosa, WFDB). ✅
    *   Configure `data/raw` and `data/processed` storage paths. ✅
*   **Step 2: Download PhysioNet Datasets** ✅
    *   **CirCor DigiScope (Primary):** ~5,000 recordings with murmur/normal labels. ✅
    *   **CinC 2016 (Augmentation):** Noisy real-world heart sounds. ✅
    *   *Action:* Run `ai/download_data.py`. ✅

## 2. Preprocessing & Feature Engineering ✅
*   **Step 3: Signal Cleaning (Denoising)** ✅
    *   Apply a **Butterworth Band-pass Filter** (25Hz - 400Hz) to remove breathing noise and high-frequency interference. ✅
    *   Resample all audio to **8,000Hz** (matching the Android app's capture rate). ✅
*   **Step 4: Segmentation & Mel-Spectrograms** ✅
    *   Slice audio into fixed **5-second windows** with 50% overlap. ✅
    *   Convert audio segments into **Mel-Spectrogram images** (128 Mel bands). ✅
    *   *Output:* A dataset of `.png` or `.npy` spectrograms ready for CNN consumption. ✅

## 3. Model Development & Training ✅
*   **Step 5: Architecture Selection** ✅
    *   Use **ResNet18** (Pre-trained on ImageNet) as the backbone. ✅
    *   Modify the input layer to accept 1-channel (grayscale) spectrograms. ✅
    *   Update the output layer for Binary Classification (Softmax/Sigmoid). ✅
*   **Step 6: Training Cycle** ✅
    *   **Loss Function:** Weighted Cross-Entropy (to handle class imbalance). ✅
    *   **Optimizer:** Adam with a learning rate scheduler. ✅
    *   **Augmentation:** Time-shifting and frequency masking (SpecAugment). ✅
*   **Step 7: Evaluation** ✅
    *   Focus on **Sensitivity (Recall)**: In medical diagnostics, missing an abnormality is worse than a false alarm. ✅

## 4. Export & Deployment ✅
*   **Step 8: Model Serialization** ✅
    *   Export the best-performing model to **ONNX format**. ✅
    *   Verify the ONNX model output matches PyTorch results using `onnxruntime`. ✅
*   **Step 9: Inference Script** ✅
    *   Write a Python wrapper that takes a `.wav` file, applies the *exact* same preprocessing as training, and runs ONNX inference. ✅

## 5. The Data Flywheel (MLOps) ✅
*   **Step 10: Feedback Loop** ✅
    *   Collect "Low Confidence" recordings from the Serverless S3 bucket. ✅
    *   Use **Label Studio** for expert review and re-labeling. ✅
*   **Step 11: Automated Retraining** ✅
    *   Script the ingestion of new labeled data into the training set. ✅
    *   Establish a CI/CD pipeline that re-exports the model to the Serverless Backend. ✅

---

## Final Status:
The JdaSense AI Pipeline is fully implemented and automated. The "Data Flywheel" is ready to ingest real-world recordings and improve the model over time.
