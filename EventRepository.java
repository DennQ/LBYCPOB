//Goes in repository folder

package com.profilemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.profilemanager.model.Event;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> findAllByOrderByStartTimeAsc();

    // "Upcoming events" -- everything starting now or later.
    List<Event> findByStartTimeGreaterThanEqualOrderByStartTimeAsc(OffsetDateTime from);

    // "Events within the next 2 weeks" -- start_time inside [from, to].
    List<Event> findByStartTimeBetweenOrderByStartTimeAsc(OffsetDateTime from, OffsetDateTime to);
}
