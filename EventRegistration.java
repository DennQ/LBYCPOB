package com.profilemanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;


@Entity
@Table(name = "event_registrations", uniqueConstraints = {
        @UniqueConstraint(name = "uc_event_profile", columnNames = {"event_id", "profile_id"})
})
public class EventRegistration {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "event_id", nullable = false, columnDefinition = "uuid")
    private UUID eventId;

    @Column(name = "profile_id", nullable = false, columnDefinition = "uuid")
    private UUID profileId;

    // insertable = false, updatable = false: the DB's default now()
    // populates this column; we never write to it from Java.
    @Column(name = "registered_at", insertable = false, updatable = false)
    private OffsetDateTime registeredAt;

    public EventRegistration() {
    }

    public EventRegistration(UUID id, UUID eventId, UUID profileId, OffsetDateTime registeredAt) {
        this.id = id;
        this.eventId = eventId;
        this.profileId = profileId;
        this.registeredAt = registeredAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public OffsetDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(OffsetDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventRegistration that)) return false;
        return Objects.equals(id, that.id)
                && Objects.equals(eventId, that.eventId)
                && Objects.equals(profileId, that.profileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventId, profileId);
    }

    @Override
    public String toString() {
        return "EventRegistration{" +
                "id=" + id +
                ", eventId=" + eventId +
                ", profileId=" + profileId +
                ", registeredAt=" + registeredAt +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    
    public static final class Builder {
        private UUID id;
        private UUID eventId;
        private UUID profileId;
        private OffsetDateTime registeredAt;

        private Builder() {
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder eventId(UUID eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder profileId(UUID profileId) {
            this.profileId = profileId;
            return this;
        }

        public Builder registeredAt(OffsetDateTime registeredAt) {
            this.registeredAt = registeredAt;
            return this;
        }

        public EventRegistration build() {
            return new EventRegistration(id, eventId, profileId, registeredAt);
        }
    }
}
