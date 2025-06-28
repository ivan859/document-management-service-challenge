#!/bin/bash

# Script to upload document 10 times with different users and tags
# Note: The specified PDF file "~/Documents/hai_ai_index_report_2025 2.pdf" doesn't exist
# Please update the FILE_PATH variable below with the correct path to your PDF file

BASE_URL="http://localhost:8081"
ENDPOINT="/documents/upload"
FILE_PATH="$HOME/Downloads/hai_ai_index_report_2025 2.pdf"

# Check if file exists
if [ ! -f "$FILE_PATH" ]; then
    echo "Error: File not found at $FILE_PATH"
    echo "Please update the FILE_PATH variable in this script with the correct path to your PDF file"
    exit 1
fi

# Arrays of different users and tags for variety
USERS=("alice.smith" "bob.johnson" "charlie.brown" "diana.prince" "eve.adams" "frank.miller" "grace.hopper" "henry.ford" "iris.watson" "jack.sparrow")
TAG_SETS=(
    "report,finance,2025"
    "analysis,ai,technology"
    "quarterly,business,strategy"
    "research,development,innovation"
    "market,trends,forecast"
    "operations,efficiency,improvement"
    "customer,feedback,survey"
    "product,launch,marketing"
    "compliance,regulatory,audit"
    "training,education,development"
)
DOCUMENT_NAMES=(
    "AI Index Report 2025"
    "Technology Analysis Report"
    "Business Strategy Document"
    "Innovation Research Paper"
    "Market Forecast Analysis"
    "Operations Improvement Plan"
    "Customer Survey Results"
    "Product Launch Strategy"
    "Compliance Audit Report"
    "Training Development Guide"
)

echo "Starting document upload process..."
echo "File to upload: $FILE_PATH"
echo "Target endpoint: $BASE_URL$ENDPOINT"
echo ""

# Upload document 10 times with different metadata in parallel
upload_document() {
    local i=$1
    local USER="${USERS[$i]}"
    local TAGS="${TAG_SETS[$i]}"
    local DOC_NAME="${DOCUMENT_NAMES[$i]}"
    
    echo "Upload #$((i+1)): User=$USER, Name=$DOC_NAME, Tags=$TAGS"
    
    # Split tags into individual form data fields
    IFS=',' read -ra TAG_ARRAY <<< "$TAGS"
    
    # Build curl command with form data
    CURL_CMD="curl -X POST \"$BASE_URL$ENDPOINT\""
    CURL_CMD="$CURL_CMD -F \"user=$USER\""
    CURL_CMD="$CURL_CMD -F \"name=$DOC_NAME\""
    
    # Add each tag as separate form field
    for tag in "${TAG_ARRAY[@]}"; do
        CURL_CMD="$CURL_CMD -F \"tags=$tag\""
    done
    
    CURL_CMD="$CURL_CMD -F \"file=@$FILE_PATH\""
    
    # Execute the upload
    echo "Executing: $CURL_CMD"
    eval $CURL_CMD
    
    RESPONSE_CODE=$?
    if [ $RESPONSE_CODE -eq 0 ]; then
        echo "✓ Upload #$((i+1)) completed successfully"
    else
        echo "✗ Upload #$((i+1)) failed with code $RESPONSE_CODE"
    fi
}

# Start all uploads in parallel
echo "Starting 10 parallel uploads..."
COUNT=0
for i in {0..9}; do
    upload_document $i &
    COUNT=$((COUNT+1))
done

# Wait for all background processes to complete while showing memory metrics
echo "Waiting for uploads to complete..."

# Function to get and display heap memory in MB
show_heap_memory() {
    local response=$(curl -s "http://localhost:8081/actuator/metrics/jvm.memory.used?tag=area:heap" 2>/dev/null)
    if [ $? -eq 0 ] && [ -n "$response" ]; then
        local value=$(echo "$response" | grep -o '"value":[^,}]*' | cut -d':' -f2 | tr -d ' "')
        if [ -n "$value" ]; then
            # Convert scientific notation to integer using printf
            local bytes=$(printf "%.0f" "$value")
            local mb=$((bytes / 1024 / 1024))
            echo "Heap memory used: ${mb} MB (${bytes} bytes)"
        else
            echo "Could not parse heap memory value"
        fi
    else
        echo "Could not fetch heap memory metrics"
    fi
}

# Show memory usage while waiting
while jobs %1 2>/dev/null; do
    show_heap_memory
    sleep 2
done

wait

echo "Document upload process completed!"
echo "Total uploads attempted: $COUNT"