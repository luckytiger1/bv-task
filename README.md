# BetVictor Text Processing System

Two Spring Boot applications that process text and store results using Kafka and PostgreSQL.

## What It Does

**Processing App** (port 8080):
- API endpoint: `GET /betvictor/text?p=3`
- Fetches hipster text from Hipsum API
- Finds most frequent word and calculates statistics
- Sends results to Kafka

**Repository App** (port 8081):
- Consumes messages from Kafka
- Stores results in PostgreSQL database
- API endpoint: `GET /betvictor/history` (last 10 results)

## How to Run

### 1. Start Infrastructure
```bash
docker-compose up -d
```
This starts Kafka, PostgreSQL, and Kafka UI.

### 2. Run Applications
```bash
# Terminal 1 - Repository App
cd repository-app
mvn spring-boot:run

# Terminal 2 - Processing App  
cd processing-app
mvn spring-boot:run
```

### 3. Test It
```bash
# Process text
curl "http://localhost:8080/betvictor/text?p=3"

# View history
curl "http://localhost:8081/betvictor/history"
```

## Monitoring
- **Kafka UI**: http://localhost:8090
- **Applications**: Check logs in terminals

## Prerequisites
- Java 21+
- Maven 3.6+
- Docker & Docker Compose