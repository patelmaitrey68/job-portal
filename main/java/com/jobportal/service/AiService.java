package com.jobportal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
public class AiService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateContent(String prompt) {
        if ("MOCK".equals(geminiApiKey) || geminiApiKey == null || geminiApiKey.isEmpty()) {
            return getMockedResponse(prompt);
        }

        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> parts = new HashMap<>();
            parts.put("text", prompt);
            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(parts));
            requestBody.put("contents", List.of(content));

            if (prompt.contains("JSON") || prompt.contains("json")) {
                Map<String, Object> generationConfig = new HashMap<>();
                generationConfig.put("responseMimeType", "application/json");
                requestBody.put("generationConfig", generationConfig);
            }

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode root = objectMapper.readTree(response.getBody());
                String text = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
                if (text.startsWith("```json")) {
                    text = text.substring(7);
                    if (text.endsWith("```")) {
                        text = text.substring(0, text.length() - 3);
                    }
                }
                return text.trim();
            } else {
                throw new RuntimeException("Gemini API Error: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return getMockedResponse(prompt);
        }
    }

    private String getMockedResponse(String prompt) {
        if (prompt.contains("enhance this job description")) {
            return "{\"title\": \"Senior Developer\", \"description\": \"We are seeking a highly skilled and experienced Developer to join our fast-paced engineering team.\\n\\nOur tech stack relies heavily on modern frameworks, and we expect candidates to be comfortable working autonomously in an agile environment.\", \"requirements\": [\"5+ years of experience in Software Engineering\", \"Expertise in modern JavaScript and React\", \"Strong understanding of RESTful APIs\"], \"responsibilities\": [\"Design and deploy scalable backend systems\", \"Collaborate with frontend engineers on API contracts\", \"Participate in code reviews\"]}";
        } else if (prompt.contains("draft a professional cover letter")) {
            return "Dear Hiring Manager,\\n\\nI am writing to express my enthusiastic interest in the open position at your company. With my strong background in software development and my proficiency in the required skills, I am confident in my ability to make an immediate impact on your team.\\n\\nThank you for your time and consideration.\\n\\nSincerely,\\n[Your Name]";
        } else if (prompt.contains("Analyze this resume")) {
            return "{\"matchScore\": 85, \"skillsMatch\": [\"React\", \"Node.js\", \"Java\"], \"aiFeedback\": \"Candidate is a strong 85% match. They possess excellent foundational skills in React and Java. However, they are missing some cloud deployment experience (e.g., AWS or Docker) which was requested in the job description. If cloud experience isn't a hard requirement, they are highly recommended for an interview.\"}";
        } else if (prompt.contains("generate 3 technical interview questions")) {
            return "[\"1. Can you describe a time when you had to optimize a slow-performing API?\", \"2. How do you handle state management in large React applications?\", \"3. Given your lack of AWS experience, how would you approach learning our deployment pipeline quickly?\"]";
        }
        return "Mocked AI Response";
    }
}
