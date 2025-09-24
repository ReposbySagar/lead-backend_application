package com.leadqualification.controller;

import com.leadqualification.dto.ScoringResponse;
import com.leadqualification.service.ScoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ScoringController {

    private static final Logger logger = LoggerFactory.getLogger(ScoringController.class);

    private final ScoringService scoringService;

    @Autowired
    public ScoringController(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @PostMapping("/score")
    public ResponseEntity<ScoringResponse> scoreAllLeads() {
        logger.info("Received request to score all unscored leads");

        try {
            ScoringResponse response = scoringService.scoreAllLeads();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to score leads", e);
            ScoringResponse errorResponse = new ScoringResponse(
                "Scoring failed: " + e.getMessage(), 0, 0, 0
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/score/{leadId}")
    public ResponseEntity<ScoringResponse> scoreSpecificLead(@PathVariable Long leadId) {
        logger.info("Received request to score lead with ID: {}", leadId);

        try {
            ScoringResponse response = scoringService.scoreSpecificLead(leadId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to score lead with ID: {}", leadId, e);
            ScoringResponse errorResponse = new ScoringResponse(
                "Scoring failed: " + e.getMessage(), 1, 0, 1
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/rescore")
    public ResponseEntity<ScoringResponse> rescoreAllLeads() {
        logger.info("Received request to rescore all leads");

        try {
            ScoringResponse response = scoringService.rescoreAllLeads();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to rescore leads", e);
            ScoringResponse errorResponse = new ScoringResponse(
                "Rescoring failed: " + e.getMessage(), 0, 0, 0
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/rescore/intent/{level}")
    public ResponseEntity<ScoringResponse> rescoreLeadsByIntent(@PathVariable String level) {
        logger.info("Received request to rescore leads with intent level: {}", level);

        try {
            ScoringResponse response = scoringService.rescoreLeadsByIntent(level);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to rescore leads with intent level: {}", level, e);
            ScoringResponse errorResponse = new ScoringResponse(
                "Rescoring failed: " + e.getMessage(), 0, 0, 0
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}

