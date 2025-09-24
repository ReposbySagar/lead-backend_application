# Lead Qualification Backend

An AI-powered lead qualification system that combines rule-based scoring with Google Gemini models to evaluate prospect buying intent. Built with Spring Boot, this backend service provides REST APIs for managing offers, uploading leads, and scoring prospects based on their likelihood to purchase.

## 🚀 Features

- **Offer Management**: Create and manage product/service offers with value propositions and ideal use cases
- **Lead Upload**: Bulk upload leads via CSV files with validation and error handling
- **Hybrid Scoring System**: 
  - Rule-based scoring (max 50 points) based on role, industry, and data completeness
  - AI-powered scoring (max 50 points) using Google Gemini models for intent classification
- **Results Export**: Export scored leads as CSV files
- **Real-time Analytics**: Track scoring progress and lead statistics
- **Comprehensive API**: RESTful endpoints with proper error handling and validation

## 🏗️ Architecture

The system follows a layered architecture pattern:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controllers   │    │    Services     │    │   Repositories  │
│                 │    │                 │    │                 │
│ • OfferController│    │ • OfferService  │    │ • OfferRepo     │
│ • LeadController │────│ • LeadService   │────│ • LeadRepo      │
│ • ScoringController│  │ • ScoringService│    │                 │
│ • ResultsController│  │ • RuleScoring   │    │                 │
│                 │    │ • GeminiService │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   External APIs │
                       │                 │
                       │ • Google Gemini │
                       └─────────────────┘
```

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Google Gemini API key
- Git

## 🛠️ Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd lead-qualification-backend
```

### 2. Configure Environment Variables

Create a `.env` file in the root directory or set environment variables:

```bash
export GEMINI_API_KEY=your-gemini-api-key-here
export GEMINI_API_BASE=https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent
```

### 3. Update Application Configuration

Edit `src/main/resources/application.yml` and update the Gemini configuration:

```yaml
gemini:
  api:
    key: ${GEMINI_API_KEY:your-gemini-api-key-here}
    base-url: ${GEMINI_API_BASE:https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent}
    model: gemini-pro
    max-tokens: 150
    temperature: 0.3
```

### 4. Build and Run

```bash
# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 5. Verify Installation

Check the health endpoint:
```bash
curl http://localhost:8080/actuator/health
```

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

### 1. Offer Management

#### Create Offer
```bash
POST /api/offer
Content-Type: application/json

