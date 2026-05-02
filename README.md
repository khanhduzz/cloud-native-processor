# Cloud-Native Polyglot Processor (2026)

A local-first AWS environment demonstrating a distributed image processing system using .NET, Java, and LocalStack.

## Architecture Overview

The system follows a decoupled microservice pattern:

1. Infrastructure: LocalStack (Docker) provides S3, SQS, and DynamoDB locally.
2. Web API (.NET 10): Handles file uploads, stores metadata in PostgreSQL, and sends messages to SQS.
3. Queue (AWS SQS): Acts as a buffer between the API and the background processor.
4. Worker (Java 21): Consumes messages from SQS, processes images, and updates the database status.

## Tech Stack

- Backend API: .NET 10 (C#) + Entity Framework Core
- Background Worker: Java 21 + Spring Boot
- Database: PostgreSQL 16
- Cloud Simulation: LocalStack (S3, SQS)
- Containerization: Docker & Docker Compose

## Structure

```
/cloud-native-processor
  ├── /infra              <-- Docker & LocalStack configs
  │     └── docker-compose.yml
  ├── /src
  │     ├── /Backend.API   <-- .NET Web API
  │     └── /Worker.Java   <-- Java SQS Processor
  ├── .gitignore
  └── README.md
```

## Getting Started

### 1. Prerequisites

- Docker Desktop
- .NET 10 SDK
- Java 21 JDK
- AWS CLI & awslocal (pip install awscli-local)

### 2. Environment Setup

Create a .env file in the root directory:

```bash
# Infrastructure
LOCALSTACK_AUTH_TOKEN=your_token_here
DEBUG=debug_mode
PERSISTENCE=persistence_mode

DB_HOST=dev_host
DB_PORT=dev_port
DB_USER=dev_user
DB_PASSWORD=dev_password
DB_NAME=db_name

# AWS Config
AWS_REGION=us-east-1
AWS_ACCESS_KEY_ID=key_id
AWS_SECRET_ACCESS_KEY=access_key
S3_SERVICE_URL=url
S3_BUCKET_NAME=bucket_name
SQS_QUEUE_NAME=sqs_name

```

### 3. Running the Infrastructure

From the root folder, start the services:

```bash
# Infrastructure
# Start Cloud + Database
docker compose --env-file .env -f infrastructure/docker-compose.yml up -d

# Check health
curl {S3_SERVICE_URL}/_localstack/health

```

## AWS CLI Commands (awslocal)

#### S3 Storage

```bash
# Create bucket
awslocal s3 mb s3://{S3_BUCKET_NAME}

# List files
awslocal s3 ls s3://{S3_BUCKET_NAME}

```

Testing the S3 in awslocal

```
https://app.localstack.cloud/inst/default/resources/s3

# Upload a test file
awslocal s3 cp my_test_file.txt s3://{S3_BUCKET_NAME}/test.txt

# List the bucket again to see it
awslocal s3 ls s3://{S3_BUCKET_NAME}/
```

#### SQS Messaging

```bash
# Create queue
awslocal sqs create-queue --queue-name {SQS_QUEUE_NAME}

# Check message count
awslocal sqs get-queue-attributes --queue-url {S3_SERVICE_URL}/000000000000/{SQS_QUEUE_NAME} --attribute-names ApproximateNumberOfMessages

```

#### PostgreSQL

```bash
# Access DB via terminal
docker exec -it cloud_postgres psql -U {DB_USER} -d {DB_NAME}

```

## 🏗️ Project Architecture

```mermaid
flowchart TD
    %% Main Flow
    Client[Client]
    --> Upload[Upload Image<br/>.NET API]

    Upload --> S3_1[Upload Image to S3<br/>LocalStack S3]
    Upload --> DB_NET[Save Metadata<br/>.NET PostgreSQL]

    S3_1 --> SQS[Send Message to SQS<br/>LocalStack SQS]

    SQS --> Consumer[SQS Consumer<br/>Java Service]

    Consumer --> Download[Download Image from S3]
    Download --> Resize[Resize Image]
    Resize --> S3_2[Upload Resized Image to S3]
    S3_2 --> DB_JAVA[Save Metadata<br/>Java PostgreSQL]

    DB_JAVA --> UI[Web UI<br/>View & Download Images]

    %% Infrastructure Layer
    subgraph Infrastructure
        direction TB
        LocalStack[LocalStack<br/> S3 + SQS]
        Postgres[(PostgreSQL)]
        PgAdmin[pgAdmin]
        Docker[Docker Compose]

        LocalStack --- Postgres
        LocalStack --- PgAdmin
    end

    %% Connections to Infrastructure
    S3_1 -.-> LocalStack
    SQS -.-> LocalStack
    Download -.-> LocalStack
    S3_2 -.-> LocalStack
    DB_NET -.-> Postgres
    DB_JAVA -.-> Postgres

    %% Styling
    classDef client fill:#4ade80,stroke:#166534,color:#166534
    classDef net fill:#512BD4,stroke:#fff,color:#fff
    classDef java fill:#007396,stroke:#fff,color:#fff
    classDef aws fill:#FF9900,stroke:#232F3E,color:#fff
    classDef db fill:#475569,stroke:#fff,color:#fff
    classDef ui fill:#8b5cf6,stroke:#fff,color:#fff

    class Client client
    class Upload,DB_NET net
    class Consumer,Download,Resize,S3_2,DB_JAVA,UI java
    class LocalStack,Docker aws
    class Postgres,DB_NET,DB_JAVA db
    class UI ui
```
