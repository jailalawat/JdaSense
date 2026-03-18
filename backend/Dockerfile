FROM public.ecr.aws/lambda/python:3.12

# Install system dependencies for librosa and compiling C++ wheels
# AL2023 uses 'dnf' instead of 'yum' (though yum works as a link)
RUN dnf install -y \
    mesa-libGL-devel \
    libsndfile \
    gcc-c++ \
    cmake \
    make \
    git

# Upgrade pip
RUN pip install --upgrade pip

# Copy requirements and install
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code
COPY app/ ${LAMBDA_TASK_ROOT}/app/

# Create model directory and copy the trained model
RUN mkdir -p ${LAMBDA_TASK_ROOT}/model
COPY model/heart_sound_model.onnx* ${LAMBDA_TASK_ROOT}/model/

# Set the handler to the Mangum object in main.py
CMD ["app.main.handler"]
