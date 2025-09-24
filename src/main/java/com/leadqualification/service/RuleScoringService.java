package com.leadqualification.service;

import com.leadqualification.entity.Lead;
import com.leadqualification.entity.Offer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class RuleScoringService {

    private static final Logger logger = LoggerFactory.getLogger(RuleScoringService.class);

    @Value("${scoring.rules.role.decision-maker:20}")
    private int decisionMakerScore;

    @Value("${scoring.rules.role.influencer:10}")
    private int influencerScore;

    @Value("${scoring.rules.industry.exact-match:20}")
    private int exactIndustryMatchScore;

    @Value("${scoring.rules.industry.adjacent-match:10}")
    private int adjacentIndustryMatchScore;

    @Value("${scoring.rules.data-completeness:10}")
    private int dataCompletenessScore;

    private static final Set<String> DECISION_MAKER_ROLES = Set.of(
        "ceo", "cto", "cfo", "coo", "president", "founder", "co-founder",
        "director", "head of", "vp", "vice president", "chief", "owner",
        "general manager", "managing director", "executive director"
    );

    private static final Set<String> INFLUENCER_ROLES = Set.of(
        "manager", "senior manager", "lead", "team lead", "principal",
        "senior", "architect", "specialist", "coordinator", "supervisor"
    );

    private static final Set<String> SAAS_INDUSTRIES = Set.of(
        "software", "saas", "technology", "tech", "it", "information technology",
        "software development", "cloud", "fintech", "edtech", "healthtech",
        "martech", "adtech", "proptech", "insurtech", "regtech"
    );

    private static final Set<String> ADJACENT_INDUSTRIES = Set.of(
        "consulting", "marketing", "advertising", "digital marketing",
        "e-commerce", "retail", "financial services", "healthcare",
        "education", "media", "telecommunications", "professional services"
    );

    public int calculateRuleScore(Lead lead, Offer offer) {
        logger.debug("Calculating rule score for lead: {}", lead.getName());

        int totalScore = 0;

        totalScore += calculateRoleScore(lead.getRole());
        totalScore += calculateIndustryScore(lead.getIndustry(), offer);
        totalScore += calculateDataCompletenessScore(lead);

        logger.debug("Rule score for lead {}: {}", lead.getName(), totalScore);
        return Math.min(totalScore, 50);
    }

    private int calculateRoleScore(String role) {
        if (StringUtils.isBlank(role)) {
            return 0;
        }

        String normalizedRole = role.toLowerCase().trim();

        if (DECISION_MAKER_ROLES.stream().anyMatch(normalizedRole::contains)) {
            logger.debug("Role '{}' identified as decision maker (+{})", role, decisionMakerScore);
            return decisionMakerScore;
        }

        if (INFLUENCER_ROLES.stream().anyMatch(normalizedRole::contains)) {
            logger.debug("Role '{}' identified as influencer (+{})", role, influencerScore);
            return influencerScore;
        }

        logger.debug("Role '{}' not recognized as decision maker or influencer (+0)", role);
        return 0;
    }

    private int calculateIndustryScore(String industry, Offer offer) {
        if (StringUtils.isBlank(industry)) {
            return 0;
        }

        String normalizedIndustry = industry.toLowerCase().trim();

        if (isExactIndustryMatch(normalizedIndustry, offer)) {
            logger.debug("Industry '{}' is exact match for offer (+{})", industry, exactIndustryMatchScore);
            return exactIndustryMatchScore;
        }

        if (isAdjacentIndustryMatch(normalizedIndustry)) {
            logger.debug("Industry '{}' is adjacent match (+{})", industry, adjacentIndustryMatchScore);
            return adjacentIndustryMatchScore;
        }

        logger.debug("Industry '{}' has no match (+0)", industry);
        return 0;
    }

    private boolean isExactIndustryMatch(String industry, Offer offer) {
        if (SAAS_INDUSTRIES.stream().anyMatch(industry::contains)) {
            return true;
        }

        if (offer != null && offer.getIdealUseCases() != null) {
            return offer.getIdealUseCases().stream()
                .anyMatch(useCase -> {
                    String normalizedUseCase = useCase.toLowerCase();
                    return normalizedUseCase.contains(industry) || 
                           industry.contains(normalizedUseCase) ||
                           hasKeywordOverlap(industry, normalizedUseCase);
                });
        }

        return false;
    }

    private boolean isAdjacentIndustryMatch(String industry) {
        return ADJACENT_INDUSTRIES.stream().anyMatch(industry::contains);
    }

    private boolean hasKeywordOverlap(String industry, String useCase) {
        List<String> industryWords = Arrays.asList(industry.split("\\s+"));
        List<String> useCaseWords = Arrays.asList(useCase.split("\\s+"));

        return industryWords.stream()
            .anyMatch(word -> word.length() > 3 && 
                     useCaseWords.stream().anyMatch(ucWord -> ucWord.contains(word)));
    }

    private int calculateDataCompletenessScore(Lead lead) {
        int filledFields = 0;
        int totalFields = 6;

        if (StringUtils.isNotBlank(lead.getName())) filledFields++;
        if (StringUtils.isNotBlank(lead.getRole())) filledFields++;
        if (StringUtils.isNotBlank(lead.getCompany())) filledFields++;
        if (StringUtils.isNotBlank(lead.getIndustry())) filledFields++;
        if (StringUtils.isNotBlank(lead.getLocation())) filledFields++;
        if (StringUtils.isNotBlank(lead.getLinkedinBio())) filledFields++;

        if (filledFields == totalFields) {
            logger.debug("All fields present for lead {} (+{})", lead.getName(), dataCompletenessScore);
            return dataCompletenessScore;
        }

        logger.debug("Data completeness for lead {}: {}/{} fields (+0)", 
                    lead.getName(), filledFields, totalFields);
        return 0;
    }

    public String generateRuleExplanation(Lead lead, Offer offer, int score) {
        StringBuilder explanation = new StringBuilder();
        
        int roleScore = calculateRoleScore(lead.getRole());
        int industryScore = calculateIndustryScore(lead.getIndustry(), offer);
        int completenessScore = calculateDataCompletenessScore(lead);

        explanation.append("Rule-based scoring breakdown: ");
        
        if (roleScore > 0) {
            String roleType = roleScore == decisionMakerScore ? "decision maker" : "influencer";
            explanation.append(String.format("Role (%s) +%d, ", roleType, roleScore));
        }
        
        if (industryScore > 0) {
            String matchType = industryScore == exactIndustryMatchScore ? "exact" : "adjacent";
            explanation.append(String.format("Industry (%s match) +%d, ", matchType, industryScore));
        }
        
        if (completenessScore > 0) {
            explanation.append(String.format("Complete data +%d, ", completenessScore));
        }

        if (explanation.toString().endsWith(", ")) {
            explanation.setLength(explanation.length() - 2);
        }

        explanation.append(String.format(". Total rule score: %d/50.", score));
        
        return explanation.toString();
    }
}

