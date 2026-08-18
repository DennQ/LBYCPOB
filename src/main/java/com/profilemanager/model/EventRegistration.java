package com.profilemanager.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "event_registrations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"event_id", "profile_id"})
})
public class EventRegistration extends BaseEntity {

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "profile_id", nullable = false)
    private UUID profileId;

    @Column(name = "registration_date", insertable = false, updatable = false)
    private OffsetDateTime registrationDate;

    private String status = "registered";

    public EventRegistration() {}

    public EventRegistration(UUID eventId, UUID profileId) {
        this.eventId = eventId;
        this.profileId = profileId;
    }

    public EventRegistration(UUID eventId, UUID profileId, String status) {
        this.eventId = eventId;
        this.profileId = profileId;
        this.status = status;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public OffsetDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(OffsetDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
