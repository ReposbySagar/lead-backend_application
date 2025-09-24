package com.leadqualification.controller;

import com.leadqualification.dto.LeadResponse;
import com.leadqualification.service.LeadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ResultsController {

    private static final Logger logger = LoggerFactory.getLogger(ResultsController.class);

    private final LeadService leadService;

    @Autowired
    public ResultsController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping("/results")
    public ResponseEntity<List<LeadResponse>> getResults() {
        logger.info("Received request to get scoring results");

        List<LeadResponse> results = leadService.getScoredLeads();
        logger.info("Returning {} scored leads", results.size());
        
        return ResponseEntity.ok(results);
    }

    @GetMapping("/results/all")
    public ResponseEntity<List<LeadResponse>> getAllResults() {
        logger.info("Received request to get all leads (scored and unscored)");

        List<LeadResponse> results = leadService.getAllLeads();
        logger.info("Returning {} total leads", results.size());
        
        return ResponseEntity.ok(results);
    }

    @GetMapping("/results/high")
    public ResponseEntity<List<LeadResponse>> getHighIntentResults() {
        logger.info("Received request to get high intent leads");

        List<LeadResponse> results = leadService.getLeadsByIntent("high");
        logger.info("Returning {} high intent leads", results.size());
        
        return ResponseEntity.ok(results);
    }

    @GetMapping("/results/medium")
    public ResponseEntity<List<LeadResponse>> getMediumIntentResults() {
        logger.info("Received request to get medium intent leads");

        List<LeadResponse> results = leadService.getLeadsByIntent("medium");
        logger.info("Returning {} medium intent leads", results.size());
        
        return ResponseEntity.ok(results);
    }

    @GetMapping("/results/low")
    public ResponseEntity<List<LeadResponse>> getLowIntentResults() {
        logger.info("Received request to get low intent leads");

        List<LeadResponse> results = leadService.getLeadsByIntent("low");
        logger.info("Returning {} low intent leads", results.size());
        
        return ResponseEntity.ok(results);
    }

    @GetMapping("/results/export")
    public ResponseEntity<String> exportResults() {
        logger.info("Received request to export results as CSV");

        String csvContent = leadService.exportScoredLeadsAsCsv();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "lead_qualification_results.csv");
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(csvContent);
    }

    @GetMapping("/results/summary")
    public ResponseEntity<Object> getResultsSummary() {
        logger.info("Received request to get results summary");

        long totalLeads = leadService.getTotalLeadsCount();
        long scoredLeads = leadService.getScoredLeadsCount();
        long highIntentLeads = leadService.getHighIntentLeadsCount();
        long unscoredLeads = totalLeads - scoredLeads;

        return ResponseEntity.ok(new Object() {
            public final long total = totalLeads;
            public final long scored = scoredLeads;
            public final long unscored = unscoredLeads;
            public final long highIntent = highIntentLeads;
            public final double scoringProgress = totalLeads > 0 ? (double) scoredLeads / totalLeads * 100 : 0;
        });
    }
}

