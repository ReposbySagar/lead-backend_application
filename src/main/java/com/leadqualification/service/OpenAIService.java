//package com.leadqualification.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.leadqualification.entity.IntentLevel;
//import com.leadqualification.entity.Lead;
//import com.leadqualification.entity.Offer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class OpenAIService {
//
//    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);
//
//    @Value("${openai.api.key}")
//    private String apiKey;
//
//    @Value("${openai.api.base-url}")
//    private String baseUrl;
//
//    @Value("${openai.api.model:gpt-3.5-turbo}")
//    private String model;
//
//    @Value("${openai.api.max-tokens:150}")
//    private int maxTokens;
//
//    @Value("${openai.api.temperature:0.3}")
//    private double temperature;
//
//    private final RestTemplate restTemplate;
//    private final ObjectMapper objectMapper;
//
//    public OpenAIService() {
//        this.restTemplate = new RestTemplate();
//        this.objectMapper = new ObjectMapper();
//    }
//
//    public AIScoreResult scoreLeadIntent(Lead lead, Offer offer) {
//        logger.info("Scoring lead intent using AI for: {}", lead.getName());
//
//        try {
//            String prompt = buildPrompt(lead, offer);
//            String response = callOpenAI(prompt);
//            return parseAIResponse(response);
//        } catch (Exception e) {
//            logger.error("Failed to score lead intent for: {}", lead.getName(), e);
//            return new AIScoreResult(IntentLevel.LOW, "AI scoring failed: " + e.getMessage());
//        }
//    }
//
//    private String buildPrompt(Lead lead, Offer offer) {
//        StringBuilder prompt = new StringBuilder();
//
//        prompt.append("You are a lead qualification expert. Analyze this prospect and determine their buying intent for the given product/offer.\n\n");
//
//        prompt.append("PRODUCT/OFFER:\n");
//        prompt.append("Name: ").append(offer.getName()).append("\n");
//        prompt.append("Value Propositions: ").append(String.join(", ", offer.getValueProps())).append("\n");
//        prompt.append("Ideal Use Cases: ").append(String.join(", ", offer.getIdealUseCases())).append("\n\n");
//
//        prompt.append("PROSPECT:\n");
//        prompt.append("Name: ").append(lead.getName()).append("\n");
//        prompt.append("Role: ").append(lead.getRole() != null ? lead.getRole() : "Not specified").append("\n");
//        prompt.append("Company: ").append(lead.getCompany() != null ? lead.getCompany() : "Not specified").append("\n");
//        prompt.append("Industry: ").append(lead.getIndustry() != null ? lead.getIndustry() : "Not specified").append("\n");
//        prompt.append("Location: ").append(lead.getLocation() != null ? lead.getLocation() : "Not specified").append("\n");
//        prompt.append("LinkedIn Bio: ").append(lead.getLinkedinBio() != null ? lead.getLinkedinBio() : "Not specified").append("\n\n");
//
//        prompt.append("TASK:\n");
//        prompt.append("Classify this prospect's buying intent as High, Medium, or Low based on:\n");
//        prompt.append("1. Role relevance and decision-making authority\n");
//        prompt.append("2. Industry fit with the product's ideal use cases\n");
//        prompt.append("3. Company size and growth stage indicators\n");
//        prompt.append("4. Pain points mentioned in bio that align with value props\n");
//        prompt.append("5. Overall likelihood to purchase this type of solution\n\n");
//
//        prompt.append("RESPONSE FORMAT:\n");
//        prompt.append("Intent: [High/Medium/Low]\n");
//        prompt.append("Reasoning: [1-2 sentences explaining your classification]\n\n");
//
//        prompt.append("Be concise and focus on the most relevant factors for this specific product-prospect match.");
//
//        return prompt.toString();
//    }
//
//    private String callOpenAI(String prompt) throws Exception {
//        String url = baseUrl + "/chat/completions";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(apiKey);
//
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("model", model);
//        requestBody.put("max_tokens", maxTokens);
//        requestBody.put("temperature", temperature);
//
//        Map<String, String> message = new HashMap<>();
//        message.put("role", "user");
//        message.put("content", prompt);
//        requestBody.put("messages", List.of(message));
//
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
//
//        logger.debug("Sending request to OpenAI API");
//        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
//            String content = jsonResponse.path("choices").get(0).path("message").path("content").asText();
//            logger.debug("Received response from OpenAI API");
//            return content;
//        } else {
//            throw new RuntimeException("OpenAI API call failed with status: " + response.getStatusCode());
//        }
//    }
//
//    private AIScoreResult parseAIResponse(String response) {
//        logger.debug("Parsing AI response: {}", response);
//
//        try {
//            String[] lines = response.split("\n");
//            IntentLevel intent = IntentLevel.LOW;
//            String reasoning = "AI analysis completed";
//
//            for (String line : lines) {
//                line = line.trim();
//                if (line.toLowerCase().startsWith("intent:")) {
//                    String intentValue = line.substring(line.indexOf(":") + 1).trim();
//                    intent = IntentLevel.fromString(intentValue);
//                } else if (line.toLowerCase().startsWith("reasoning:")) {
//                    reasoning = line.substring(line.indexOf(":") + 1).trim();
//                }
//            }
//
//            if (reasoning.equals("AI analysis completed") && response.length() > 50) {
//                reasoning = response.substring(0, Math.min(200, response.length())).trim();
//                if (reasoning.contains("\n")) {
//                    reasoning = reasoning.substring(0, reasoning.indexOf("\n")).trim();
//                }
//            }
//
//            logger.debug("Parsed intent: {}, reasoning: {}", intent, reasoning);
//            return new AIScoreResult(intent, reasoning);
//
//        } catch (Exception e) {
//            logger.warn("Failed to parse AI response, using default values", e);
//            return new AIScoreResult(IntentLevel.LOW, "Failed to parse AI response: " + response);
//        }
//    }
//
//    public static class AIScoreResult {
//        private final IntentLevel intent;
//        private final String reasoning;
//
//        public AIScoreResult(IntentLevel intent, String reasoning) {
//            this.intent = intent;
//            this.reasoning = reasoning;
//        }
//
//        public IntentLevel getIntent() {
//            return intent;
//        }
//
//        public String getReasoning() {
//            return reasoning;
//        }
//
//        public int getScore() {
//            return intent.getAiScore();
//        }
//    }
//}
//
