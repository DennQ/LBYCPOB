package com.profilemanager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "profiles")
public class Profile extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String status = "";

    @Column(nullable = false)
    private String quote = "";

    @Column(nullable = false)
    private String picture = "";

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "course")
    private String course;

    @Column(name = "year_level")
    private Integer yearLevel;

    // ===== Constructors =====
    public Profile() {}

    public Profile(String name, String studentId, String course, Integer yearLevel) {
        this.name = name;
        this.studentId = studentId;
        this.course = course;
        this.yearLevel = yearLevel;
    }

    // ===== Getters and Setters =====
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Integer getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(Integer yearLevel) {
        this.yearLevel = yearLevel;
    }
}