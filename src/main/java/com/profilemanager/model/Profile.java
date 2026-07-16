package com.profilemanager.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String status = "";

    @Column(nullable = false)
    private String quote = "";

    @Column(nullable = false)
    private String picture = "";

    public Profile() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getQuote() { return quote; }
    public void setQuote(String quote) { this.quote = quote; }
    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile)) return false;
        Profile profile = (Profile) o;
        return Objects.equals(id, profile.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
