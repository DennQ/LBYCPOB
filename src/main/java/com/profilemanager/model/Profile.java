package com.profilemanager.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "profiles")
public class Profile {

    private static final String DEFAULT_PICTURE =
            "https://6fkrqtkwbcnqsois.public.blob.vercel-storage.com/avatars/default.webp";

    @Id
    @UuidGenerator
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String status = "";

    @Column(nullable = false)
    private String quote = "";

    @Column(nullable = false)
    private String picture = DEFAULT_PICTURE;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    // ===== STUDENT FIELDS (added by Member 2) =====
    @Column(name = "student_id")
    private String studentId;

    @Column(name = "course")
    private String course;

    @Column(name = "year_level")
    private Integer yearLevel;

    // ===== Constructors =====
    public Profile() {}

    public Profile(UUID id, String name, String status, String quote,
                   String picture, OffsetDateTime createdAt,
                   String studentId, String course, Integer yearLevel) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.quote = quote;
        this.picture = picture;
        this.createdAt = createdAt;
        this.studentId = studentId;
        this.course = course;
        this.yearLevel = yearLevel;
    }

    // ===== Getters and Setters =====
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

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public Integer getYearLevel() { return yearLevel; }
    public void setYearLevel(Integer yearLevel) { this.yearLevel = yearLevel; }

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

    @Override
    public String toString() {
        return "Profile{id=" + id + ", name='" + name + "'}";
    }

    // ===== Builder =====
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID id;
        private String name;
        private String status = "";
        private String quote = "";
        private String picture = DEFAULT_PICTURE;
        private OffsetDateTime createdAt;
        private String studentId;
        private String course;
        private Integer yearLevel;

        private Builder() {}

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder quote(String quote) { this.quote = quote; return this; }
        public Builder picture(String picture) { this.picture = picture; return this; }
        public Builder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder studentId(String studentId) { this.studentId = studentId; return this; }
        public Builder course(String course) { this.course = course; return this; }
        public Builder yearLevel(Integer yearLevel) { this.yearLevel = yearLevel; return this; }

        public Profile build() {
            return new Profile(id, name, status, quote, picture, createdAt,
                    studentId, course, yearLevel);
        }
    }
}
