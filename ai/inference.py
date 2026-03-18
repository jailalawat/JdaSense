import onnxruntime
import numpy as np
import librosa
import scipy.signal as signal
import os

# Constants matching training/mobile app
SAMPLE_RATE = 8000
N_MELS = 128
HOP_LENGTH = 512
FMIN = 20
FMAX = 600

def butter_bandpass(lowcut, highcut, fs, order=5):
    nyquist = 0.5 * fs
    low = lowcut / nyquist
    high = highcut / nyquist
    b, a = signal.butter(order, [low, high], btype='band')
    return b, a

def apply_bandpass_filter(data, lowcut=25, highcut=400, fs=8000, order=5):
    b, a = butter_bandpass(lowcut, highcut, fs, order=order)
    y = signal.lfilter(b, a, data)
    return y

def preprocess_audio(file_path):
    """
    Exact same preprocessing as used in training.
    """
    # 1. Load and resample
    y, sr = librosa.load(file_path, sr=SAMPLE_RATE)
    
    # 2. Filter
    y_filtered = apply_bandpass_filter(y, fs=SAMPLE_RATE)
    
    # 3. Take the first 5 seconds
    samples_per_segment = 5 * SAMPLE_RATE
    if len(y_filtered) < samples_per_segment:
        # Pad with zeros if shorter
        y_filtered = np.pad(y_filtered, (0, samples_per_segment - len(y_filtered)))
    else:
        # Take first 5s
        y_filtered = y_filtered[:samples_per_segment]
        
    # 4. Mel-Spectrogram
    spec = librosa.feature.melspectrogram(
        y=y_filtered, sr=SAMPLE_RATE, n_mels=N_MELS, hop_length=HOP_LENGTH, fmin=FMIN, fmax=FMAX
    )
    spec_dB = librosa.power_to_db(spec, ref=np.max)
    
    # 5. Normalize (Z-score)
    spec_norm = (spec_dB - np.mean(spec_dB)) / (np.std(spec_dB) + 1e-6)
    
    # 6. Prepare for ResNet (1, 3, H, W)
    # Add channel dimension and repeat for 3 channels
    spec_final = spec_norm[np.newaxis, np.newaxis, ...].repeat(3, axis=1)
    
    return spec_final.astype(np.float32)

def predict(file_path, model_path='ai/heart_sound_model.onnx'):
    """
    Runs end-to-end inference on a single file.
    """
    if not os.path.exists(model_path):
        return {"error": "Model file not found. Export to ONNX first."}

    # Preprocess
    input_data = preprocess_audio(file_path)

    # Run ONNX inference
    ort_session = onnxruntime.InferenceSession(model_path)
    ort_inputs = {ort_session.get_inputs()[0].name: input_data}
    ort_outs = ort_session.run(None, ort_inputs)
    
    # Softmax on outputs
    logits = ort_outs[0]
    exp_logits = np.exp(logits - np.max(logits))
    probs = exp_logits / np.sum(exp_logits)
    
    # Map to result
    prediction_idx = np.argmax(probs)
    result = "Normal" if prediction_idx == 0 else "Anomaly Detected"
    confidence = float(np.max(probs))
    
    return {
        "result": result,
        "is_anomaly": bool(prediction_idx == 1),
        "confidence": confidence
    }

if __name__ == "__main__":
    # Test with a dummy path or first file found in processed
    test_file = 'mobile/heart_sound.wav' # Example path
    if os.path.exists(test_file):
        print(f"Predicting for {test_file}...")
        print(predict(test_file))
    else:
        print(f"Test file {test_file} not found. Use a real .wav file.")
