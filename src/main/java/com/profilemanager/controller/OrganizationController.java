package com.profilemanager.controller;

import com.profilemanager.dto.Dtos;
import com.profilemanager.model.Organization;
import com.profilemanager.model.OrganizationMember;
import com.profilemanager.service.OrganizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/organizations")
/** Represents the class component in the SocialNet system. */
public class OrganizationController {
    private final OrganizationService orgService;

    public OrganizationController(OrganizationService orgService) {
        this.orgService = orgService;
    }

    @GetMapping
    public ResponseEntity<List<Dtos.OrganizationResponse>> listOrganizations() {
        List<Organization> orgs = orgService.listAll();
        List<Dtos.OrganizationResponse> responses = orgs.stream()
                .map(org -> Dtos.OrganizationResponse.fromEntity(org, 0L))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dtos.OrganizationResponse> getOrganization(@PathVariable UUID id) {
        Organization org = orgService.getById(id);
        return ResponseEntity.ok(Dtos.OrganizationResponse.fromEntity(org, 0L));
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<List<Organization>> getOrganizationsForProfile(@PathVariable UUID profileId) {
        List<Organization> orgs = orgService.getOrganizationsForProfile(profileId);
        return ResponseEntity.ok(orgs);
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
        Organization saved = orgService.create(org);
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
        Organization saved = orgService.update(id, updated);
        return ResponseEntity.ok(Dtos.OrganizationResponse.fromEntity(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable UUID id) {
        orgService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<Map<String, Object>>> getMembers(@PathVariable UUID id) {
        List<OrganizationMember> members = orgService.getMembers(id);
        List<Map<String, Object>> result = new ArrayList<>();
        for (OrganizationMember m : members) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", m.getId());
            map.put("profileId", m.getProfileId());
            map.put("role", m.getRole());
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<Void> addMember(@PathVariable UUID id,
                                          @RequestBody Map<String, String> payload) {
        UUID profileId = UUID.fromString(payload.get("profileId"));
        String role = payload.getOrDefault("role", "member");
        orgService.addMember(id, profileId, role);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/members/{profileId}")
    public ResponseEntity<Void> removeMember(@PathVariable UUID id,
                                             @PathVariable UUID profileId) {
        orgService.removeMember(id, profileId);
        return ResponseEntity.noContent().build();
    }
}
