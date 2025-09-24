package com.leadqualification.service;

import com.leadqualification.entity.Lead;
import com.leadqualification.entity.Offer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class RuleScoringServiceTest {

    private RuleScoringService ruleScoringService;
    private Offer testOffer;

    @BeforeEach
    void setUp() {
        ruleScoringService = new RuleScoringService();
        
        ReflectionTestUtils.setField(ruleScoringService, "decisionMakerScore", 20);
        ReflectionTestUtils.setField(ruleScoringService, "influencerScore", 10);
        ReflectionTestUtils.setField(ruleScoringService, "exactIndustryMatchScore", 20);
        ReflectionTestUtils.setField(ruleScoringService, "adjacentIndustryMatchScore", 10);
        ReflectionTestUtils.setField(ruleScoringService, "dataCompletenessScore", 10);

        testOffer = new Offer(
            "AI Outreach Automation",
            Arrays.asList("24/7 outreach", "6x more meetings"),
            Arrays.asList("B2B SaaS mid-market")
        );
    }

    @Test
    void testCalculateRuleScore_DecisionMakerWithCompleteData() {
        Lead lead = new Lead(
            "John Doe",
            "CEO",
            "TechCorp",
            "Software",
            "San Francisco",
            "Experienced CEO in SaaS industry"
        );

        int score = ruleScoringService.calculateRuleScore(lead, testOffer);
        assertEquals(50, score);
    }

    @Test
    void testCalculateRuleScore_InfluencerWithPartialData() {
        Lead lead = new Lead(
            "Jane Smith",
            "Senior Manager",
            "TechCorp",
            "Consulting",
            null,
            null
        );

        int score = ruleScoringService.calculateRuleScore(lead, testOffer);
        assertEquals(20, score);
    }

    @Test
    void testCalculateRuleScore_NoMatchingRole() {
        Lead lead = new Lead(
            "Bob Johnson",
            "Junior Developer",
            "TechCorp",
            "Software",
            "New York",
            "Entry level developer"
        );

        int score = ruleScoringService.calculateRuleScore(lead, testOffer);
        assertEquals(30, score);
    }

    @Test
    void testCalculateRuleScore_EmptyLead() {
        Lead lead = new Lead(
            "Empty Lead",
            null,
            null,
            null,
            null,
            null
        );

        int score = ruleScoringService.calculateRuleScore(lead, testOffer);
        assertEquals(0, score);
    }

    @Test
    void testGenerateRuleExplanation() {
        Lead lead = new Lead(
            "John Doe",
            "CEO",
            "TechCorp",
            "Software",
            "San Francisco",
            "Experienced CEO in SaaS industry"
        );

        int score = ruleScoringService.calculateRuleScore(lead, testOffer);
        String explanation = ruleScoringService.generateRuleExplanation(lead, testOffer, score);

        assertNotNull(explanation);
        assertTrue(explanation.contains("decision maker"));
        assertTrue(explanation.contains("exact match"));
        assertTrue(explanation.contains("Complete data"));
        assertTrue(explanation.contains("50"));
    }
}

