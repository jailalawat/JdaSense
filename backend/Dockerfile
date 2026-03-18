FROM public.ecr.aws/lambda/python:3.10

# Install system dependencies for librosa/audio processing
RUN yum install -y mesa-libGL-devel libsndfile

# Copy requirements and install
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code
COPY app/ ${LAMBDA_TASK_ROOT}/app/

# Create model directory and copy the trained model
# Note: Ensure you have your .onnx file in backend/model/
RUN mkdir -p ${LAMBDA_TASK_ROOT}/model
COPY model/heart_sound_model.onnx* ${LAMBDA_TASK_ROOT}/model/

# Set the handler to the Mangum object in main.py
CMD ["app.main.handler"]
