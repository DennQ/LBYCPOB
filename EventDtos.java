//Goes in dto folder

package com.profilemanager.dto;

import com.profilemanager.model.Event;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class EventDtos {
    private EventDtos() {
    }

    
    public record EventListItem(UUID id, String title, String location,
                                 OffsetDateTime startTime, OffsetDateTime endTime,
                                 long registrationCount) {
        public static EventListItem of(Event e, long registrationCount) {
            return new EventListItem(e.getId(), e.getTitle(), e.getLocation(),
                    e.getStartTime(), e.getEndTime(), registrationCount);
        }
    }

    
    public record EventDetail(UUID id, String title, String description, String location,
                               OffsetDateTime startTime, OffsetDateTime endTime,
                               long registrationCount, boolean isRegistered,
                               List<Dtos.NameRef> registrants) {
        public static EventDetail of(Event e, long registrationCount, boolean isRegistered,
                                      List<Dtos.NameRef> registrants) {
            return new EventDetail(e.getId(), e.getTitle(), e.getDescription(), e.getLocation(),
                    e.getStartTime(), e.getEndTime(), registrationCount, isRegistered, registrants);
        }
    }

    public record NewEventRequest(String title, String description, String location,
                                   OffsetDateTime startTime, OffsetDateTime endTime) {
    }

    public record RegisterRequest(String profileName) {
    }

    public record RegistrationResult(String profileName, String eventTitle,
                                      long registrationCount, OffsetDateTime registeredAt) {
    }
}
