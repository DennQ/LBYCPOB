package com.profilemanager.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "event_registrations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"event_id", "profile_id"})
})
/** Represents the class component in the SocialNet system. */
public class EventRegistration {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    @Column(name = "event_id", nullable = false)
    private UUID eventId;
    @Column(name = "profile_id", nullable = false)
    private UUID profileId;
    @Column(name = "registration_date", insertable = false, updatable = false)
    private OffsetDateTime registrationDate;
    private String status = "registered";

    public EventRegistration() {}
    public EventRegistration(UUID id, UUID eventId, UUID profileId, OffsetDateTime registrationDate, String status) {
        this.id = id;
        this.eventId = eventId;
        this.profileId = profileId;
        this.registrationDate = registrationDate;
        this.status = status;
    }
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }
    public UUID getProfileId() { return profileId; }
    public void setProfileId(UUID profileId) { this.profileId = profileId; }
    public OffsetDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(OffsetDateTime registrationDate) { this.registrationDate = registrationDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventRegistration)) return false;
        EventRegistration that = (EventRegistration) o;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() { return Objects.hash(id); }
    @Override
    public String toString() { return "EventRegistration{id=" + id + ", eventId=" + eventId + ", profileId=" + profileId + '}'; }
    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private UUID id;
        private UUID eventId;
        private UUID profileId;
        private OffsetDateTime registrationDate;
        private String status = "registered";
        private Builder() {}
        public Builder id(UUID id) { this.id = id; return this; }
        public Builder eventId(UUID eventId) { this.eventId = eventId; return this; }
        public Builder profileId(UUID profileId) { this.profileId = profileId; return this; }
        public Builder registrationDate(OffsetDateTime registrationDate) { this.registrationDate = registrationDate; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public EventRegistration build() { return new EventRegistration(id, eventId, profileId, registrationDate, status); }
    }
}
