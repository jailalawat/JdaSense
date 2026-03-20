import argparse
import hashlib
import json
from pathlib import Path

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

RAW_DIR_DEFAULT = Path("ai/data/raw")
PROCESSED_DIR_DEFAULT = Path("ai/data/processed")
PREPROCESS_STATE_PATH = Path("ai/preprocess_state.json")
TRAINED_MANIFEST_PATH = Path("ai/trained_data_manifest.json")


def butter_bandpass(lowcut, highcut, fs, order=5):
    nyquist = 0.5 * fs
    low = lowcut / nyquist
    high = highcut / nyquist
    b, a = signal.butter(order, [low, high], btype="band")
    return b, a


def apply_bandpass_filter(data, lowcut=25, highcut=400, fs=8000, order=5):
    b, a = butter_bandpass(lowcut, highcut, fs, order=order)
    return signal.lfilter(b, a, data)


def generate_mel_spectrogram(y, sr):
    S = librosa.feature.melspectrogram(
        y=y, sr=sr, n_mels=N_MELS, hop_length=HOP_LENGTH, fmin=FMIN, fmax=FMAX
    )
    return librosa.power_to_db(S, ref=np.max)


def compute_expected_segments(sample_count: int) -> int:
    samples_per_segment = DURATION * SAMPLE_RATE
    overlap = samples_per_segment // 2
    if sample_count < samples_per_segment:
        return 0
    return 1 + (sample_count - samples_per_segment) // overlap


def _raw_wav_fingerprint(wav_files):
    hasher = hashlib.sha256()
    hasher.update(str(SAMPLE_RATE).encode("utf-8"))
    hasher.update(str(DURATION).encode("utf-8"))
    hasher.update(str(N_MELS).encode("utf-8"))
    hasher.update(str(HOP_LENGTH).encode("utf-8"))
    hasher.update(str(FMIN).encode("utf-8"))
    hasher.update(str(FMAX).encode("utf-8"))

    for p in wav_files:
        stat = p.stat()
        hasher.update(str(p).encode("utf-8"))
        hasher.update(str(stat.st_size).encode("utf-8"))
        hasher.update(str(int(stat.st_mtime)).encode("utf-8"))

    return hasher.hexdigest()


def load_trained_manifest(manifest_path: Path):
    if not manifest_path.exists():
        return {}
    try:
        payload = json.loads(manifest_path.read_text(encoding="utf-8"))
    except Exception:
        return {}
    return payload.get("sources", {})


def is_manifest_trained(file_path: Path, raw_dir: Path, trained_sources: dict) -> bool:
    try:
        rel = file_path.relative_to(raw_dir)
    except ValueError:
        return False

    parts = rel.parts
    if not parts:
        return False
    source_name = parts[0]
    source_rel = "/".join(parts[1:])
    trained_entries = trained_sources.get(source_name, [])
    return source_rel in trained_entries


def process_file(file_path: Path, output_dir: Path, force: bool = False):
    try:
        y, _ = librosa.load(str(file_path), sr=SAMPLE_RATE)

        filename = file_path.stem
        expected_segments = compute_expected_segments(len(y))
        if expected_segments <= 0:
            return {"processed": False, "skipped": True, "segments_written": 0}

        expected_paths = [output_dir / f"{filename}_seg{i}.npy" for i in range(expected_segments)]
        if not force and all(p.exists() for p in expected_paths):
            return {
                "processed": False,
                "skipped": True,
                "segments_written": 0,
                "segments_existing": expected_segments,
            }

        y_filtered = apply_bandpass_filter(y, fs=SAMPLE_RATE)
        samples_per_segment = DURATION * SAMPLE_RATE
        overlap = samples_per_segment // 2

        written = 0
        for i, start in enumerate(range(0, len(y_filtered) - samples_per_segment + 1, overlap)):
            segment = y_filtered[start : start + samples_per_segment]
            spec = generate_mel_spectrogram(segment, SAMPLE_RATE)
            out_name = f"{filename}_seg{i}.npy"
            np.save(output_dir / out_name, spec)
            written += 1

        return {"processed": True, "skipped": False, "segments_written": written}

    except Exception as e:
        print(f"Error processing {file_path}: {e}")
        return {"processed": False, "skipped": False, "segments_written": 0, "error": str(e)}


def parse_args():
    parser = argparse.ArgumentParser(description="Preprocess raw heart sounds into mel spectrogram segments")
    parser.add_argument("--raw-dir", default=str(RAW_DIR_DEFAULT))
    parser.add_argument("--processed-dir", default=str(PROCESSED_DIR_DEFAULT))
    parser.add_argument("--trained-manifest", default=str(TRAINED_MANIFEST_PATH))
    parser.add_argument("--force", action="store_true", help="Recreate spectrograms even if expected outputs exist")
    parser.add_argument(
        "--since-epoch",
        type=int,
        default=None,
        help="If set, preprocess only raw wav files modified at/after this epoch timestamp",
    )
    return parser.parse_args()


def main():
    args = parse_args()
    raw_dir = Path(args.raw_dir)
    processed_dir = Path(args.processed_dir)
    trained_manifest = Path(args.trained_manifest)
    processed_dir.mkdir(parents=True, exist_ok=True)

    wav_files = sorted(raw_dir.rglob("*.wav"))
    if args.since_epoch is not None:
        wav_files = [p for p in wav_files if int(p.stat().st_mtime) >= int(args.since_epoch)]
        print(f"Filtering raw files since epoch {args.since_epoch}.")
    trained_sources = load_trained_manifest(trained_manifest)
    if trained_sources:
        before_count = len(wav_files)
        wav_files = [p for p in wav_files if not is_manifest_trained(p, raw_dir, trained_sources)]
        print(f"Excluded {before_count - len(wav_files)} already-trained raw files using {trained_manifest}.")
    print(f"Found {len(wav_files)} files to process.")

    raw_fingerprint = _raw_wav_fingerprint(wav_files)
    if not args.force and PREPROCESS_STATE_PATH.exists():
        try:
            prev = json.loads(PREPROCESS_STATE_PATH.read_text(encoding="utf-8"))
        except Exception:
            prev = {}
        if prev.get("raw_fingerprint") == raw_fingerprint:
            print("Raw data unchanged since last preprocess. Skipping preprocessing.")
            return

    processed_files = 0
    skipped_files = 0
    total_written = 0

    for f in tqdm(wav_files):
        result = process_file(f, processed_dir, force=args.force)
        if result.get("processed"):
            processed_files += 1
        if result.get("skipped"):
            skipped_files += 1
        total_written += int(result.get("segments_written", 0))

    PREPROCESS_STATE_PATH.write_text(
        json.dumps(
            {
                "raw_fingerprint": raw_fingerprint,
                "wav_file_count": len(wav_files),
                "processed_files": processed_files,
                "skipped_files": skipped_files,
                "segments_written": total_written,
            },
            indent=2,
        ),
        encoding="utf-8",
    )

    print(
        "Preprocessing complete. "
        f"Processed files: {processed_files}, skipped files: {skipped_files}, new/updated segments: {total_written}."
    )


if __name__ == "__main__":
    main()
