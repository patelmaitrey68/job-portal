package com.jobportal.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.jobportal.service.AiService;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiController {

    @Autowired
    private AiService aiService;

    @PostMapping("/enhance-job")
    public ResponseEntity<String> enhanceJobDescription(@RequestBody Map<String, String> payload) {
        String description = payload.get("description");
        String prompt = "You are an expert technical recruiter. Please enhance this job description and output ONLY valid JSON. " +
                        "The JSON must have the following keys: 'title' (string), 'description' (string), " +
                        "'requirements' (array of strings), 'responsibilities' (array of strings). " +
                        "Here is the raw description to enhance: " + description;
        
        String jsonResult = aiService.generateContent(prompt);
        return ResponseEntity.ok(jsonResult);
    }

    @PostMapping("/draft-cover-letter")
    public ResponseEntity<Map<String, String>> draftCoverLetter(@RequestBody Map<String, String> payload) {
        String jobDescription = payload.get("jobDescription");
        String skills = payload.get("skills");
        
        String prompt = "You are an expert career coach. Please draft a professional cover letter for an applicant " +
                        "applying to the following job description: '" + jobDescription + "'. " +
                        "The applicant has the following skills: '" + skills + "'. " +
                        "Write a 3-paragraph cover letter. Do not include any JSON formatting, just return the plain text cover letter.";
        
        String letter = aiService.generateContent(prompt);
        return ResponseEntity.ok(Map.of("coverLetter", letter));
    }

    @GetMapping("/interview-questions/{applicationId}")
    public ResponseEntity<String> generateInterviewQuestions(@PathVariable String applicationId) {
        // We will pass the application ID, but for the prompt we can just make it generic or fetch the app details.
        // For simplicity, we'll assume the client sends the context, or we fetch it.
        // Let's modify this to accept the context in a POST for easier integration.
        return ResponseEntity.badRequest().body("Use POST /generate-interview-questions");
    }
    
    @PostMapping("/generate-interview-questions")
    public ResponseEntity<String> generateInterviewQuestionsPost(@RequestBody Map<String, String> payload) {
        String jobDescription = payload.get("jobDescription");
        String applicantSkills = payload.get("applicantSkills");
        
        String prompt = "You are a hiring manager preparing for an interview. " +
                        "The job description is: '" + jobDescription + "'. " +
                        "The candidate's skills are: '" + applicantSkills + "'. " +
                        "Based on the gaps between the candidate's skills and the job description, " +
                        "generate 3 technical interview questions to test their knowledge. " +
                        "Output ONLY a JSON array of 3 strings.";
                        
        String jsonResult = aiService.generateContent(prompt);
        return ResponseEntity.ok(jsonResult);
    }

    @PostMapping("/analyze-profile")
    public ResponseEntity<String> analyzeProfile(@RequestBody Map<String, String> payload) {
        String jobDescription = payload.get("jobDescription");
        String applicantSkills = payload.get("applicantSkills");
        
        String prompt = "You are an expert technical recruiter and career coach. " +
                        "The job description is: '" + jobDescription + "'. " +
                        "The candidate's skills are: '" + applicantSkills + "'. " +
                        "Analyze the candidate's fit for this role and return ONLY valid JSON. " +
                        "The JSON must have the following keys: " +
                        "'strengths' (array of strings), " +
                        "'weaknesses' (array of strings), " +
                        "'missingSkills' (array of strings), " +
                        "'improvementSuggestions' (array of strings).";
                        
        String jsonResult = aiService.generateContent(prompt);
        return ResponseEntity.ok(jsonResult);
    }

    @PostMapping("/mock-interview")
    public ResponseEntity<String> mockInterview(@RequestBody Map<String, Object> payload) {
        String jobDescription = (String) payload.get("jobDescription");
        String applicantSkills = (String) payload.get("applicantSkills");
        
        // transcript is an array of message objects like {role: 'user', content: '...'}
        // For simplicity, we just pass the stringified transcript.
        Object transcriptObj = payload.get("transcript");
        String transcript = transcriptObj != null ? transcriptObj.toString() : "[]";
        
        String prompt = "You are a technical interviewer for this job: '" + jobDescription + "'. " +
                        "The candidate has these skills: '" + applicantSkills + "'. " +
                        "Here is the interview transcript so far: " + transcript + " " +
                        "Act as the interviewer. Evaluate the candidate's last answer (if any), give a suggested better answer if they struggled, and ask the next question. " +
                        "If you have asked 3 questions already, conclude the interview and give overall feedback. " +
                        "Output ONLY valid JSON with these keys: " +
                        "'evaluation' (string: feedback on the last answer, empty if first turn), " +
                        "'suggestedAnswer' (string: how they should have answered, empty if first turn), " +
                        "'nextQuestion' (string: the question you are asking now), " +
                        "'isComplete' (boolean: true if interview is over).";
                        
        String jsonResult = aiService.generateContent(prompt);
        return ResponseEntity.ok(jsonResult);
    }
}
