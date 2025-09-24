package com.leadqualification.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "offers")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ElementCollection
    @CollectionTable(name = "offer_value_props", joinColumns = @JoinColumn(name = "offer_id"))
    @Column(name = "value_prop")
    private List<String> valueProps;

    @ElementCollection
    @CollectionTable(name = "offer_use_cases", joinColumns = @JoinColumn(name = "offer_id"))
    @Column(name = "use_case")
    private List<String> idealUseCases;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Offer() {}

    public Offer(String name, List<String> valueProps, List<String> idealUseCases) {
        this.name = name;
        this.valueProps = valueProps;
        this.idealUseCases = idealUseCases;
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

