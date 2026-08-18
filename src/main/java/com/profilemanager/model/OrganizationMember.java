package com.profilemanager.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;

@Entity
@Table(name = "organization_members", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"organization_id", "profile_id"})
})
/** Represents the class component in the SocialNet system. */
public class OrganizationMember {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;
    @Column(name = "profile_id", nullable = false)
    private UUID profileId;
    private String role = "member";

    public OrganizationMember() {}
    public OrganizationMember(UUID id, UUID organizationId, UUID profileId, String role) {
        this.id = id;
        this.organizationId = organizationId;
        this.profileId = profileId;
        this.role = role;
    }
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public UUID getProfileId() { return profileId; }
    public void setProfileId(UUID profileId) { this.profileId = profileId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private UUID id;
        private UUID organizationId;
        private UUID profileId;
        private String role = "member";
        private Builder() {}
        public Builder id(UUID id) { this.id = id; return this; }
        public Builder organizationId(UUID organizationId) { this.organizationId = organizationId; return this; }
        public Builder profileId(UUID profileId) { this.profileId = profileId; return this; }
        public Builder role(String role) { this.role = role; return this; }
        public OrganizationMember build() {
            return new OrganizationMember(id, organizationId, profileId, role);
        }
    }
}
