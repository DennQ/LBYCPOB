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

    // get all files in order

    public List<Profile> listProfiles() {
        return profileRepository.findAllByOrderByNameAsc();
    }

     // Retrieves one profile using its ID.

    public Profile getProfile(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Profile ID is required.");
        }

        return profileRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Profile not found."));
    }


     //  Searches for profiles whose names contain the search text.
    public List<Profile> searchProfiles(String query) {
        String cleanedQuery = cleanRequiredText(
                query,
                "Please enter a profile name to search."
        );

        return profileRepository
                .findByNameContainingIgnoreCaseOrderByNameAsc(cleanedQuery);
    }

     //Returns the first alphabetical profile matching the search text.

    public Profile lookupFirstMatch(String query) {
        List<Profile> matches = searchProfiles(query);

        if (matches.isEmpty()) {
            throw new NoSuchElementException(
                    "No profile matched the search."
            );
        }

        return matches.get(0);
    }

     // Creates a new profile.

    @Transactional
    public Profile createProfile(String name) {
        String cleanedName = cleanRequiredText(
                name,
                "Please enter a profile name."
        );

        if (profileRepository.findByNameIgnoreCase(cleanedName).isPresent()) {
            throw new IllegalStateException(
                    "A profile with this name already exists."
            );
        }

        Profile profile = Profile.builder()
                .name(cleanedName)
                .build();

        return profileRepository.save(profile);
    }


     // Deletes an existing profile.

    @Transactional
    public void deleteProfile(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Profile ID is required.");
        }

        if (!profileRepository.existsById(id)) {
            throw new NoSuchElementException("Profile not found.");
        }

        profileRepository.deleteById(id);
    }

     // Updates a profile's status.

    @Transactional
    public Profile updateStatus(UUID id, String status) {
        String cleanedStatus = cleanRequiredText(
                status,
                "Please enter a status."
        );

        Profile profile = getProfile(id);
        profile.setStatus(cleanedStatus);

        return profileRepository.save(profile);
    }

     // Updates a profile's quote.

    @Transactional
    public Profile updateQuote(UUID id, String quote) {
        String cleanedQuote = cleanRequiredText(
                quote,
                "Please enter a quote."
        );

        Profile profile = getProfile(id);
        profile.setQuote(cleanedQuote);

        return profileRepository.save(profile);
    }

    // Removes extra spaces and rejects null or blank values.

    private String cleanRequiredText(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }

        return value.trim();
    }
}