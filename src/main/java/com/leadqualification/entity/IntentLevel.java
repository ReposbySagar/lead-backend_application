package com.leadqualification.entity;

public enum IntentLevel {
    HIGH("High", 50),
    MEDIUM("Medium", 30),
    LOW("Low", 10);

    private final String displayName;
    private final int aiScore;

    IntentLevel(String displayName, int aiScore) {
        this.displayName = displayName;
        this.aiScore = aiScore;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getAiScore() {
        return aiScore;
    }

    public static IntentLevel fromString(String value) {
        if (value == null) {
            return LOW;
        }
        
        String normalizedValue = value.trim().toLowerCase();
        switch (normalizedValue) {
            case "high":
                return HIGH;
            case "medium":
                return MEDIUM;
            case "low":
                return LOW;
            default:
                return LOW;
        }
    }
}

