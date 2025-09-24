package com.leadqualification.service;

import com.leadqualification.dto.LeadResponse;
import com.leadqualification.dto.LeadUploadResponse;
import com.leadqualification.entity.IntentLevel;
import com.leadqualification.entity.Lead;
import com.leadqualification.exception.ResourceNotFoundException;
import com.leadqualification.repository.LeadRepository;
import com.leadqualification.util.CsvProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeadService {

    private static final Logger logger = LoggerFactory.getLogger(LeadService.class);

    private final LeadRepository leadRepository;
    private final CsvProcessor csvProcessor;

    @Autowired
    public LeadService(LeadRepository leadRepository, CsvProcessor csvProcessor) {
        this.leadRepository = leadRepository;
        this.csvProcessor = csvProcessor;
    }

    public LeadUploadResponse uploadLeads(MultipartFile file) {
        logger.info("Processing lead upload: {}", file.getOriginalFilename());

        if (!csvProcessor.isValidCsvFile(file)) {
            throw new IllegalArgumentException("Invalid file format. Please upload a CSV file.");
        }

        List<String> errors = new ArrayList<>();
        int successfulUploads = 0;
        int totalLeads = 0;

        try {
            List<Lead> leads = csvProcessor.processLeadsCsv(file);
            totalLeads = leads.size();

            for (Lead lead : leads) {
                try {
                    leadRepository.save(lead);
                    successfulUploads++;
                } catch (Exception e) {
                    errors.add("Failed to save lead '" + lead.getName() + "': " + e.getMessage());
                    logger.error("Failed to save lead: {}", lead.getName(), e);
                }
            }

        } catch (Exception e) {
            errors.add("Failed to process CSV file: " + e.getMessage());
            logger.error("Failed to process CSV file", e);
        }

        int failedUploads = totalLeads - successfulUploads;
        String message = String.format("Upload completed. %d successful, %d failed.", 
                                      successfulUploads, failedUploads);

        logger.info("Lead upload completed: {} successful, {} failed", successfulUploads, failedUploads);

        return new LeadUploadResponse(message, totalLeads, successfulUploads, failedUploads, errors);
    }

    @Transactional(readOnly = true)
    public List<LeadResponse> getAllLeads() {
        logger.info("Retrieving all leads");

        return leadRepository.findAll()
            .stream()
            .map(LeadResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LeadResponse> getScoredLeads() {
        logger.info("Retrieving scored leads");

        return leadRepository.findByIsScoredOrderByTotalScoreDesc(true)
            .stream()
            .map(LeadResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LeadResponse> getUnscoredLeads() {
        logger.info("Retrieving unscored leads");

        return leadRepository.findByIsScored(false)
            .stream()
            .map(LeadResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LeadResponse getLeadById(Long id) {
        logger.info("Retrieving lead with ID: {}", id);

        Lead lead = leadRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lead not found with ID: " + id));

        return new LeadResponse(lead);
    }

    @Transactional(readOnly = true)
    public List<LeadResponse> getLeadsByIntent(String intentLevel) {
        logger.info("Retrieving leads with intent level: {}", intentLevel);

        IntentLevel intent = IntentLevel.fromString(intentLevel);
        return leadRepository.findByIntent(intent)
            .stream()
            .map(LeadResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public String exportLeadsAsCsv() {
        logger.info("Exporting all leads as CSV");

        List<Lead> leads = leadRepository.findAll();
        return csvProcessor.generateCsvContent(leads);
    }

    @Transactional(readOnly = true)
    public String exportScoredLeadsAsCsv() {
        logger.info("Exporting scored leads as CSV");

        List<Lead> leads = leadRepository.findByIsScored(true);
        return csvProcessor.generateCsvContent(leads);
    }

    public void updateLeadScoring(Long leadId, Integer ruleScore, Integer aiScore, 
                                 IntentLevel intent, String reasoning) {
        logger.info("Updating scoring for lead ID: {}", leadId);

        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new ResourceNotFoundException("Lead not found with ID: " + leadId));

        lead.setRuleScore(ruleScore);
        lead.setAiScore(aiScore);
        lead.setTotalScore(ruleScore + aiScore);
        lead.setIntent(intent);
        lead.setReasoning(reasoning);
        lead.setIsScored(true);

        leadRepository.save(lead);
        logger.info("Lead scoring updated successfully for ID: {}", leadId);
    }

    public void clearAllLeads() {
        logger.info("Clearing all leads");
        leadRepository.deleteAll();
        logger.info("All leads cleared successfully");
    }

    public void clearUnscoredLeads() {
        logger.info("Clearing unscored leads");
        leadRepository.deleteAllByIsScored(false);
        logger.info("Unscored leads cleared successfully");
    }

    @Transactional(readOnly = true)
    public long getTotalLeadsCount() {
        return leadRepository.count();
    }

    @Transactional(readOnly = true)
    public long getScoredLeadsCount() {
        return leadRepository.countScoredLeads();
    }

    @Transactional(readOnly = true)
    public long getHighIntentLeadsCount() {
        return leadRepository.countByIntent(IntentLevel.HIGH);
    }
}

