package com.profilemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import com.profilemanager.model.EventRegistration;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, UUID> {

    List<EventRegistration> findByEventId(UUID eventId);

    List<EventRegistration> findByProfileId(UUID profileId);

    long countByEventId(UUID eventId);

    boolean existsByEventIdAndProfileId(UUID eventId, UUID profileId);

    Optional<EventRegistration> findByEventIdAndProfileId(UUID eventId, UUID profileId);

    @Transactional
    void deleteByEventIdAndProfileId(UUID eventId, UUID profileId);
}
