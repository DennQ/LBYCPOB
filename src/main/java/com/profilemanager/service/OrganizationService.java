package com.profilemanager.service;

import com.profilemanager.model.Organization;
import com.profilemanager.repository.OrganizationRepository;
import com.profilemanager.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final EventRepository eventRepository;

    public OrganizationService(OrganizationRepository organizationRepository, EventRepository eventRepository) {
        this.organizationRepository = organizationRepository;
        this.eventRepository = eventRepository;
    }

    public List<Organization> listAll() {
        return organizationRepository.findAllByOrderByNameAsc();
    }

    public Organization getById(UUID id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Organization not found with id: " + id));
    }

    @Transactional
    public Organization create(Organization organization) {
        if (organization.getName() == null || organization.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Organization name is required.");
        }
        organization.setName(organization.getName().trim());
        return organizationRepository.save(organization);
    }

    @Transactional
    public Organization update(UUID id, Organization updated) {
        Organization existing = getById(id);
        if (updated.getName() != null && !updated.getName().trim().isEmpty()) {
            existing.setName(updated.getName().trim());
        }
        if (updated.getDescription() != null) {
            existing.setDescription(updated.getDescription());
        }
        if (updated.getCategory() != null) {
            existing.setCategory(updated.getCategory());
        }
        if (updated.getEmail() != null) {
            existing.setEmail(updated.getEmail());
        }
        if (updated.getLogoUrl() != null) {
            existing.setLogoUrl(updated.getLogoUrl());
        }
        return organizationRepository.save(existing);
    }

    @Transactional
    public void delete(UUID id) {
        if (!organizationRepository.existsById(id)) {
            throw new NoSuchElementException("Organization not found with id: " + id);
        }
        organizationRepository.deleteById(id);
    }

    public long getEventCount(UUID organizationId) {
        return eventRepository.findByOrganizationIdOrderByEventDateAsc(organizationId).size();
    }
}
