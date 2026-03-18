import wfdb
import os

def download_dataset(db_name, dest_dir):
    """
    Downloads a dataset from PhysioNet using wfdb.
    """
    db_path = os.path.join(dest_dir, db_name)
    if not os.path.exists(db_path):
        os.makedirs(db_path)
    
    print(f"Downloading {db_name} to {db_path}...")
    try:
        wfdb.dl_database(db_name, dl_dir=db_path)
        print(f"Successfully downloaded {db_name}.")
    except Exception as e:
        print(f"Error downloading {db_name}: {e}")

def main():
    # Base destination directory
    raw_data_dir = os.path.join(os.path.dirname(__file__), 'data', 'raw')
    
    # 1. CirCor DigiScope (Primary - ~5,000 recordings)
    download_dataset('circor-heart-sound', raw_data_dir)
    
    # 2. PhysioNet CinC 2016 (Augmentation - Noisy/Real-world data)
    download_dataset('challenge-2016', raw_data_dir)

if __name__ == "__main__":
    main()
