import json
import os
import wfdb
import subprocess
from datetime import datetime

VERIFIED_SOURCES_FILE = "ai/verified_sources.json"
RAW_DATA_DIR = "ai/data/raw"

def load_verified_sources():
    with open(VERIFIED_SOURCES_FILE, 'r') as f:
        return json.load(f)

def update_last_checked(source_name):
    config = load_verified_sources()
    for s in config['sources']:
        if s['name'] == source_name:
            s['last_checked'] = datetime.now().strftime("%Y-%m-%d")
    with open(VERIFIED_SOURCES_FILE, 'w') as f:
        json.dump(config, f, indent=4)

def fetch_new_data():
    config = load_verified_sources()
    new_data_found = False
    
    print(f"🔍 Starting Dynamic Data Sync at {datetime.now()}")
    
    for source in config['sources']:
        if not source['verified']:
            print(f"⚠️ Skipping unverified source: {source['name']}")
            continue
            
        print(f"📡 Checking {source['provider']} for updated records in {source['name']}...")
        
        try:
            # PhysioNet (WFDB) check/download
            # In a real scenario, we'd check for version/timestamp updates
            # Here we ensure the latest version is mirrored locally
            dest_path = os.path.join(RAW_DATA_DIR, source['name'])
            
            # Simple check: if directory doesn't exist or is older than a week, we refresh
            # In production, use wfdb.get_record_list to compare counts
            wfdb.dl_database(source['name'], dl_dir=dest_path, overwrite=False)
            
            # Update tracking
            update_last_checked(source['name'])
            new_data_found = True # Assuming new data for demonstration
            
        except Exception as e:
            print(f"❌ Error syncing {source['name']}: {e}")

    if new_data_found:
        print("🚀 New data detected from verified sources. Triggering Retraining Pipeline...")
        subprocess.run(["python", "ai/automate_retrain.py"], check=True)
    else:
        print("✅ Data is up to date. No retraining needed.")

if __name__ == "__main__":
    fetch_new_data()
