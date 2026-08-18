package com.profilemanager.service;

import com.profilemanager.model.Organization;
import com.profilemanager.model.OrganizationMember;
import com.profilemanager.repository.OrganizationMemberRepository;
import com.profilemanager.repository.OrganizationRepository;
import com.profilemanager.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrganizationService {

    private final OrganizationRepository orgRepo;
    private final OrganizationMemberRepository memberRepo;
    private final ProfileRepository profileRepo;

    public OrganizationService(OrganizationRepository orgRepo,
                               OrganizationMemberRepository memberRepo,
                               ProfileRepository profileRepo) {
        this.orgRepo = orgRepo;
        this.memberRepo = memberRepo;
        this.profileRepo = profileRepo;
    }

    public List<Organization> listAll() {
        return orgRepo.findAllByOrderByNameAsc();
    }

    public Organization getById(UUID id) {
        return orgRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Organization not found"));
    }

    @Transactional
    public Organization create(Organization organization) {
        if (organization.getName() == null || organization.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Organization name is required");
        }
        organization.setName(organization.getName().trim());
        return orgRepo.save(organization);
    }

    @Transactional
    public Organization update(UUID id, Organization updated) {
        Organization existing = getById(id);
        if (updated.getName() != null && !updated.getName().trim().isEmpty()) {
            existing.setName(updated.getName().trim());
        }
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getCategory() != null) existing.setCategory(updated.getCategory());
        if (updated.getEmail() != null) existing.setEmail(updated.getEmail());
        if (updated.getLogoUrl() != null) existing.setLogoUrl(updated.getLogoUrl());
        return orgRepo.save(existing);
    }

    @Transactional
    public void delete(UUID id) {
        if (!orgRepo.existsById(id)) {
            throw new NoSuchElementException("Organization not found");
        }
        orgRepo.deleteById(id);
    }

    public List<OrganizationMember> getMembers(UUID orgId) {
        return memberRepo.findByOrganizationId(orgId);
    }

    public List<Organization> getOrganizationsForProfile(UUID profileId) {
        List<OrganizationMember> members = memberRepo.findByProfileId(profileId);
        if (members.isEmpty()) return List.of();
        List<UUID> orgIds = members.stream()
                .map(OrganizationMember::getOrganizationId)
                .collect(Collectors.toList());
        return orgRepo.findAllById(orgIds);
    }

    @Transactional
    public void addMember(UUID orgId, UUID profileId, String role) {
        if (!orgRepo.existsById(orgId)) {
            throw new NoSuchElementException("Organization not found");
        }
        if (!profileRepo.existsById(profileId)) {
            throw new NoSuchElementException("Profile not found");
        }
        if (memberRepo.existsByOrganizationIdAndProfileId(orgId, profileId)) {
            throw new IllegalStateException("Profile is already a member");
        }
        OrganizationMember member = new OrganizationMember(orgId, profileId,
                role != null ? role : "member");
        memberRepo.save(member);
    }

    @Transactional
    public void removeMember(UUID orgId, UUID profileId) {
        if (!memberRepo.existsByOrganizationIdAndProfileId(orgId, profileId)) {
            throw new NoSuchElementException("Member not found");
        }
        memberRepo.deleteByOrganizationIdAndProfileId(orgId, profileId);
    }
}
