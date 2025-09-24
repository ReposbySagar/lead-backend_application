package com.leadqualification.dto;

import com.leadqualification.entity.IntentLevel;
import com.leadqualification.entity.Lead;

public class LeadResponse {

    private Long id;
    private String name;
    private String role;
    private String company;
    private String industry;
    private String location;
    private String linkedinBio;
    private Integer ruleScore;
    private Integer aiScore;
    private Integer score;
    private String intent;
    private String reasoning;
    private Boolean isScored;

    public LeadResponse() {}

    public LeadResponse(Lead lead) {
        this.id = lead.getId();
        this.name = lead.getName();
        this.role = lead.getRole();
        this.company = lead.getCompany();
        this.industry = lead.getIndustry();
        this.location = lead.getLocation();
        this.linkedinBio = lead.getLinkedinBio();
        this.ruleScore = lead.getRuleScore();
        this.aiScore = lead.getAiScore();
        this.score = lead.getTotalScore();
        this.intent = lead.getIntent() != null ? lead.getIntent().getDisplayName() : null;
        this.reasoning = lead.getReasoning();
        this.isScored = lead.getIsScored();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLinkedinBio() {
        return linkedinBio;
    }

    public void setLinkedinBio(String linkedinBio) {
        this.linkedinBio = linkedinBio;
    }

    public Integer getRuleScore() {
        return ruleScore;
    }

    public void setRuleScore(Integer ruleScore) {
        this.ruleScore = ruleScore;
    }

    public Integer getAiScore() {
        return aiScore;
    }

    public void setAiScore(Integer aiScore) {
        this.aiScore = aiScore;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }

    public Boolean getIsScored() {
        return isScored;
    }

    public void setIsScored(Boolean isScored) {
        this.isScored = isScored;
    }
}

