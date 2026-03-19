import os
import subprocess
import time
import json
import hashlib
from pathlib import Path
import shutil

QUALITY_REPORT = "ai/data_quality_report.json"
MIN_LABEL_COVERAGE_PERCENT = 80.0
TRAINING_STATE = Path("ai/last_training_state.json")
PROCESSED_DIR = Path("ai/data/processed")
RAW_DIR = Path("ai/data/raw")
VERIFIED_SOURCES = Path("ai/verified_sources.json")
REQUIRED_ARTIFACTS = [
    Path("ai/heart_sound_model.pth"),
    Path("ai/heart_sound_model.onnx"),
    Path("ai/training_report.json"),
    Path("ai/calibration.json"),
]


def compute_data_fingerprint(cv_folds: int, target_sensitivity: str):
    if not PROCESSED_DIR.exists():
        raise RuntimeError(f"Missing processed directory: {PROCESSED_DIR}")

    count = 0
    total_size = 0
    latest_mtime = 0
    for p in PROCESSED_DIR.glob("*.npy"):
        st = p.stat()
        count += 1
        total_size += st.st_size
        latest_mtime = max(latest_mtime, int(st.st_mtime))

    hasher = hashlib.sha256()
    hasher.update(str(count).encode("utf-8"))
    hasher.update(str(total_size).encode("utf-8"))
    hasher.update(str(latest_mtime).encode("utf-8"))
    hasher.update(str(cv_folds).encode("utf-8"))
    hasher.update(str(target_sensitivity or "").encode("utf-8"))

    if os.path.exists(QUALITY_REPORT):
        hasher.update(Path(QUALITY_REPORT).read_bytes())
    if VERIFIED_SOURCES.exists():
        hasher.update(VERIFIED_SOURCES.read_bytes())

    return {
        "fingerprint": hasher.hexdigest(),
        "processed_file_count": count,
        "processed_total_size_bytes": total_size,
        "processed_latest_mtime": latest_mtime,
        "cv_folds": cv_folds,
        "target_sensitivity": target_sensitivity,
    }


def artifacts_ready():
    return all(p.exists() for p in REQUIRED_ARTIFACTS)


def should_skip_training(current_state: dict):
    if not TRAINING_STATE.exists() or not artifacts_ready():
        return False
    try:
        previous = json.loads(TRAINING_STATE.read_text(encoding="utf-8"))
    except Exception:
        return False
    return previous.get("fingerprint") == current_state.get("fingerprint")


def save_training_state(state: dict):
    payload = dict(state)
    payload["updated_at_epoch_seconds"] = int(time.time())
    TRAINING_STATE.write_text(json.dumps(payload, indent=2), encoding="utf-8")


def cleanup_local_training_data():
    """
    Removes local raw/processed training artifacts to save disk.
    Use only after successful training/export.
    """
    removed = []
    for target in [RAW_DIR, PROCESSED_DIR]:
        if target.exists():
            for child in target.iterdir():
                if child.is_dir():
                    shutil.rmtree(child, ignore_errors=True)
                else:
                    child.unlink(missing_ok=True)
            removed.append(str(target))
    if removed:
        print("Local training data cleanup complete:")
        for path in removed:
            print(f"  - cleaned {path}")
    else:
        print("Local training data cleanup: nothing to clean.")

def run_retrain_pipeline():
    """
    Automated pipeline to refresh the model.
    """
    print("🚀 Starting Automated Retraining Pipeline...")

    # 0. Preprocess new and old data
    print("--- 0. Preprocessing ---")
    subprocess.run(["python", "ai/preprocess.py"], check=True)

    # 1. Data quality gate
    print("--- 1. Data Quality Check ---")
    subprocess.run(["python", "ai/data_quality_report.py"], check=True)
    with open(QUALITY_REPORT, "r") as f:
        quality = json.load(f)
    coverage = float(quality.get("label_coverage_percent", 0.0))
    if coverage < MIN_LABEL_COVERAGE_PERCENT:
        raise RuntimeError(
            f"Label coverage too low ({coverage:.2f}%). "
            f"Need >= {MIN_LABEL_COVERAGE_PERCENT:.2f}% before training."
        )
    
    train_cmd = ["python", "ai/train.py"]
    cv_folds = int(os.getenv("CV_FOLDS", "0"))
    target_sensitivity = os.getenv("TARGET_SENSITIVITY")
    force_retrain = os.getenv("FORCE_RETRAIN", "0").strip().lower() in {"1", "true", "yes"}
    cleanup_after_success = os.getenv("CLEAN_LOCAL_DATA_AFTER_SUCCESS", "0").strip().lower() in {"1", "true", "yes"}
    if cv_folds >= 2:
        train_cmd.extend(["--cv-folds", str(cv_folds)])
    if target_sensitivity:
        train_cmd.extend(["--target-sensitivity", target_sensitivity])

    current_state = compute_data_fingerprint(cv_folds, target_sensitivity)
    if (not force_retrain) and should_skip_training(current_state):
        print("--- 2. Training ---")
        print("Data and training settings unchanged. Skipping training and ONNX export.")
    else:
        # 2. Re-train the model
        print("--- 2. Training ---")
        subprocess.run(train_cmd, check=True)

        # 3. Export to ONNX
        print("--- 3. Exporting to ONNX ---")
        subprocess.run(["python", "ai/export_onnx.py"], check=True)
        save_training_state(current_state)
    
    # 4. Final Verification
    if os.path.exists("ai/heart_sound_model.onnx"):
        print("✅ Pipeline Success: New model ready for deployment.")
        if cleanup_after_success:
            print("--- 5. Cleanup Local Training Data ---")
            cleanup_local_training_data()
        # In production, add a shell command here to deploy to AWS Lambda/S3
    else:
        print("❌ Pipeline Failed: ONNX model not found.")

if __name__ == "__main__":
    start_time = time.time()
    run_retrain_pipeline()
    duration = (time.time() - start_time) / 60
    print(f"Total Pipeline Duration: {duration:.2f} minutes")
