package com.profilemanager.service;

import com.profilemanager.model.EventRegistration;
import com.profilemanager.repository.EventRegistrationRepository;
import com.profilemanager.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class EventRegistrationService {
    private final EventRegistrationRepository registrationRepository;
    private final EventService eventService;
    private final ProfileRepository profileRepository;

    public EventRegistrationService(EventRegistrationRepository registrationRepository,
                                    EventService eventService,
                                    ProfileRepository profileRepository) {
        this.registrationRepository = registrationRepository;
        this.eventService = eventService;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public EventRegistration register(UUID eventId, UUID profileId) {
        eventService.getById(eventId);
        if (!profileRepository.existsById(profileId)) {
            throw new NoSuchElementException("Profile not found: " + profileId);
        }
        if (registrationRepository.existsByEventIdAndProfileId(eventId, profileId)) {
            throw new IllegalStateException("User is already registered for this event");
        }
        EventRegistration registration = new EventRegistration(eventId, profileId);
        return registrationRepository.save(registration);
    }

    @Transactional
    public void cancelRegistration(UUID eventId, UUID profileId) {
        if (!registrationRepository.existsByEventIdAndProfileId(eventId, profileId)) {
            throw new NoSuchElementException("Registration not found");
        }
        registrationRepository.deleteByEventIdAndProfileId(eventId, profileId);
    }

    public List<EventRegistration> getByEventId(UUID eventId) {
        return registrationRepository.findByEventId(eventId);
    }

    public List<EventRegistration> getByProfileId(UUID profileId) {
        return registrationRepository.findByProfileId(profileId);
    }

    public boolean isRegistered(UUID eventId, UUID profileId) {
        return registrationRepository.existsByEventIdAndProfileId(eventId, profileId);
    }

    public long getRegistrationCount(UUID eventId) {
        return registrationRepository.countByEventId(eventId);
    }
}
