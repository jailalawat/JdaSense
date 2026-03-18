import os
import subprocess
import time

def run_retrain_pipeline():
    """
    Automated pipeline to refresh the model.
    """
    print("🚀 Starting Automated Retraining Pipeline...")
    
    # 1. Preprocess new and old data
    print("--- 1. Preprocessing ---")
    subprocess.run(["python", "ai/preprocess.py"], check=True)
    
    # 2. Re-train the model
    print("--- 2. Training ---")
    subprocess.run(["python", "ai/train.py"], check=True)
    
    # 3. Export to ONNX
    print("--- 3. Exporting to ONNX ---")
    subprocess.run(["python", "ai/export_onnx.py"], check=True)
    
    # 4. Final Verification
    if os.path.exists("ai/heart_sound_model.onnx"):
        print("✅ Pipeline Success: New model ready for deployment.")
        # In production, add a shell command here to deploy to AWS Lambda/S3
    else:
        print("❌ Pipeline Failed: ONNX model not found.")

if __name__ == "__main__":
    start_time = time.time()
    run_retrain_pipeline()
    duration = (time.time() - start_time) / 60
    print(f"Total Pipeline Duration: {duration:.2f} minutes")
