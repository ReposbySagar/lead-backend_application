#!/bin/bash

set -e

echo "🚀 Lead Qualification Backend Deployment Script"
echo "=============================================="

if [ -z "$GEMINI_API_KEY" ]; then
    echo "❌ Error: GEMINI_API_KEY environment variable is not set"
    echo "Please set your Gemini API key:"
    echo "export GEMINI_API_KEY=your-gemini-api-key-here"
    exit 1
fi

echo "✅ Gemini API key found"

echo "📦 Building the application..."
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Build successful"
else
    echo "❌ Build failed"
    exit 1
fi

echo "🐳 Building Docker image..."
docker build -t lead-qualification-backend .

if [ $? -eq 0 ]; then
    echo "✅ Docker image built successfully"
else
    echo "❌ Docker build failed"
    exit 1
fi

echo "🚀 Starting the application..."
docker run -d \
    --name lead-qualification-backend \
    -p 8080:8080 \
    -e GEMINI_API_KEY="$GEMINI_API_KEY" \
    -e GEMINI_API_BASE="${GEMINI_API_BASE:-https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent}" \
    -v $(pwd)/uploads:/app/uploads \
    lead-qualification-backend

if [ $? -eq 0 ]; then
    echo "✅ Application started successfully"
    echo "🌐 Application is running at: http://localhost:8080"
    echo "📊 Health check: http://localhost:8080/actuator/health"
    echo "📚 API documentation: See README.md"
else
    echo "❌ Failed to start application"
    exit 1
fi

echo ""
echo "🎉 Deployment completed successfully!"
echo ""
echo "Next steps:"
echo "1. Test the health endpoint: curl http://localhost:8080/actuator/health"
echo "2. Create an offer: curl -X POST http://localhost:8080/api/offer -H 'Content-Type: application/json' -d '{\"name\":\"Test Offer\",\"valueProps\":[\"Value 1\"],\"idealUseCases\":[\"Use Case 1\"]}'"
echo "3. Upload leads: curl -X POST http://localhost:8080/api/leads/upload -F 'file=@sample_leads.csv'"
echo "4. Score leads: curl -X POST http://localhost:8080/api/score"
echo "5. Get results: curl http://localhost:8080/api/results"

