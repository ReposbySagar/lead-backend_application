package com.leadqualification.service;

import com.leadqualification.dto.ScoringResponse;
import com.leadqualification.entity.Lead;
import com.leadqualification.entity.Offer;
import com.leadqualification.exception.ResourceNotFoundException;
import com.leadqualification.repository.LeadRepository;
import com.leadqualification.repository.OfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScoringService {

    private static final Logger logger = LoggerFactory.getLogger(ScoringService.class);

    private final LeadRepository leadRepository;
    private final OfferRepository offerRepository;
    private final RuleScoringService ruleScoringService;
    private final GeminiService geminiService;
    private final LeadService leadService;
    private final ExecutorService executorService;

    @Autowired
    public ScoringService(LeadRepository leadRepository, 
                         OfferRepository offerRepository,
                         RuleScoringService ruleScoringService,
                         GeminiService geminiService,
                         LeadService leadService) {
        this.leadRepository = leadRepository;
        this.offerRepository = offerRepository;
        this.ruleScoringService = ruleScoringService;
        this.geminiService = geminiService;
        this.leadService = leadService;
        this.executorService = Executors.newFixedThreadPool(5);
    }

    public ScoringResponse scoreAllLeads() {
        logger.info("Starting scoring process for all unscored leads");

        Offer latestOffer = offerRepository.findLatestOffer()
            .orElseThrow(() -> new ResourceNotFoundException("No offer found. Please create an offer first."));
        // Eagerly initialize collections to prevent LazyInitializationException in async tasks
        latestOffer.getValueProps().size(); 
        latestOffer.getIdealUseCases().size();

        List<Lead> unscoredLeads = leadRepository.findByIsScored(false);
        
        if (unscoredLeads.isEmpty()) {
            logger.info("No unscored leads found");
            return new ScoringResponse("No unscored leads found", 0, 0, 0);
        }

        logger.info("Found {} unscored leads to process", unscoredLeads.size());

        int successCount = 0;
        int failureCount = 0;

        List<CompletableFuture<Void>> futures = unscoredLeads.stream()
            .map(lead -> CompletableFuture.runAsync(() -> {
                try {
                    scoreLead(lead, latestOffer);
                } catch (Exception e) {
                    logger.error("Failed to score lead: {}", lead.getName(), e);
                }
            }, executorService))
            .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        for (Lead lead : unscoredLeads) {
            if (lead.getIsScored()) {
                successCount++;
            } else {
                failureCount++;
            }
        }

        String message = String.format("Scoring completed. %d leads scored successfully, %d failed.", 
                                      successCount, failureCount);
        
        logger.info("Scoring process completed: {} successful, {} failed", successCount, failureCount);

        return new ScoringResponse(message, unscoredLeads.size(), successCount, failureCount);
    }

    public ScoringResponse scoreSpecificLead(Long leadId) {
        logger.info("Scoring specific lead with ID: {}", leadId);

        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new ResourceNotFoundException("Lead not found with ID: " + leadId));

        Offer latestOffer = offerRepository.findLatestOffer()
            .orElseThrow(() -> new ResourceNotFoundException("No offer found. Please create an offer first."));
        // Eagerly initialize collections to prevent LazyInitializationException in async tasks
        latestOffer.getValueProps().size(); 
        latestOffer.getIdealUseCases().size();

        try {
            scoreLead(lead, latestOffer);
            return new ScoringResponse("Lead scored successfully", 1, 1, 0);
        } catch (Exception e) {
            logger.error("Failed to score lead: {}", lead.getName(), e);
            return new ScoringResponse("Failed to score lead: " + e.getMessage(), 1, 0, 1);
        }
    }

    private void scoreLead(Lead lead, Offer offer) {
        logger.debug("Scoring lead: {}", lead.getName());

        try {
            int ruleScore = ruleScoringService.calculateRuleScore(lead, offer);
            String ruleExplanation = ruleScoringService.generateRuleExplanation(lead, offer, ruleScore);

            GeminiService.AIScoreResult aiResult = geminiService.scoreLeadIntent(lead, offer);
            int aiScore = aiResult.getScore();

            int totalScore = ruleScore + aiScore;
            String combinedReasoning = String.format("%s %s", ruleExplanation, aiResult.getReasoning());

            leadService.updateLeadScoring(lead.getId(), ruleScore, aiScore, 
                                        aiResult.getIntent(), combinedReasoning);

            lead.setRuleScore(ruleScore);
            lead.setAiScore(aiScore);
            lead.setTotalScore(totalScore);
            lead.setIntent(aiResult.getIntent());
            lead.setReasoning(combinedReasoning);
            lead.setIsScored(true);

            logger.debug("Lead {} scored: Rule={}, AI={}, Total={}, Intent={}", 
                        lead.getName(), ruleScore, aiScore, totalScore, aiResult.getIntent());

        } catch (Exception e) {
            logger.error("Error scoring lead {}: {}", lead.getName(), e.getMessage());
            throw new RuntimeException("Failed to score lead: " + e.getMessage(), e);
        }
    }

    public ScoringResponse rescoreAllLeads() {
        logger.info("Rescoring all leads");

        leadRepository.findAll().forEach(lead -> {
            lead.setIsScored(false);
            lead.setRuleScore(null);
            lead.setAiScore(null);
            lead.setTotalScore(null);
            lead.setIntent(null);
            lead.setReasoning(null);
        });

        return scoreAllLeads();
    }

    public ScoringResponse rescoreLeadsByIntent(String intentLevel) {
        logger.info("Rescoring leads with intent level: {}", intentLevel);

        List<Lead> leads = leadRepository.findByIntent(
            com.leadqualification.entity.IntentLevel.fromString(intentLevel)
        );

        leads.forEach(lead -> {
            lead.setIsScored(false);
            lead.setRuleScore(null);
            lead.setAiScore(null);
            lead.setTotalScore(null);
            lead.setIntent(null);
            lead.setReasoning(null);
        });

        Offer latestOffer = offerRepository.findLatestOffer()
            .orElseThrow(() -> new ResourceNotFoundException("No offer found. Please create an offer first."));
        // Eagerly initialize collections to prevent LazyInitializationException in async tasks
        latestOffer.getValueProps().size(); 
        latestOffer.getIdealUseCases().size();

        int successCount = 0;
        int failureCount = 0;

        for (Lead lead : leads) {
            try {
                scoreLead(lead, latestOffer);
                successCount++;
            } catch (Exception e) {
                logger.error("Failed to rescore lead: {}", lead.getName(), e);
                failureCount++;
            }
        }

        String message = String.format("Rescoring completed. %d leads rescored successfully, %d failed.", 
                                      successCount, failureCount);

        return new ScoringResponse(message, leads.size(), successCount, failureCount);
    }
}

