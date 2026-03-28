# 💳 Payment Infrastructure Sandbox (2026)

This repository contains a local-first AWS environment for building and testing resilient payment processing systems. It uses **LocalStack** to simulate S3, SQS, and DynamoDB locally on macOS.

---

## 🏗️ The Big Picture

We are building a **Distributed Payment Pipeline** to handle high-volume financial transactions with a "Zero-Loss" architecture.

1. **Infrastructure:** LocalStack (Docker) provides the AWS services.
2. **Producer (.NET):** Simulates a POS Terminal or Cloud Gateway sending payment requests.
3. **Queue (SQS):** Acts as a buffer to ensure messages are never lost during high traffic.
4. **Consumer (Java):** Processes the payments, saves receipts to S3, and logs history to DynamoDB.

---

## 🚀 Getting Started

### 1. Prerequisites

- **Docker Desktop** (Required for the "Cloud" container)
- **AWS CLI v2** & **awslocal** (`pip install awscli-local` or `brew install localstack/tap/localstack-cli`)
- **LocalStack Auth Token** (Get yours at [app.localstack.cloud](https://app.localstack.cloud))

### 2. Environment Setup

Create a `.env` file in the root directory:

```bash
LOCALSTACK_AUTH_TOKEN=ls-your-token-here
```

# Start the cloud

docker compose up -d

# Stop the cloud (Keep data)

docker compose down

# Stop the cloud (Delete all data/volumes)

docker compose down -v

# View live container logs

docker compose logs -f localstack

# Check if container is running

docker ps

# Check status of AWS services (S3, SQS, etc.)

curl http://localhost:4566/\_localstack/health

# List all buckets

awslocal s3 ls

# Create a new bucket

awslocal s3 mb s3://payment-receipts-2026

# List files inside a bucket

awslocal s3 ls s3://payment-receipts-2026

# Delete a bucket (and all files inside)

awslocal s3 rb s3://payment-receipts-2026 --force

# List all queues

awslocal sqs list-queues

# Create a payment request queue

awslocal sqs create-queue --queue-name payment-request-queue

# View message count and attributes

awslocal sqs get-queue-attributes --queue-url http://localhost:4566/000000000000/payment-request-queue --attribute-names All

# Manually receive/peek at a message

awslocal sqs receive-message --queue-url http://localhost:4566/000000000000/payment-request-queue

# Delete a queue

awslocal sqs delete-queue --queue-url http://localhost:4566/000000000000/payment-request-queue

# List all tables

awslocal dynamodb list-tables

# Delete a table

awslocal dynamodb delete-table --table-name Payments History
