package com.leadqualification.dto;

import java.time.LocalDateTime;
import java.util.List;

public class LeadUploadResponse {

    private String message;
    private int totalLeads;
    private int successfulUploads;
    private int failedUploads;
    private List<String> errors;
    private LocalDateTime uploadedAt;

    public LeadUploadResponse() {}

    public LeadUploadResponse(String message, int totalLeads, int successfulUploads, 
                             int failedUploads, List<String> errors) {
        this.message = message;
        this.totalLeads = totalLeads;
        this.successfulUploads = successfulUploads;
        this.failedUploads = failedUploads;
        this.errors = errors;
        this.uploadedAt = LocalDateTime.now();
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

    public int getSuccessfulUploads() {
        return successfulUploads;
    }

    public void setSuccessfulUploads(int successfulUploads) {
        this.successfulUploads = successfulUploads;
    }

    public int getFailedUploads() {
        return failedUploads;
    }

    public void setFailedUploads(int failedUploads) {
        this.failedUploads = failedUploads;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}

