package com.profilemanager.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "organization_members", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"organization_id", "profile_id"})
})
public class OrganizationMember extends BaseEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "profile_id", nullable = false)
    private UUID profileId;

    private String role = "member";

    public OrganizationMember() {}

    public OrganizationMember(UUID organizationId, UUID profileId) {
        this.organizationId = organizationId;
        this.profileId = profileId;
    }

    public OrganizationMember(UUID organizationId, UUID profileId, String role) {
        this.organizationId = organizationId;
        this.profileId = profileId;
        this.role = role;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