{
  "name": "AI Outreach Automation",
  "valueProps": ["24/7 outreach", "6x more meetings"],
  "idealUseCases": ["B2B SaaS mid-market"]
}
```

**Response:**
```json
{
  "id": 1,
  "name": "AI Outreach Automation",
  "valueProps": ["24/7 outreach", "6x more meetings"],
  "idealUseCases": ["B2B SaaS mid-market"],
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

#### Get Latest Offer
```bash
GET /api/offer/latest
```

### 2. Lead Management

#### Upload Leads (CSV)
```bash
POST /api/leads/upload
Content-Type: multipart/form-data

# Form data with file field named 'file'
# CSV format: name,role,company,industry,location,linkedin_bio
```

**Example CSV:**
```csv
name,role,company,industry,location,linkedin_bio
Ava Patel,Head of Growth,FlowMetrics,SaaS,San Francisco,Growth leader with 8+ years in B2B SaaS
John Smith,CEO,TechCorp,Software,New York,Serial entrepreneur in enterprise software
```

**Response:**
```json
{
  "message": "Upload completed. 2 successful, 0 failed.",
  "totalLeads": 2,
  "successfulUploads": 2,
  "failedUploads": 0,
  "errors": [],
  "uploadedAt": "2024-01-15T10:35:00"
}
```

#### Get All Leads
```bash
GET /api/leads
```

#### Get Unscored Leads
```bash
GET /api/leads/unscored
```

### 3. Scoring Pipeline

#### Score All Unscored Leads
```bash
POST /api/score
```

**Response:**
```json
{
  "message": "Scoring completed. 2 leads scored successfully, 0 failed.",
  "totalLeads": 2,
  "successfulScores": 2,
  "failedScores": 0,
  "scoredAt": "2024-01-15T10:40:00"
}
```

#### Score Specific Lead
```bash
POST /api/score/{leadId}
```

### 4. Results Retrieval

#### Get Scored Results
```bash
GET /api/results
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Ava Patel",
    "role": "Head of Growth",
    "company": "FlowMetrics",
    "industry": "SaaS",
    "location": "San Francisco",
    "linkedinBio": "Growth leader with 8+ years in B2B SaaS",
    "ruleScore": 40,
    "aiScore": 50,
    "score": 90,
    "intent": "High",
    "reasoning": "Rule-based scoring breakdown: Role (decision maker) +20, Industry (exact match) +20. Total rule score: 40/50. Strong fit for B2B SaaS automation tools based on growth leadership role and industry experience.",
    "isScored": true
  }
]
```

#### Get High Intent Leads
```bash
GET /api/results/high
```

#### Export Results as CSV
```bash
GET /api/results/export
```

### 5. Analytics

#### Get Lead Statistics
```bash
GET /api/leads/stats
```

**Response:**
```json
{
  "totalLeads": 10,
  "scoredLeads": 8,
  "unscoredLeads": 2,
  "highIntentLeads": 3
}
```

## 🧮 Scoring Logic

### Rule-Based Scoring (Max 50 Points)

#### Role Relevance (Max 20 Points)
- **Decision Maker (+20)**: CEO, CTO, CFO, COO, President, Founder, Director, Head of, VP, Chief, Owner
- **Influencer (+10)**: Manager, Senior Manager, Lead, Team Lead, Principal, Senior, Architect
- **Other (+0)**: All other roles

#### Industry Match (Max 20 Points)
- **Exact Match (+20)**: SaaS, Software, Technology, IT, Cloud, FinTech, EdTech, etc.
- **Adjacent Match (+10)**: Consulting, Marketing, E-commerce, Financial Services, Healthcare
- **No Match (+0)**: All other industries

#### Data Completeness (Max 10 Points)
- **Complete Data (+10)**: All 6 fields present (name, role, company, industry, location, linkedin_bio)
- **Incomplete Data (+0)**: Missing any required fields

### AI-Powered Scoring (Max 50 Points)

The system uses Google Gemini model to analyze each prospect and classify their buying intent:

- **High Intent (+50)**: Strong likelihood to purchase based on role, industry fit, and pain points
- **Medium Intent (+30)**: Moderate likelihood with some qualifying factors
- **Low Intent (+10)**: Limited likelihood or poor fit

#### AI Prompt Strategy

The AI analysis considers:
1. Role relevance and decision-making authority
2. Industry fit with product's ideal use cases
3. Company size and growth stage indicators
4. Pain points mentioned in bio that align with value propositions
5. Overall likelihood to purchase this type of solution

### Final Score Calculation

```
Final Score = Rule Score + AI Score (Max 100 points)
```

**Intent Classification:**
- **High Intent**: 70-100 points
- **Medium Intent**: 40-69 points  
- **Low Intent**: 0-39 points

## 🧪 Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn verify
```

### Manual Testing with cURL

1. **Create an offer:**
```bash
curl -X POST http://localhost:8080/api/offer \
  -H "Content-Type: application/json" \
  -d '{
    "name": "AI Outreach Automation",
    "valueProps": ["24/7 outreach", "6x more meetings"],
    "idealUseCases": ["B2B SaaS mid-market"]
  }'
```

2. **Upload leads:**
```bash
curl -X POST http://localhost:8080/api/leads/upload \
  -F "file=@sample_leads.csv"
