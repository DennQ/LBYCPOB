package com.profilemanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Maps to public.events. Field-for-field match with the schema:
 *
 * id uuid primary key default gen_random_uuid()
 * title text not null
 * description text not null default ''
 * location text not null default ''
 * start_time timestamptz not null
 * end_time timestamptz null
 * created_at timestamptz not null default now()
 *
 * See sql/events_schema.sql for the full DDL (ddl-auto=none, so the
 * table must already exist in Supabase before this entity will work).
 */
@Entity
@Table(name = "events")
public class Event {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description = "";

    @Column(nullable = false)
    private String location = "";

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Column(name = "end_time")
    private OffsetDateTime endTime;

    // insertable = false, updatable = false: the DB's default now()
    // populates this column; we never write to it from Java.
    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public Event() {
    }

    public Event(UUID id, String title, String description, String location,
                 OffsetDateTime startTime, OffsetDateTime endTime, OffsetDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event event)) return false;
        return Objects.equals(id, event.id)
                && Objects.equals(title, event.title)
                && Objects.equals(description, event.description)
                && Objects.equals(location, event.location)
                && Objects.equals(startTime, event.startTime)
                && Objects.equals(endTime, event.endTime)
                && Objects.equals(createdAt, event.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, location, startTime, endTime, createdAt);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Manual replacement for Lombok's @Builder. */
    public static final class Builder {
        private UUID id;
        private String title;
        private String description = "";
        private String location = "";
        private OffsetDateTime startTime;
        private OffsetDateTime endTime;
        private OffsetDateTime createdAt;

        private Builder() {
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder startTime(OffsetDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(OffsetDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Event build() {
            return new Event(id, title, description, location, startTime, endTime, createdAt);
        }
    }
}
