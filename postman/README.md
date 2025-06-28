# Clara Challenge API - Postman Collection

This folder contains Postman collection and environment files for testing the Document Management Service API.

## Files

- **Clara Challenge API.postman_collection.json**: Complete API collection with all endpoints
- **Clara Challenge.postman_environment.json**: Environment variables for easy configuration
- **README.md**: This documentation file

## Getting Started

### 1. Import Collection and Environment

1. Open Postman
2. Click "Import" button
3. Select both JSON files:
   - `Clara Challenge API.postman_collection.json`
   - `Clara Challenge.postman_environment.json`

### 2. Select Environment

1. In Postman, select "Clara Challenge" environment from the dropdown in the top-right corner
2. Verify the environment variables are loaded correctly

### 3. Configure Base URL

The default base URL is set to `http://localhost:8080`. If your service runs on a different URL or port:

1. Go to Environments → Clara Challenge
2. Modify the `baseUrl` variable value
3. Save the environment

## API Endpoints

### Health Check
- **GET** `/health` - Simple health check to verify service is running

### Document Management

#### Upload Document
- **POST** `/documents/upload` (multipart/form-data)
- Required parameters:
  - `user`: The user uploading the document
  - `name`: Document name
  - `file`: PDF file to upload
- Optional parameters:
  - `tags`: Document tags (can add multiple)

#### Search Documents
- **POST** `/document-management/search`
- Request body: JSON with optional filters
- Query parameters:
  - `page`: Page number (default: 0)
  - `size`: Page size (default: 10)
  - `sort`: Sort criteria (default: createdAt,desc)

#### Download Document
- **GET** `/document-management/download/{documentId}`
- Returns a presigned URL for document download

## Environment Variables

| Variable | Default Value | Description |
|----------|---------------|-------------|
| `baseUrl` | `http://localhost:8080` | API base URL |
| `sampleUser` | `john.doe` | Sample user for testing |
| `sampleDocumentName` | `sample-document.pdf` | Sample document name |
| `sampleTag1` | `important` | Sample tag |
| `sampleTag2` | `contract` | Sample tag |
| `sampleDocumentId` | `123e4567-e89b-12d3-a456-426614174000` | Sample document UUID |
| `defaultPage` | `0` | Default page for pagination |
| `defaultSize` | `10` | Default page size |
| `defaultSort` | `createdAt,desc` | Default sort order |

## Usage Tips

### Testing Upload Workflow

1. **Upload a Document**: Use "Upload Document" request
   - Select a PDF file in the `file` parameter
   - Modify user and document name as needed
   - Note: Only PDF files are accepted

2. **Search for Documents**: Use "Search Documents" request
   - Start with empty filters `{}` to see all documents
   - Use specific filters to narrow results
   - Check pagination parameters

3. **Get Document ID**: From search results, copy a document ID

4. **Download Document**: Use "Download Document" request
   - Replace `{{sampleDocumentId}}` with actual document ID
   - The response contains a presigned URL for download

### Error Testing

The collection includes test scenarios for common error cases:
- Missing required parameters (400 Bad Request)
- Non-existent document IDs (404 Not Found)
- Invalid file types (400 Bad Request)

### Sample Request Bodies

#### Search with Filters
```json
{
  "user": "john.doe",
  "name": "contract",
  "tags": ["important", "legal"]
}
```

#### Search All Documents
```json
{}
```

## Response Examples

### Search Response
```json
{
  "metadata": {
    "currentPage": 0,
    "itemsPerPage": 10,
    "currentItems": 5,
    "totalPages": 1,
    "totalItems": 5
  },
  "documents": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "user": "john.doe",
      "name": "sample-document.pdf",
      "tags": ["important", "contract"],
      "size": 1024576,
      "type": "application/pdf",
      "createdAt": "2024-01-15T10:30:00Z"
    }
  ]
}
```

### Download Response
```json
{
  "url": "https://minio.example.com/document-bucket/john.doe/sample-document.pdf?X-Amz-Algorithm=AWS4-HMAC-SHA256&..."
}
```

## Troubleshooting

### Common Issues

1. **Service Not Running**: Ensure the Document Management Service is running on the configured base URL
2. **File Upload Fails**: Verify you're uploading a PDF file and all required parameters are provided
3. **Document Not Found**: Make sure you're using a valid document ID from search results
4. **Environment Variables Not Working**: Check that "Clara Challenge" environment is selected

### Service Dependencies

Before testing, ensure these services are running:
- Document Management Service (Spring Boot application)
- PostgreSQL database
- MinIO object storage (via LocalStack or dedicated instance)

For detailed setup instructions, refer to the main project README.md file.