package ph.edu.dlsu.lbycpob.profilemanager.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ph.edu.dlsu.lbycpob.profilemanager.model.Profile;
import ph.edu.dlsu.lbycpob.profilemanager.repository.ProfileRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    // Gets all profiles in alphabetical order.
    public List<Profile> listProfiles() {
        return profileRepository.findAllByOrderByNameAsc();
    }

    // Gets one profile using its ID.
    public Profile getProfile(UUID id) {
        return profileRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Profile not found."));
    }

    // Searches for a profile and returns the first matching result.
    public Profile lookupFirstMatch(String query) {
        String trimmed = query == null ? "" : query.trim();

        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(
                    "Name field is empty. Please enter a name to search."
            );
        }

        List<Profile> matches =
                profileRepository
                        .findByNameContainingIgnoreCaseOrderByNameAsc(trimmed);

        if (matches.isEmpty()) {
            throw new NoSuchElementException(
                    "No profile found matching \"" + trimmed + "\"."
            );
        }

        return matches.getFirst();
    }

    // Creates a new profile.
    @Transactional
    public Profile createProfile(String name) {
        String trimmed = name == null ? "" : name.trim();

        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(
                    "Name field is empty. Please enter a name."
            );
        }

        if (profileRepository.findByNameIgnoreCase(trimmed).isPresent()) {
            throw new IllegalStateException(
                    "A profile named \"" + trimmed + "\" already exists."
            );
        }

        Profile profile = Profile.builder()
                .name(trimmed)
                .build();

        return profileRepository.save(profile);
    }

    // Deletes an existing profile.
    @Transactional
    public void deleteProfile(UUID id) {
        if (!profileRepository.existsById(id)) {
            throw new NoSuchElementException("Profile not found.");
        }

        profileRepository.deleteById(id);
    }

    // Updates a profile's status.
    @Transactional
    public void updateStatus(UUID id, String status) {
        String trimmed = status == null ? "" : status.trim();

        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Status field is empty.");
        }

        getProfile(id).setStatus(trimmed);
    }

    // Updates a profile's quote.
    @Transactional
    public void updateQuote(UUID id, String quote) {
        String trimmed = quote == null ? "" : quote.trim();

        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Quote field is empty.");
        }

        getProfile(id).setQuote(trimmed);
    }
}