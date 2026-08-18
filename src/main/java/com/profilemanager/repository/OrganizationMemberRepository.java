package com.profilemanager.repository;

import com.profilemanager.model.OrganizationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

/** Represents the interface component in the SocialNet system. */
public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, UUID> {
    List<OrganizationMember> findByOrganizationId(UUID organizationId);
    List<OrganizationMember> findByProfileId(UUID profileId);
    boolean existsByOrganizationIdAndProfileId(UUID organizationId, UUID profileId);
    void deleteByOrganizationIdAndProfileId(UUID organizationId, UUID profileId);
}
