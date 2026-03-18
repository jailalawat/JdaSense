import os
import boto3
import json
import pandas as pd
from shutil import copyfile

# Constants
S3_BUCKET = "jdasense-recordings"
LOW_CONFIDENCE_THRESHOLD = 0.7
LOCAL_RAW_DIR = "ai/data/raw/flywheel"
LABEL_STUDIO_EXPORT_DIR = "ai/data/labels"

def download_low_confidence_data(metadata_file):
    """
    Downloads recordings from S3 where the model's confidence was low.
    """
    s3 = boto3.client('s3')
    if not os.path.exists(LOCAL_RAW_DIR):
        os.makedirs(LOCAL_RAW_DIR)
        
    with open(metadata_file, 'r') as f:
        metadata = json.load(f)
        
    count = 0
    for record in metadata:
        if record['confidence'] < LOW_CONFIDENCE_THRESHOLD:
            # Download from S3
            s3_key = record['s3_key']
            local_path = os.path.join(LOCAL_RAW_DIR, os.path.basename(s3_key))
            s3.download_file(S3_BUCKET, s3_key, local_path)
            count += 1
            
    print(f"Downloaded {count} low-confidence recordings for labeling.")

def ingest_labeled_data(label_studio_json):
    """
    Ingests re-labeled data from Label Studio into the main training set.
    """
    with open(label_studio_json, 'r') as f:
        new_labels = json.load(f)
        
    # Assume CSV structure: file_path, label
    master_labels_path = 'ai/data/master_labels.csv'
    if os.path.exists(master_labels_path):
        master_df = pd.read_csv(master_labels_path)
    else:
        master_df = pd.DataFrame(columns=['file_path', 'label'])
        
    rows = []
    for item in new_labels:
        file_name = os.path.basename(item['file_upload'])
        new_label = item['annotations'][0]['result'][0]['value']['choices'][0]
        
        # 0 = Normal, 1 = Anomaly
        label_idx = 0 if new_label == "Normal" else 1
        rows.append({'file_path': file_name, 'label': label_idx})
        
    new_df = pd.DataFrame(rows)
    master_df = pd.concat([master_df, new_df]).drop_duplicates(subset='file_path', keep='last')
    master_df.to_csv(master_labels_path, index=False)
    
    print(f"Ingested {len(rows)} new expert-labeled records into master_labels.csv.")

if __name__ == "__main__":
    # Example usage (to be run periodically)
    # download_low_confidence_data('ai/logs/predictions_log.json')
    # ingest_labeled_data('ai/data/labels/export.json')
    pass
