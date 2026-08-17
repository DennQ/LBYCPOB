package com.profilemanager.service;

import com.profilemanager.model.Event;
import com.profilemanager.model.EventRegistration;
import com.profilemanager.model.Organization;
import com.profilemanager.repository.EventRepository;
import com.profilemanager.repository.EventRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final OrganizationService organizationService;

    public EventService(EventRepository eventRepository,
                        EventRegistrationRepository registrationRepository,
                        OrganizationService organizationService) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.organizationService = organizationService;
    }

    public List<Event> listAll() {
        return eventRepository.findAllByOrderByEventDateAsc();
    }

    public List<Event> findByOrganization(UUID organizationId) {
        return eventRepository.findByOrganizationIdOrderByEventDateAsc(organizationId);
    }

    public List<Event> findUpcomingEvents() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime twoWeeksFromNow = now.plusDays(14);
        return eventRepository.findUpcomingEvents(now, twoWeeksFromNow);
    }

    public List<Event> findUpcomingEventsByOrganization(UUID organizationId) {
        return eventRepository.findUpcomingEventsByOrganization(organizationId, OffsetDateTime.now());
    }

    public Event getById(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with id: " + id));
    }

    @Transactional
    public Event create(Event event) {
        if (event.getName() == null || event.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Event name is required.");
        }
        if (event.getOrganizationId() == null) {
            throw new IllegalArgumentException("Organization ID is required.");
        }
        if (event.getEventDate() == null) {
            throw new IllegalArgumentException("Event date is required.");
        }
        organizationService.getById(event.getOrganizationId());
        event.setName(event.getName().trim());
        if (event.getCapacity() == null) {
            event.setCapacity(0);
        }
        return eventRepository.save(event);
    }

    @Transactional
    public Event update(UUID id, Event updated) {
        Event existing = getById(id);
        if (updated.getName() != null && !updated.getName().trim().isEmpty()) {
            existing.setName(updated.getName().trim());
        }
        if (updated.getDescription() != null) {
            existing.setDescription(updated.getDescription());
        }
        if (updated.getVenue() != null) {
            existing.setVenue(updated.getVenue());
        }
        if (updated.getEventDate() != null) {
            existing.setEventDate(updated.getEventDate());
        }
        if (updated.getCapacity() != null) {
            existing.setCapacity(updated.getCapacity());
        }
        return eventRepository.save(existing);
    }

    @Transactional
    public void delete(UUID id) {
        if (!eventRepository.existsById(id)) {
            throw new NoSuchElementException("Event not found with id: " + id);
        }
        eventRepository.deleteById(id);
    }

    public long getRegistrationCount(UUID eventId) {
        return registrationRepository.countByEventId(eventId);
    }

    public boolean isUserRegistered(UUID eventId, UUID profileId) {
        return registrationRepository.existsByEventIdAndProfileId(eventId, profileId);
    }
}
