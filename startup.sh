#!/usr/local/bin/bash
# Check if Docker is already installed
if ! command -v docker &> /dev/null
then
    # Docker is not installed, install it
    echo "Docker not found, installing..."
    curl -fsSL https://get.docker.com -o get-docker.sh
    sudo sh get-docker.sh
    rm get-docker.sh
    echo "Docker installed successfully."
fi

docker compose up -d