```

3. **Score leads:**
```bash
curl -X POST http://localhost:8080/api/score
```

4. **Get results:**
```bash
curl http://localhost:8080/api/results
```

## 🐳 Docker Deployment

### Build Docker Image
```bash
docker build -t lead-qualification-backend .
```

### Run with Docker
```bash
docker run -p 8080:8080 \
  -e GEMINI_API_KEY=your-api-key \
  -e GEMINI_API_BASE:https://generativelanguage.googleapis.com/v1/models/gemini-1.5-pro:generateContent
  lead-qualification-backend
```

## ☁️ Cloud Deployment

### Deploy to Render

1. Connect your GitHub repository to Render
2. Create a new Web Service
3. Set environment variables:
   - `GEMINI_API_KEY`: Your Google Gemini API key
   - `GEMINI_API_BASE:https://generativelanguage.googleapis.com/v1/models/gemini-1.5-pro:generateContent`
   - `SPRING_PROFILES_ACTIVE`: production
4. Deploy with build command: `mvn clean package`
5. Start command: `java -jar target/lead-qualification-backend-1.0.0.jar`

### Deploy to Railway

1. Connect your GitHub repository to Railway
2. Set environment variables in Railway dashboard
3. Railway will automatically detect the Spring Boot application
4. Deploy and get your public URL

## 📁 Project Structure

```
lead-qualification-backend/
├── src/
│   ├── main/
│   │   ├── java/com/leadqualification/
│   │   │   ├── config/           # Configuration classes
│   │   │   ├── controller/       # REST controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── exception/       # Exception handling
│   │   │   ├── repository/      # Data repositories
│   │   │   ├── service/         # Business logic
│   │   │   ├── util/            # Utility classes
│   │   │   └── LeadQualificationApplication.java
│   │   └── resources/
│   │       ├── application.yml   # Application configuration
│   │       └── static/          # Static resources
│   └── test/                    # Unit and integration tests
├── uploads/                     # File upload directory
├── Dockerfile                   # Docker configuration
├── pom.xml                     # Maven dependencies
└── README.md                   # This file
```

## 🔧 Configuration

### Application Properties

Key configuration options in `application.yml`:

```yaml
# Server configuration
server:
  port: 8080

# Database configuration (H2 for development)
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: password

# OpenAI configuration
gemini:
  api:
    key: ${GEMINI_API_KEY}
    model: gemini-1.5-pro
    max-tokens: 150
    temperature: 0.3

# File upload limits
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

# Scoring configuration
scoring:
  rules:
    role:
      decision-maker: 20
      influencer: 10
    industry:
      exact-match: 20
      adjacent-match: 10
    data-completeness: 10
```

## 🚨 Error Handling

The API provides comprehensive error handling with structured error responses:

```json
{
  "code": "RESOURCE_NOT_FOUND",
  "message": "Lead not found with ID: 123",
  "status": 404,
  "timestamp": "2024-01-15T10:30:00"
}
```

Common error codes:
- `RESOURCE_NOT_FOUND` (404): Resource doesn't exist
- `VALIDATION_FAILED` (400): Request validation errors
- `INVALID_ARGUMENT` (400): Invalid request parameters
- `FILE_SIZE_EXCEEDED` (413): File upload too large
- `INTERNAL_SERVER_ERROR` (500): Unexpected server errors

## 📊 Monitoring

### Health Checks
```bash
GET /actuator/health
```

### Metrics
```bash
GET /actuator/metrics
```

### Application Info
```bash
GET /actuator/info
```

## 🔐 Security Considerations

- Google Gemini API key should be stored as environment variables
- CORS is configured to allow all origins for development
- Input validation is implemented for all endpoints
- File upload size limits are enforced
- SQL injection protection via JPA/Hibernate

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For issues and questions:
1. Check the error logs in the console
2. Verify your OpenAI API key is valid
3. Ensure all required environment variables are set
4. Review the API documentation for correct request formats

## 🔄 Version History

- **v1.0.0**: Initial release with core functionality
  - Offer management
  - Lead upload and processing
  - Hybrid scoring system
  - Results export
  - REST API endpoints

---

**Built with ❤️ using Spring Boot, Google Gemini, and modern Java practices.**

