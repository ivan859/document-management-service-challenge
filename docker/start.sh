#!/bin/bash

# Load environment variables from .env file
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
    echo "Loaded environment variables from .env"
else
    echo "Warning: .env file not found. Using default values from .exampleEnv"
    echo "Please copy .exampleEnv to .env and configure your values"
fi

# Start the containers
echo "Starting containers..."
docker-compose up -d

# Show container status
echo "Container status:"
docker-compose ps

echo "Services started successfully!"
echo "- PostgreSQL: localhost:5432"
echo "- MinIO API: localhost:9000"
echo "- MinIO Console: localhost:9001"
echo "- Document Management Service: localhost:${SERVER_PORT:-8080}"