package com.profilemanager.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "friends")
public class Friend extends BaseEntity {

    @Column(name = "profile_id", nullable = false)
    private UUID profileId;

    @Column(name = "friend_id", nullable = false)
    private UUID friendId;

    public Friend() {}

    public Friend(UUID profileId, UUID friendId) {
        this.profileId = profileId;
        this.friendId = friendId;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public UUID getFriendId() {
        return friendId;
    }

    public void setFriendId(UUID friendId) {
        this.friendId = friendId;
    }
}
