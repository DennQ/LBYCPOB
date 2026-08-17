package com.profilemanager.dto;

import com.profilemanager.model.Event;
import com.profilemanager.model.EventRegistration;
import com.profilemanager.model.Organization;
import com.profilemanager.model.Profile;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class Dtos {

    private Dtos() {}

    public record ProfileListItem(UUID id, String name, String picture) {
        public static ProfileListItem of(Profile p) {
            return new ProfileListItem(p.getId(), p.getName(), p.getPicture());
        }
    }

    public record NameRef(UUID id, String name) {
        public static NameRef of(Profile p) {
            return new NameRef(p.getId(), p.getName());
        }
    }

    public record ProfileDetail(UUID id, String name, String status, String quote,
                                String picture, List<NameRef> friends) {
        public static ProfileDetail of(Profile p, List<Profile> friends) {
            return new ProfileDetail(
                    p.getId(), p.getName(), p.getStatus(), p.getQuote(), p.getPicture(),
                    friends.stream().map(NameRef::of).toList()
            );
        }
    }

    public record NewProfileRequest(String name) {}
    public record UpdateStatusRequest(String status) {}
    public record UpdateQuoteRequest(String quote) {}
    public record FriendActionRequest(String friendName) {}
    public record ApiError(String error) {}
    public record UpdatePictureRequest(String pictureUrl) {}
    public record PictureResult(String url) {}

    public record OrganizationRequest(
            String name,
            String description,
            String category,
            String email,
            String logoUrl
    ) {}

    public record OrganizationResponse(
            UUID id,
            String name,
            String description,
            String category,
            String email,
            String logoUrl,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt,
            Long eventCount
    ) {
        public static OrganizationResponse fromEntity(Organization org, Long eventCount) {
            return new OrganizationResponse(
                    org.getId(),
                    org.getName(),
                    org.getDescription(),
                    org.getCategory(),
                    org.getEmail(),
                    org.getLogoUrl(),
                    org.getCreatedAt(),
                    org.getUpdatedAt(),
                    eventCount != null ? eventCount : 0L
            );
        }

        public static OrganizationResponse fromEntity(Organization org) {
            return fromEntity(org, 0L);
        }
    }

    public record EventRequest(
            String name,
            String description,
            String venue,
            OffsetDateTime eventDate,
            Integer capacity,
            UUID organizationId
    ) {}

    public record EventResponse(
            UUID id,
            String name,
            String description,
            String venue,
            OffsetDateTime eventDate,
            Integer capacity,
            UUID organizationId,
            String organizationName,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt,
            Long registrationCount,
            Boolean isRegistered
    ) {
        public static EventResponse fromEntity(Event event, String organizationName,
                                               Long registrationCount, Boolean isRegistered) {
            return new EventResponse(
                    event.getId(),
                    event.getName(),
                    event.getDescription(),
                    event.getVenue(),
                    event.getEventDate(),
                    event.getCapacity(),
                    event.getOrganizationId(),
                    organizationName != null ? organizationName : "",
                    event.getCreatedAt(),
                    event.getUpdatedAt(),
                    registrationCount != null ? registrationCount : 0L,
                    isRegistered != null ? isRegistered : false
            );
        }
    }

    public record RegistrationRequest(
            UUID eventId,
            UUID profileId
    ) {}

    public record RegistrationResponse(
            UUID id,
            UUID eventId,
            UUID profileId,
            OffsetDateTime registrationDate,
            String status
    ) {
        public static RegistrationResponse fromEntity(EventRegistration registration) {
            return new RegistrationResponse(
                    registration.getId(),
                    registration.getEventId(),
                    registration.getProfileId(),
                    registration.getRegistrationDate(),
                    registration.getStatus()
            );
        }
    }
}
