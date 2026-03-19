import json
import os
import shutil
import tarfile
import time
import urllib.request
import zipfile
from pathlib import Path
from urllib.error import HTTPError, URLError

import wfdb

ROOT_DIR = Path(__file__).resolve().parent
RAW_DATA_DIR = ROOT_DIR / "data" / "raw"
VERIFIED_SOURCES_FILE = ROOT_DIR / "verified_sources.json"
SYNC_STATE_FILE = ROOT_DIR / "data" / "sync_state.json"


def load_verified_sources():
    with VERIFIED_SOURCES_FILE.open("r", encoding="utf-8") as f:
        return json.load(f).get("sources", [])


def list_wav_relative_paths(source_dir: Path):
    if not source_dir.exists():
        return set()
    return {str(p.relative_to(source_dir)) for p in source_dir.rglob("*.wav")}


def download_dataset_wfdb(db_name: str, dest_dir: Path):
    dest_dir.mkdir(parents=True, exist_ok=True)
    print(f"Downloading WFDB dataset '{db_name}' to {dest_dir}...")
    try:
        wfdb.dl_database(db_name, dl_dir=str(dest_dir), overwrite=False)
        print(f"WFDB download complete: {db_name}")
    except Exception as exc:
        print(f"WFDB download warning for {db_name}: {exc}")


def download_file(url: str, dest_path: Path) -> bool:
    try:
        print(f"Downloading from {url}")
        req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
        dest_path.parent.mkdir(parents=True, exist_ok=True)
        with urllib.request.urlopen(req, timeout=300) as response, dest_path.open("wb") as out:
            shutil.copyfileobj(response, out, length=1024 * 1024)
        return True
    except (URLError, HTTPError, TimeoutError) as exc:
        print(f"Failed: {exc}")
        return False


def extract_archive(archive_path: Path, output_dir: Path) -> bool:
    try:
        if archive_path.suffix == ".zip":
            with zipfile.ZipFile(archive_path, "r") as zf:
                zf.extractall(output_dir)
        elif archive_path.suffixes[-2:] == [".tar", ".gz"]:
            with tarfile.open(archive_path, "r:gz") as tf:
                tf.extractall(output_dir)
        else:
            return False
        return True
    except Exception as exc:
        print(f"Extraction failed for {archive_path.name}: {exc}")
        return False


def ensure_metadata_files(source_dir: Path, metadata_files: list):
    for item in metadata_files or []:
        url = item.get("url")
        dest_rel = item.get("dest_rel")
        if not url or not dest_rel:
            continue

        dest_path = source_dir / dest_rel
        if dest_path.exists():
            continue

        ok = download_file(url, dest_path)
        if ok:
            print(f"Downloaded metadata: {dest_path}")


def ensure_archives_extracted(source_dir: Path, archive_urls: list):
    for url in archive_urls or []:
        filename = os.path.basename(url)
        archive_path = source_dir / filename
        if not archive_path.exists():
            ok = download_file(url, archive_path)
            if not ok:
                continue

        extracted = extract_archive(archive_path, source_dir)
        if extracted:
            print(f"Extracted {filename}")


def verify_source(source_dir: Path, required_markers: list) -> bool:
    if not required_markers:
        return source_dir.exists()

    for marker in required_markers:
        if (source_dir / marker).exists():
            return True
    return False


def download_source(source: dict):
    if not source.get("verified", False):
        print(f"Skipping unverified source: {source.get('name')}")
        return {"source": source.get("name"), "skipped": True}
    if not source.get("enabled", True):
        print(f"Skipping disabled source: {source.get('name')}")
        return {"source": source.get("name"), "skipped": True}

    source_name = source.get("name")
    source_dir = RAW_DATA_DIR / source_name
    source_dir.mkdir(parents=True, exist_ok=True)

    before_wavs = list_wav_relative_paths(source_dir)

    source_type = source.get("type")
    if source_type == "wfdb":
        wfdb_name = source.get("wfdb_name", source_name)
        download_dataset_wfdb(wfdb_name, source_dir)

    ensure_metadata_files(source_dir, source.get("metadata_files", []))
    ensure_archives_extracted(source_dir, source.get("archive_urls", []))

    after_wavs = list_wav_relative_paths(source_dir)
    new_wavs = sorted(after_wavs - before_wavs)

    ok = verify_source(source_dir, source.get("required_markers", []))
    print(f"Verification for {source_name}: {ok} | new wav files: {len(new_wavs)}")

    return {
        "source": source_name,
        "verified": ok,
        "wav_count_before": len(before_wavs),
        "wav_count_after": len(after_wavs),
        "new_wav_count": len(new_wavs),
        "new_wavs": new_wavs,
    }


def verify_download(sources: list):
    print("--- Verification ---")
    all_ok = True

    for source in sources:
        if not source.get("enabled", True) or not source.get("verified", False):
            continue
        source_name = source.get("name")
        source_dir = RAW_DATA_DIR / source_name
        ok = verify_source(source_dir, source.get("required_markers", []))
        print(f"{source_name}: {ok}")
        all_ok = all_ok and ok

    if not all_ok:
        print("⚠️ Some source metadata/files are missing. Label integrity may be incomplete.")
        print("Run data_quality_report.py after fixing download issues.")


def write_sync_state(source_reports, sync_started_at_epoch: int, sync_finished_at_epoch: int):
    total_new = sum(int(r.get("new_wav_count", 0)) for r in source_reports if not r.get("skipped"))
    state = {
        "sync_started_at_epoch": sync_started_at_epoch,
        "sync_finished_at_epoch": sync_finished_at_epoch,
        "sources": source_reports,
        "total_new_wav_files": total_new,
        "any_new_data": total_new > 0,
    }
    SYNC_STATE_FILE.parent.mkdir(parents=True, exist_ok=True)
    SYNC_STATE_FILE.write_text(json.dumps(state, indent=2), encoding="utf-8")
    print(f"Sync state written to {SYNC_STATE_FILE}")


def main():
    RAW_DATA_DIR.mkdir(parents=True, exist_ok=True)
    sources = load_verified_sources()

    sync_started = int(time.time())
    source_reports = []

    for source in sources:
        source_reports.append(download_source(source))

    verify_download(sources)
    sync_finished = int(time.time())
    write_sync_state(source_reports, sync_started, sync_finished)


if __name__ == "__main__":
    main()
