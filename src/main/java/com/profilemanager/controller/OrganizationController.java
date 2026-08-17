package com.profilemanager.controller;

import com.profilemanager.dto.Dtos;
import com.profilemanager.model.Organization;
import com.profilemanager.service.OrganizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {
    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    public ResponseEntity<List<Dtos.OrganizationResponse>> listOrganizations() {
        List<Organization> organizations = organizationService.listAll();
        List<Dtos.OrganizationResponse> responses = organizations.stream()
                .map(org -> Dtos.OrganizationResponse.fromEntity(org, organizationService.getEventCount(org.getId())))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dtos.OrganizationResponse> getOrganization(@PathVariable UUID id) {
        Organization org = organizationService.getById(id);
        long eventCount = organizationService.getEventCount(id);
        return ResponseEntity.ok(Dtos.OrganizationResponse.fromEntity(org, eventCount));
    }

    @PostMapping
    public ResponseEntity<Dtos.OrganizationResponse> createOrganization(@RequestBody Dtos.OrganizationRequest request) {
        Organization org = Organization.builder()
                .name(request.name())
                .description(request.description())
                .category(request.category())
                .email(request.email())
                .logoUrl(request.logoUrl())
                .build();
        Organization saved = organizationService.create(org);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Dtos.OrganizationResponse.fromEntity(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dtos.OrganizationResponse> updateOrganization(@PathVariable UUID id,
                                                                        @RequestBody Dtos.OrganizationRequest request) {
        Organization updated = Organization.builder()
                .name(request.name())
                .description(request.description())
                .category(request.category())
                .email(request.email())
                .logoUrl(request.logoUrl())
                .build();
        Organization saved = organizationService.update(id, updated);
        return ResponseEntity.ok(Dtos.OrganizationResponse.fromEntity(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable UUID id) {
        organizationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
