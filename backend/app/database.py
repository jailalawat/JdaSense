import os
import boto3
from boto3.dynamodb.conditions import Key, Attr
import uuid
from datetime import datetime

# Fetch from environment variables with fallbacks
USERS_TABLE_NAME = os.getenv("USERS_TABLE", "jdasense-users")
AUDIT_TABLE_NAME = os.getenv("AUDIT_TABLE", "jdasense-audit")

dynamodb = boto3.resource("dynamodb")
users_table = dynamodb.Table(USERS_TABLE_NAME)
audit_table = dynamodb.Table(AUDIT_TABLE_NAME)

def get_user_by_email(email: str):
    response = users_table.get_item(Key={"email": email})
    return response.get("Item")

def create_user(email: str, password_hash: str, role: str, hospital_id: str = None, name: str = ""):
    user = {
        "email": email,
        "password_hash": password_hash,
        "role": role,
        "hospital_id": hospital_id or "GLOBAL",
        "name": name,
        "is_deleted": False,
        "created_at": datetime.utcnow().isoformat()
    }
    users_table.put_item(Item=user)
    return user

def soft_delete_user(email: str):
    users_table.update_item(
        Key={"email": email},
        UpdateExpression="set is_deleted = :d",
        ExpressionAttributeValues={":d": True}
    )

def get_all_users(hospital_id: str = None, include_deleted: bool = False):
    # Scan is okay for small apps; in prod, use secondary indexes
    response = users_table.scan()
    items = response.get("Item", response.get("Items", []))
    
    filtered_items = []
    for item in items:
        if not include_deleted and item.get("is_deleted", False):
            continue
        if hospital_id and item.get("hospital_id") != hospital_id:
            continue
        filtered_items.append(item)
    return filtered_items

def log_audit(actor_email: str, action: str, target_id: str, hospital_id: str = None):
    log_entry = {
        "log_id": str(uuid.uuid4()),
        "actor_email": actor_email,
        "action": action,
        "target_id": target_id,
        "hospital_id": hospital_id or "GLOBAL",
        "timestamp": datetime.utcnow().isoformat()
    }
    audit_table.put_item(Item=log_entry)

def get_audit_logs(hospital_id: str = None):
    response = audit_table.scan()
    items = response.get("Items", [])
    if hospital_id:
        return [item for item in items if item.get("hospital_id") == hospital_id]
    return sorted(items, key=lambda x: x["timestamp"], reverse=True)
