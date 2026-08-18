package com.profilemanager.controller;

import com.profilemanager.dto.Dtos;
import com.profilemanager.model.Event;
import com.profilemanager.model.Organization;
import com.profilemanager.service.EventService;
import com.profilemanager.service.OrganizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
/** Represents the class component in the SocialNet system. */
public class EventController {
    private final EventService eventService;
    private final OrganizationService organizationService;

    public EventController(EventService eventService, OrganizationService organizationService) {
        this.eventService = eventService;
        this.organizationService = organizationService;
    }

    @GetMapping
    public ResponseEntity<List<Dtos.EventResponse>> listAllEvents(
            @RequestParam(defaultValue = "all") String scope) {
        List<Event> events = switch (scope) {
            case "upcoming" -> eventService.findAllUpcomingEvents();
            case "next-two-weeks" -> eventService.findUpcomingEvents();
            default -> eventService.listAll();
        };
        return ResponseEntity.ok(buildEventResponses(events));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Dtos.EventResponse>> getUpcomingEvents() {
        List<Event> events = eventService.findUpcomingEvents();
        return ResponseEntity.ok(buildEventResponses(events));
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<Dtos.EventResponse>> getEventsByOrganization(@PathVariable UUID organizationId) {
        List<Event> events = eventService.findByOrganization(organizationId);
        return ResponseEntity.ok(buildEventResponses(events));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dtos.EventResponse> getEvent(@PathVariable UUID id,
                                                       @RequestParam(required = false) UUID profileId) {
        Event event = eventService.getById(id);
        Organization org = organizationService.getById(event.getOrganizationId());
        long registrationCount = eventService.getRegistrationCount(id);
        boolean isRegistered = profileId != null && eventService.isUserRegistered(id, profileId);
        return ResponseEntity.ok(Dtos.EventResponse.fromEntity(event, org.getName(), registrationCount, isRegistered));
    }

    @PostMapping
    public ResponseEntity<Dtos.EventResponse> createEvent(@RequestBody Dtos.EventRequest request) {
        Event event = Event.builder()
                .organizationId(request.organizationId())
                .name(request.name())
                .description(request.description())
                .venue(request.venue())
                .eventDate(request.eventDate())
                .capacity(request.capacity())
                .build();
        Event saved = eventService.create(event);
        Organization org = organizationService.getById(saved.getOrganizationId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Dtos.EventResponse.fromEntity(saved, org.getName(), 0L, false));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dtos.EventResponse> updateEvent(@PathVariable UUID id,
                                                          @RequestBody Dtos.EventRequest request) {
        Event updated = Event.builder()
                .organizationId(request.organizationId())
                .name(request.name())
                .description(request.description())
                .venue(request.venue())
                .eventDate(request.eventDate())
                .capacity(request.capacity())
                .build();
        Event saved = eventService.update(id, updated);
        Organization org = organizationService.getById(saved.getOrganizationId());
        return ResponseEntity.ok(Dtos.EventResponse.fromEntity(saved, org.getName(), eventService.getRegistrationCount(id), false));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private List<Dtos.EventResponse> buildEventResponses(List<Event> events) {
        return events.stream().map(event -> {
            try {
                Organization org = organizationService.getById(event.getOrganizationId());
                long registrationCount = eventService.getRegistrationCount(event.getId());
                return Dtos.EventResponse.fromEntity(event, org.getName(), registrationCount, false);
            } catch (Exception e) {
                return Dtos.EventResponse.fromEntity(event, "Unknown", 0L, false);
            }
        }).toList();
    }
}
