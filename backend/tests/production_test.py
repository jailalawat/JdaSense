import requests
import boto3
import os
import time

# Configuration
API_URL = "https://v0vo91g9da.execute-api.ap-south-1.amazonaws.com/Prod"
API_KEY = "YOUR_API_KEY_HERE" # Need to replace with real key
AUTH_TOKEN = "YOUR_JWT_TOKEN_HERE" # Need to replace with real token
S3_BUCKET = "jdasense-recordings"
DYNAMODB_TABLE = "jdasense-predictions"

def test_end_to_end_prediction(wav_path):
    print(f"🚀 Starting Production E2E Test with {wav_path}...")
    
    # 1. Call /predict
    url = f"{API_URL}/predict"
    headers = {
        "x-api-key": API_KEY,
        "Authorization": f"Bearer {AUTH_TOKEN}"
    }
    
    with open(wav_path, 'rb') as f:
        files = {'audio': (os.path.basename(wav_path), f, 'audio/wav')}
        response = requests.post(url, headers=headers, files=files)
        
    if response.status_code != 200:
        print(f"❌ API Failure: {response.status_code} - {response.text}")
        return
        
    result = response.json()
    record_id = result['record_id']
    print(f"✅ API Success: Result={result['result']}, RecordID={record_id}")
    
    # 2. Verify S3 Capture
    time.sleep(2) # Give AWS a second to settle
    s3 = boto3.client('s3')
    try:
        s3.head_object(Bucket=S3_BUCKET, Key=f"raw/{record_id}.wav")
        print(f"✅ S3 Capture Verified: raw/{record_id}.wav exists.")
    except:
        print(f"❌ S3 Capture Failed: File not found in bucket.")
        
    # 3. Verify DynamoDB Log
    dynamodb = boto3.resource('dynamodb', region_name='ap-south-1')
    table = dynamodb.Table(DYNAMODB_TABLE)
    db_response = table.get_item(Key={'record_id': record_id})
    
    if 'Item' in db_response:
        print(f"✅ DynamoDB Log Verified: Record {record_id} found.")
    else:
        print(f"❌ DynamoDB Log Failed: Record not found in table.")

if __name__ == "__main__":
    # This script is intended for manual trigger after providing real Auth data
    # test_end_to_end_prediction("path/to/real/heart_sound.wav")
    pass
