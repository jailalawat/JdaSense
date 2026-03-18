import os
import librosa
import numpy as np
import scipy.signal as signal
from tqdm import tqdm

# Constants matching mobile app capture
SAMPLE_RATE = 8000
DURATION = 5  # 5-second segments
N_MELS = 128
HOP_LENGTH = 512
FMIN = 20
FMAX = 600

def butter_bandpass(lowcut, highcut, fs, order=5):
    """
    Creates a Butterworth bandpass filter.
    """
    nyquist = 0.5 * fs
    low = lowcut / nyquist
    high = highcut / nyquist
    b, a = signal.butter(order, [low, high], btype='band')
    return b, a

def apply_bandpass_filter(data, lowcut=25, highcut=400, fs=8000, order=5):
    """
    Applies the bandpass filter to audio data.
    """
    b, a = butter_bandpass(lowcut, highcut, fs, order=order)
    y = signal.lfilter(b, a, data)
    return y

def generate_mel_spectrogram(y, sr):
    """
    Generates a Mel-Spectrogram from audio data.
    """
    S = librosa.feature.melspectrogram(
        y=y, sr=sr, n_mels=N_MELS, hop_length=HOP_LENGTH, fmin=FMIN, fmax=FMAX
    )
    S_dB = librosa.power_to_db(S, ref=np.max)
    return S_dB

def process_file(file_path, output_dir):
    """
    Loads, filters, segments, and saves Mel-Spectrograms from a single file.
    """
    try:
        # 1. Load and resample
        y, sr = librosa.load(file_path, sr=SAMPLE_RATE)
        
        # 2. Apply Band-pass Filter
        y_filtered = apply_bandpass_filter(y, fs=SAMPLE_RATE)
        
        # 3. Segment into 5-second windows with 50% overlap
        samples_per_segment = DURATION * SAMPLE_RATE
        overlap = samples_per_segment // 2
        
        filename = os.path.basename(file_path).replace('.wav', '')
        
        for i, start in enumerate(range(0, len(y_filtered) - samples_per_segment + 1, overlap)):
            segment = y_filtered[start:start + samples_per_segment]
            
            # 4. Generate Mel-Spectrogram
            spec = generate_mel_spectrogram(segment, SAMPLE_RATE)
            
            # 5. Save as .npy for fast loading during training
            out_name = f"{filename}_seg{i}.npy"
            np.save(os.path.join(output_dir, out_name), spec)
            
    except Exception as e:
        print(f"Error processing {file_path}: {e}")

def main():
    raw_dir = 'ai/data/raw'
    processed_dir = 'ai/data/processed'
    
    if not os.path.exists(processed_dir):
        os.makedirs(processed_dir)
        
    # Search for all .wav files in the raw data directory
    wav_files = []
    for root, _, files in os.walk(raw_dir):
        for f in files:
            if f.endswith('.wav'):
                wav_files.append(os.path.join(root, f))
                
    print(f"Found {len(wav_files)} files to process.")
    
    for f in tqdm(wav_files):
        process_file(f, processed_dir)
        
    print("Preprocessing complete.")

if __name__ == "__main__":
    main()
