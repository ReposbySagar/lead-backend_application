package com.leadqualification.dto;

import com.leadqualification.entity.Offer;

import java.time.LocalDateTime;
import java.util.List;

public class OfferResponse {

    private Long id;
    private String name;
    private List<String> valueProps;
    private List<String> idealUseCases;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OfferResponse() {}

    public OfferResponse(Offer offer) {
        this.id = offer.getId();
        this.name = offer.getName();
        this.valueProps = offer.getValueProps();
        this.idealUseCases = offer.getIdealUseCases();
        this.createdAt = offer.getCreatedAt();
        this.updatedAt = offer.getUpdatedAt();
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

    public List<String> getValueProps() {
        return valueProps;
    }

    public void setValueProps(List<String> valueProps) {
        this.valueProps = valueProps;
    }

    public List<String> getIdealUseCases() {
        return idealUseCases;
    }

    public void setIdealUseCases(List<String> idealUseCases) {
        this.idealUseCases = idealUseCases;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

