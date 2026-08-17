package com.profilemanager.controller;

import org.springframework.web.bind.annotation.*;
import com.profilemanager.dto.EventDtos;
import com.profilemanager.dto.EventDtos.*;
import com.profilemanager.model.Event;
import com.profilemanager.service.EventService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventDtos.EventListItem> listEvents(
            @RequestParam(defaultValue = "all") String scope) {

        List<Event> events = switch (scope) {
            case "upcoming" -> eventService.listUpcomingEvents();
            case "next-two-weeks" -> eventService.listEventsWithinNextTwoWeeks();
            default -> eventService.listAllEvents();
        };

        return events.stream()
                .map(e -> EventListItem.of(e, eventService.getRegistrationCount(e.getId())))
                .toList();
    }

    /**
     * Event details page. Pass ?profileName=... to also learn whether
     * that profile is already registered (for the Register/Cancel
     * button state and the "You are registered" indicator).
     */
    @GetMapping("/{id}")
    public EventDetail getEvent(@PathVariable UUID id,
                                 @RequestParam(required = false) String profileName) {
        Event event = eventService.getEvent(id);
        long count = eventService.getRegistrationCount(id);
        List<com.profilemanager.dto.Dtos.NameRef> registrants = eventService.getRegistrants(id);
        boolean registered = profileName != null && !profileName.isBlank()
                && registrants.stream().anyMatch(r -> r.name().equalsIgnoreCase(profileName.trim()));
        return EventDetail.of(event, count, registered, registrants);
    }

    @PostMapping
    public EventDetail createEvent(@RequestBody NewEventRequest request) {
        Event created = eventService.createEvent(
                request.title(), request.description(), request.location(),
                request.startTime(), request.endTime());
        return EventDetail.of(created, 0, false, List.of());
    }

    @PostMapping("/{id}/register")
    public RegistrationResult register(@PathVariable UUID id, @RequestBody RegisterRequest request) {
        Event event = eventService.getEvent(id);
        long count = eventService.registerForEvent(id, request.profileName());
        return new RegistrationResult(request.profileName().trim(), event.getTitle(), count,
                java.time.OffsetDateTime.now());
    }

    @DeleteMapping("/{id}/register")
    public Map<String, Object> cancelRegistration(@PathVariable UUID id, @RequestBody RegisterRequest request) {
        Event event = eventService.getEvent(id);
        long count = eventService.cancelRegistration(id, request.profileName());
        return Map.of(
                "profileName", request.profileName().trim(),
                "eventTitle", event.getTitle(),
                "registrationCount", count
        );
    }
}
