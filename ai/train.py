import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import Dataset, DataLoader
import torchvision.models as models
import numpy as np
import os
from sklearn.model_selection import train_test_split
from tqdm import tqdm

# Constants
BATCH_SIZE = 32
LEARNING_RATE = 1e-4
EPOCHS = 20
DEVICE = torch.device("cuda" if torch.cuda.is_available() else "cpu")

class HeartSoundDataset(Dataset):
    def __init__(self, file_paths, labels, transform=None):
        self.file_paths = file_paths
        self.labels = labels
        self.transform = transform

    def __len__(self):
        return len(self.file_paths)

    def __getitem__(self, idx):
        # Load .npy spectrogram
        spec = np.load(self.file_paths[idx])
        
        # Normalize (Z-score)
        spec = (spec - np.mean(spec)) / (np.std(spec) + 1e-6)
        
        # Add channel dimension (1, H, W)
        spec = spec[np.newaxis, ...]
        
        # Convert to tensor
        spec_tensor = torch.from_numpy(spec).float()
        
        # Repeat channels to match ResNet 3-channel input if needed, 
        # or modify first layer. Let's repeat for simplicity.
        spec_tensor = spec_tensor.repeat(3, 1, 1)
        
        label = torch.tensor(self.labels[idx]).long()
        
        return spec_tensor, label

def create_model(num_classes=2):
    # Use ResNet18
    model = models.resnet18(weights=models.ResNet18_Weights.IMAGENET1K_V1)
    
    # Modify final fully connected layer
    num_ftrs = model.fc.in_features
    model.fc = nn.Linear(num_ftrs, num_classes)
    
    return model

def train_model(model, train_loader, val_loader, criterion, optimizer, epochs):
    model.to(DEVICE)
    
    for epoch in range(epochs):
        model.train()
        running_loss = 0.0
        correct = 0
        total = 0
        
        print(f"Epoch {epoch+1}/{epochs}")
        for inputs, labels in tqdm(train_loader):
            inputs, labels = inputs.to(DEVICE), labels.to(DEVICE)
            
            optimizer.zero_grad()
            outputs = model(inputs)
            loss = criterion(outputs, labels)
            loss.backward()
            optimizer.step()
            
            running_loss += loss.item()
            _, predicted = outputs.max(1)
            total += labels.size(0)
            correct += predicted.eq(labels).sum().item()
            
        train_acc = 100. * correct / total
        print(f"Train Loss: {running_loss/len(train_loader):.4f} | Acc: {train_acc:.2f}%")
        
        # Validation
        model.eval()
        val_loss = 0.0
        correct = 0
        total = 0
        with torch.no_grad():
            for inputs, labels in val_loader:
                inputs, labels = inputs.to(DEVICE), labels.to(DEVICE)
                outputs = model(inputs)
                loss = criterion(outputs, labels)
                
                val_loss += loss.item()
                _, predicted = outputs.max(1)
                total += labels.size(0)
                correct += predicted.eq(labels).sum().item()
                
        val_acc = 100. * correct / total
        print(f"Val Loss: {val_loss/len(val_loader):.4f} | Acc: {val_acc:.2f}%")
        
    print("Training Complete.")

def main():
    processed_dir = 'ai/data/processed'
    
    # Mock label logic for setup (In production, parse from .csv labels)
    # 0 = Normal, 1 = Abnormal
    file_paths = []
    labels = []
    
    for f in os.listdir(processed_dir):
        if f.endswith('.npy'):
            path = os.path.join(processed_dir, f)
            file_paths.append(path)
            # Placeholder: Assign random labels if actual labels not parsed yet
            labels.append(np.random.randint(0, 2))
            
    if not file_paths:
        print("No processed data found. Run preprocess.py first.")
        return

    # Split data
    train_files, val_files, train_labels, val_labels = train_test_split(
        file_paths, labels, test_size=0.2, random_state=42
    )
    
    train_dataset = HeartSoundDataset(train_files, train_labels)
    val_dataset = HeartSoundDataset(val_files, val_labels)
    
    train_loader = DataLoader(train_dataset, batch_size=BATCH_SIZE, shuffle=True)
    val_loader = DataLoader(val_dataset, batch_size=BATCH_SIZE, shuffle=False)
    
    model = create_model()
    
    # Weighted loss for imbalance
    # (Assuming 80% Normal, 20% Abnormal -> weights [0.25, 1.0])
    weights = torch.tensor([1.0, 4.0]).to(DEVICE)
    criterion = nn.CrossEntropyLoss(weight=weights)
    
    optimizer = optim.Adam(model.parameters(), lr=LEARNING_RATE)
    
    train_model(model, train_loader, val_loader, criterion, optimizer, EPOCHS)
    
    # Save the model
    torch.save(model.state_dict(), 'ai/heart_sound_model.pth')
    print("Model saved to ai/heart_sound_model.pth")

if __name__ == "__main__":
    main()
