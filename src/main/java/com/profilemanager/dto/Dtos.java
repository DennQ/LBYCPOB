package com.profilemanager.dto;

import com.profilemanager.model.Profile;
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
}