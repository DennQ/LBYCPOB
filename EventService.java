//Goes in service folder

package com.profilemanager.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.profilemanager.dto.Dtos;
import com.profilemanager.model.Event;
import com.profilemanager.model.EventRegistration;
import com.profilemanager.model.Profile;
import com.profilemanager.repository.EventRegistrationRepository;
import com.profilemanager.repository.EventRepository;
import com.profilemanager.repository.ProfileRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final ProfileRepository profileRepository;

    public EventService(EventRepository eventRepository,
                         EventRegistrationRepository registrationRepository,
                         ProfileRepository profileRepository) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.profileRepository = profileRepository;
    }

    // ============================================================
    // Discovery
    // ============================================================

    public List<Event> listAllEvents() {
        return eventRepository.findAllByOrderByStartTimeAsc();
    }

    /** Upcoming events page: everything starting now or later. */
    public List<Event> listUpcomingEvents() {
        return eventRepository.findByStartTimeGreaterThanEqualOrderByStartTimeAsc(OffsetDateTime.now());
    }

    /** Events within the next 2 weeks (14 days) from now. */
    public List<Event> listEventsWithinNextTwoWeeks() {
        OffsetDateTime now = OffsetDateTime.now();
        return eventRepository.findByStartTimeBetweenOrderByStartTimeAsc(now, now.plusDays(14));
    }

    public Event getEvent(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found."));
    }

    // ============================================================
    // Registration status / count
    // ============================================================

    public long getRegistrationCount(UUID eventId) {
        return registrationRepository.countByEventId(eventId);
    }

    public boolean isRegistered(UUID eventId, UUID profileId) {
        if (profileId == null) return false;
        return registrationRepository.existsByEventIdAndProfileId(eventId, profileId);
    }

    public List<Dtos.NameRef> getRegistrants(UUID eventId) {
        List<UUID> profileIds = registrationRepository.findByEventId(eventId).stream()
                .map(EventRegistration::getProfileId)
                .toList();
        if (profileIds.isEmpty()) return List.of();
        return profileRepository.findAllById(profileIds).stream().map(Dtos.NameRef::of).toList();
    }

    // ============================================================
    // Register / cancel
    // ============================================================

    /**
     * Registers a profile (looked up by name, same convention as
     * ProfileService's friend actions) for an event. Prevents duplicate
     * registration by checking first, so we can throw a friendly
     * IllegalStateException instead of a raw constraint-violation error.
     */
    @Transactional
    public long registerForEvent(UUID eventId, String profileName) {
        Event event = getEvent(eventId);
        Profile profile = findProfileOrThrow(profileName);

        if (registrationRepository.existsByEventIdAndProfileId(event.getId(), profile.getId())) {
            throw new IllegalStateException(
                    "\"" + profile.getName() + "\" is already registered for \"" + event.getTitle() + "\".");
        }

        registrationRepository.save(EventRegistration.builder()
                .eventId(event.getId())
                .profileId(profile.getId())
                .build());

        return registrationRepository.countByEventId(event.getId());
    }

    @Transactional
    public long cancelRegistration(UUID eventId, String profileName) {
        Event event = getEvent(eventId);
        Profile profile = findProfileOrThrow(profileName);

        if (!registrationRepository.existsByEventIdAndProfileId(event.getId(), profile.getId())) {
            throw new NoSuchElementException(
                    "\"" + profile.getName() + "\" is not registered for \"" + event.getTitle() + "\".");
        }

        registrationRepository.deleteByEventIdAndProfileId(event.getId(), profile.getId());
        return registrationRepository.countByEventId(event.getId());
    }

    // ============================================================
    // Event creation (basic admin/test helper -- Member 3's checklist
    // is discovery + registration, but there needs to be a way to get
    // sample events into the table; see sql/events_schema.sql for
    // seeding via SQL instead, if preferred).
    // ============================================================

    @Transactional
    public Event createEvent(String title, String description, String location,
                              OffsetDateTime startTime, OffsetDateTime endTime) {
        String trimmedTitle = title == null ? "" : title.trim();
        if (trimmedTitle.isEmpty()) {
            throw new IllegalArgumentException("Title field is empty. Please enter a title.");
        }
        if (startTime == null) {
            throw new IllegalArgumentException("Start time is required.");
        }
        if (endTime != null && endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time cannot be before start time.");
        }
        return eventRepository.save(Event.builder()
                .title(trimmedTitle)
                .description(description == null ? "" : description.trim())
                .location(location == null ? "" : location.trim())
                .startTime(startTime)
                .endTime(endTime)
                .build());
    }

    private Profile findProfileOrThrow(String profileName) {
        String trimmed = profileName == null ? "" : profileName.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Your profile name is empty. Enter it above first.");
        }
        return profileRepository.findByNameIgnoreCase(trimmed)
                .orElseThrow(() -> new NoSuchElementException(
                        "No profile named \"" + trimmed + "\" exists. Add that profile first."));
    }
}
