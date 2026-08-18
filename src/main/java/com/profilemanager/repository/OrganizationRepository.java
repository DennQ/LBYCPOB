package com.profilemanager.repository;

import com.profilemanager.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

/** Represents the interface component in the SocialNet system. */
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    List<Organization> findAllByOrderByNameAsc();
}
