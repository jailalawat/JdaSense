import torch
import torch.nn as nn
import torchvision.models as models
import onnx
import onnxruntime
import numpy as np

def create_model(num_classes=2):
    model = models.resnet18()
    num_ftrs = model.fc.in_features
    model.fc = nn.Linear(num_ftrs, num_classes)
    return model

def export_to_onnx(model_path, onnx_path):
    # 1. Load the PyTorch model
    model = create_model()
    # Check if model exists before loading
    try:
        model.load_state_dict(torch.load(model_path, map_location='cpu'))
    except FileNotFoundError:
        print(f"Error: {model_path} not found. Using randomly initialized model for export demonstration.")
    
    model.eval()

    # 2. Create dummy input (Batch, Channel, Height, Width)
    # Mel-Spectrograms are 128 (N_MELS) x 79 (approx for 5s @ 8kHz)
    # We use 3 channels because we repeated them in training for ResNet
    dummy_input = torch.randn(1, 3, 128, 79)

    # 3. Export
    torch.onnx.export(
        model,
        dummy_input,
        onnx_path,
        export_params=True,
        opset_version=12,
        do_constant_folding=True,
        input_names=['input'],
        output_names=['output'],
        dynamic_axes={'input': {0: 'batch_size'}, 'output': {0: 'batch_size'}}
    )
    
    # Reload and save specifically without external data
    # (Sometimes torch.onnx.export creates external data if not careful)
    onnx_model = onnx.load(onnx_path)
    from onnx.external_data_helper import convert_model_to_external_data
    # We want the OPPOSITE of external data
    onnx.save_model(onnx_model, onnx_path, save_as_external_data=False)
    
    print(f"Model exported to {onnx_path}")

    # 4. Verify ONNX model
    onnx_model = onnx.load(onnx_path)
    onnx.checker.check_model(onnx_model)
    
    # Run a test inference
    ort_session = onnxruntime.InferenceSession(onnx_path)
    ort_inputs = {ort_session.get_inputs()[0].name: dummy_input.numpy()}
    ort_outs = ort_session.run(None, ort_inputs)
    
    print("ONNX verification complete. Inference successful.")

if __name__ == "__main__":
    export_to_onnx('ai/heart_sound_model.pth', 'ai/heart_sound_model.onnx')
