package com.profilemanager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "organizations")
public class Organization extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;

    private String email;

    @Column(name = "logo_url")
    private String logoUrl = "";

    // ===== Constructors =====
    public Organization() {}

    public Organization(String name, String description, String category, String email, String logoUrl) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.email = email;
        this.logoUrl = logoUrl;
    }

    // ===== Getters and Setters =====
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}