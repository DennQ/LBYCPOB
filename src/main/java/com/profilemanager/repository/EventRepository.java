package com.profilemanager.repository;

import com.profilemanager.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByOrganizationIdOrderByEventDateAsc(UUID organizationId);
    List<Event> findByEventDateGreaterThanEqualOrderByEventDateAsc(OffsetDateTime from);
    @Query("SELECT e FROM Event e WHERE e.eventDate BETWEEN :now AND :twoWeeksFromNow ORDER BY e.eventDate ASC")
    List<Event> findUpcomingEvents(@Param("now") OffsetDateTime now, @Param("twoWeeksFromNow") OffsetDateTime twoWeeksFromNow);
    @Query("SELECT e FROM Event e WHERE e.organizationId = :orgId AND e.eventDate > :now ORDER BY e.eventDate ASC")
    List<Event> findUpcomingEventsByOrganization(@Param("orgId") UUID orgId, @Param("now") OffsetDateTime now);
}
