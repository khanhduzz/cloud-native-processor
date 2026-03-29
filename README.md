# Cloud-Native Polyglot Processor (2026)

A local-first AWS environment demonstrating a distributed image processing system using .NET, Java, and LocalStack.

## Architecture Overview

The system follows a decoupled microservice pattern:

1. Infrastructure: LocalStack (Docker) provides S3, SQS, and DynamoDB locally.
2. Web API (.NET 9): Handles file uploads, stores metadata in PostgreSQL, and sends messages to SQS.
3. Queue (AWS SQS): Acts as a buffer between the API and the background processor.
4. Worker (Java 21): Consumes messages from SQS, processes images, and updates the database status.

## Tech Stack

- Backend API: .NET 9 (C#) + Entity Framework Core
- Background Worker: Java 21 + Spring Boot
- Database: PostgreSQL 16
- Cloud Simulation: LocalStack (S3, SQS)
- Containerization: Docker & Docker Compose

## Getting Started

### 1. Prerequisites

- Docker Desktop
- .NET 9 SDK
- Java 21 JDK
- AWS CLI & awslocal (pip install awscli-local)

### 2. Environment Setup

Create a .env file in the root directory:

```bash
# Infrastructure
LOCALSTACK_AUTH_TOKEN=your_token_here
DB_USER=dev_user
DB_PASSWORD=dev_password
DB_NAME=cloud_native_db

# AWS Config
S3_BUCKET_NAME=wedding-uploads
SQS_QUEUE_NAME=image-processing-queue

```

### 3. Running the Infrastructure

From the root folder, start the services:

```bash
# Infrastructure
# Start Cloud + Database
docker compose -f infrastructure/docker-compose.yml up -d
docker compose --env-file .env -f infrastructure/docker-compose.yml up -d

# Check health
curl http://localhost:4566/_localstack/health

```

## AWS CLI Commands (awslocal)

#### S3 Storage

```bash
# Create bucket
awslocal s3 mb s3://cloud-native

# List files
awslocal s3 ls s3://cloud-native

```

Testing the S3 in awslocal

```
https://app.localstack.cloud/inst/default/resources/s3

# Upload a test file
awslocal s3 cp my_test_file.txt s3://cloud-native/test.txt

# List the bucket again to see it
awslocal s3 ls s3://cloud-native/
```

#### SQS Messaging

```bash
# Create queue
awslocal sqs create-queue --queue-name image-processing-queue

# Check message count
awslocal sqs get-queue-attributes --queue-url http://localhost:4566/000000000000/image-processing-queue --attribute-names ApproximateNumberOfMessages

```

#### PostgreSQL

```bash
# Access DB via terminal
docker exec -it cloud_postgres psql -U dev_user -d cloud_native_db

```
