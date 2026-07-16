package ph.edu.dlsu.lbycpob.profilemanager.model;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class Profile {

    private static final String DEFAULT_PICTURE =
            "https://6fkrqtkwbcnqsois.public.blob.vercel-storage.com/avatars/default.webp";

    private UUID id;
    private String name;
    private String status = "";
    private String quote = "";
    private String picture = DEFAULT_PICTURE;
    private OffsetDateTime createdAt;

    public Profile() {
    }

    public Profile(
            UUID id,
            String name,
            String status,
            String quote,
            String picture,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.quote = quote;
        this.picture = picture;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Profile profile)) {
            return false;
        }

        return Objects.equals(id, profile.id)
                && Objects.equals(name, profile.name)
                && Objects.equals(status, profile.status)
                && Objects.equals(quote, profile.quote)
                && Objects.equals(picture, profile.picture)
                && Objects.equals(createdAt, profile.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                name,
                status,
                quote,
                picture,
                createdAt
        );
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", quote='" + quote + '\'' +
                ", picture='" + picture + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private UUID id;
        private String name;
        private String status = "";
        private String quote = "";
        private String picture = DEFAULT_PICTURE;
        private OffsetDateTime createdAt;

        private Builder() {
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder quote(String quote) {
            this.quote = quote;
            return this;
        }

        public Builder picture(String picture) {
            this.picture = picture;
            return this;
        }

        public Builder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Profile build() {
            return new Profile(
                    id,
                    name,
                    status,
                    quote,
                    picture,
                    createdAt
            );
        }
    }
}