package com.profilemanager.controller;

import com.profilemanager.dto.Dtos;
import com.profilemanager.model.EventRegistration;
import com.profilemanager.service.EventRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/registrations")
public class EventRegistrationController {

    private final EventRegistrationService registrationService;

    public EventRegistrationController(EventRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<Dtos.RegistrationResponse> register(@RequestBody Dtos.RegistrationRequest request) {
        // No login system, so the frontend sends the real profileId it
        // looked up by name. (Previously hardcoded to a placeholder UUID
        // that didn't exist, so registration always failed silently.)
        EventRegistration registration = registrationService.register(request.eventId(), request.profileId());
    
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Dtos.RegistrationResponse.fromEntity(registration));
    }

    @DeleteMapping("/{eventId}/{profileId}")
    public ResponseEntity<Void> cancelRegistration(@PathVariable UUID eventId, @PathVariable UUID profileId) {
        registrationService.cancelRegistration(eventId, profileId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Dtos.RegistrationResponse>> getRegistrationsByEvent(@PathVariable UUID eventId) {
        List<EventRegistration> registrations = registrationService.getByEventId(eventId);
        return ResponseEntity.ok(registrations.stream()
                .map(Dtos.RegistrationResponse::fromEntity)
                .toList());
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<List<Dtos.RegistrationResponse>> getRegistrationsByProfile(@PathVariable UUID profileId) {
        List<EventRegistration> registrations = registrationService.getByProfileId(profileId);
        return ResponseEntity.ok(registrations.stream()
                .map(Dtos.RegistrationResponse::fromEntity)
                .toList());
    }

    @GetMapping("/check/{eventId}/{profileId}")
    public ResponseEntity<Boolean> checkRegistration(@PathVariable UUID eventId, @PathVariable UUID profileId) {
        boolean isRegistered = registrationService.isRegistered(eventId, profileId);
        return ResponseEntity.ok(isRegistered);
    }

    @GetMapping("/count/{eventId}")
    public ResponseEntity<Long> getRegistrationCount(@PathVariable UUID eventId) {
        long count = registrationService.getRegistrationCount(eventId);
        return ResponseEntity.ok(count);
    }
}
