FROM public.ecr.aws/lambda/python:3.12

# Install system dependencies for librosa and compiling C++ wheels
RUN dnf install -y \
    mesa-libGL-devel \
    libsndfile \
    gcc-c++ \
    cmake \
    make \
    git

# Set working directory to Lambda task root
WORKDIR ${LAMBDA_TASK_ROOT}

# Upgrade pip
RUN pip install --upgrade pip

# Copy and install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code and model
COPY app/ ./app/
COPY model/ ./model/

# Set the handler to the Mangum object
CMD ["app.main.handler"]
