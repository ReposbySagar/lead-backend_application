package com.leadqualification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class OfferRequest {

    @NotBlank(message = "Offer name is required")
    private String name;

    @NotEmpty(message = "Value propositions are required")
    private List<String> valueProps;

    @NotEmpty(message = "Ideal use cases are required")
    private List<String> idealUseCases;

    public OfferRequest() {}

    public OfferRequest(String name, List<String> valueProps, List<String> idealUseCases) {
        this.name = name;
        this.valueProps = valueProps;
        this.idealUseCases = idealUseCases;
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
}

