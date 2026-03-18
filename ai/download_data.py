import wfdb
import os

def download_dataset():
    # The record name on PhysioNet
    dataset_name = 'circor-heart-sound-1.0.3'
    
    # Destination directory
    dest_dir = './data'
    if not os.path.exists(dest_dir):
        os.makedirs(dest_dir)
    
    print(f"Downloading {dataset_name} to {dest_dir}...")
    
    # Downloading the dataset using wfdb
    wfdb.dl_database('circor-heart-sound', dl_dir=dest_dir)
    
    print("Download complete.")

if __name__ == "__main__":
    download_dataset()
