package com.profilemanager.repository;

import com.profilemanager.model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, UUID> {
    Optional<EventRegistration> findByEventIdAndProfileId(UUID eventId, UUID profileId);
    List<EventRegistration> findByProfileId(UUID profileId);
    List<EventRegistration> findByEventId(UUID eventId);
    long countByEventId(UUID eventId);
    void deleteByEventIdAndProfileId(UUID eventId, UUID profileId);
    boolean existsByEventIdAndProfileId(UUID eventId, UUID profileId);
}
