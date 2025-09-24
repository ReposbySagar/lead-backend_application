package com.leadqualification.controller;

import com.leadqualification.dto.LeadResponse;
import com.leadqualification.dto.LeadUploadResponse;
import com.leadqualification.service.LeadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LeadController {

    private static final Logger logger = LoggerFactory.getLogger(LeadController.class);

    private final LeadService leadService;

    @Autowired
    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @PostMapping("/leads/upload")
    public ResponseEntity<LeadUploadResponse> uploadLeads(@RequestParam("file") MultipartFile file) {
        logger.info("Received lead upload request: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new LeadUploadResponse("File is empty", 0, 0, 0, List.of("No file provided")));
        }

        try {
            LeadUploadResponse response = leadService.uploadLeads(file);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid file upload: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(new LeadUploadResponse("Upload failed", 0, 0, 0, List.of(e.getMessage())));
        } catch (Exception e) {
            logger.error("Failed to upload leads", e);
            return ResponseEntity.internalServerError()
                .body(new LeadUploadResponse("Upload failed", 0, 0, 0, List.of("Internal server error")));
        }
    }

    @GetMapping("/leads")
    public ResponseEntity<List<LeadResponse>> getAllLeads() {
        logger.info("Received request to get all leads");

        List<LeadResponse> leads = leadService.getAllLeads();
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/leads/scored")
    public ResponseEntity<List<LeadResponse>> getScoredLeads() {
        logger.info("Received request to get scored leads");

        List<LeadResponse> leads = leadService.getScoredLeads();
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/leads/unscored")
    public ResponseEntity<List<LeadResponse>> getUnscoredLeads() {
        logger.info("Received request to get unscored leads");

        List<LeadResponse> leads = leadService.getUnscoredLeads();
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/leads/{id}")
    public ResponseEntity<LeadResponse> getLead(@PathVariable Long id) {
        logger.info("Received request to get lead with ID: {}", id);

        LeadResponse lead = leadService.getLeadById(id);
        return ResponseEntity.ok(lead);
    }

    @GetMapping("/leads/intent/{level}")
    public ResponseEntity<List<LeadResponse>> getLeadsByIntent(@PathVariable String level) {
        logger.info("Received request to get leads with intent level: {}", level);

        List<LeadResponse> leads = leadService.getLeadsByIntent(level);
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/leads/export")
    public ResponseEntity<String> exportLeads() {
        logger.info("Received request to export all leads as CSV");

        String csvContent = leadService.exportLeadsAsCsv();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "leads.csv");
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(csvContent);
    }

    @GetMapping("/leads/export/scored")
    public ResponseEntity<String> exportScoredLeads() {
        logger.info("Received request to export scored leads as CSV");

        String csvContent = leadService.exportScoredLeadsAsCsv();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "scored_leads.csv");
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(csvContent);
    }

    @DeleteMapping("/leads")
    public ResponseEntity<Void> clearAllLeads() {
        logger.info("Received request to clear all leads");

        leadService.clearAllLeads();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/leads/unscored")
    public ResponseEntity<Void> clearUnscoredLeads() {
        logger.info("Received request to clear unscored leads");

        leadService.clearUnscoredLeads();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/leads/stats")
    public ResponseEntity<Object> getLeadStats() {
        logger.info("Received request to get lead statistics");

        return ResponseEntity.ok(new Object() {
            public final long totalLeads = leadService.getTotalLeadsCount();
            public final long scoredLeads = leadService.getScoredLeadsCount();
            public final long highIntentLeads = leadService.getHighIntentLeadsCount();
            public final long unscoredLeads = totalLeads - scoredLeads;
        });
    }
}

