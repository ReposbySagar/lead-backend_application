package com.leadqualification.dto;

import java.time.LocalDateTime;

public class ScoringResponse {

    private String message;
    private int totalLeads;
    private int successfulScores;
    private int failedScores;
    private LocalDateTime scoredAt;

    public ScoringResponse() {}

    public ScoringResponse(String message, int totalLeads, int successfulScores, int failedScores) {
        this.message = message;
        this.totalLeads = totalLeads;
        this.successfulScores = successfulScores;
        this.failedScores = failedScores;
        this.scoredAt = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalLeads() {
        return totalLeads;
    }

    public void setTotalLeads(int totalLeads) {
        this.totalLeads = totalLeads;
    }

    public int getSuccessfulScores() {
        return successfulScores;
    }

    public void setSuccessfulScores(int successfulScores) {
        this.successfulScores = successfulScores;
    }

    public int getFailedScores() {
        return failedScores;
    }

    public void setFailedScores(int failedScores) {
        this.failedScores = failedScores;
    }

    public LocalDateTime getScoredAt() {
        return scoredAt;
    }

    public void setScoredAt(LocalDateTime scoredAt) {
        this.scoredAt = scoredAt;
    }
}

