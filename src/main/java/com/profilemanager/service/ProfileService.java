package com.profilemanager.service;

import com.profilemanager.model.Friend;
import com.profilemanager.model.Profile;
import com.profilemanager.repository.FriendRepository;
import com.profilemanager.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final FriendRepository friendRepository;
    private final ImageCompressionService imageCompressionService;
    private final SupabaseStorageService supabaseStorageService;

    public ProfileService(ProfileRepository profileRepository,
                          FriendRepository friendRepository,
                          ImageCompressionService imageCompressionService,
                          SupabaseStorageService supabaseStorageService) {
        this.profileRepository = profileRepository;
        this.friendRepository = friendRepository;
        this.imageCompressionService = imageCompressionService;
        this.supabaseStorageService = supabaseStorageService;
    }

    public List<Profile> listProfiles() {
        return profileRepository.findAllByOrderByNameAsc();
    }

    public Profile getProfile(UUID id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Profile not found"));
    }

    public List<Profile> getFriendsOf(UUID profileId) {
        List<Friend> friends = friendRepository.findByProfileId(profileId);
        if (friends.isEmpty()) return List.of();
        List<UUID> friendIds = friends.stream()
                .map(Friend::getFriendId)
                .collect(Collectors.toList());
        return profileRepository.findAllById(friendIds);
    }

    public Profile lookupFirstMatch(String query) {
        String trimmed = query == null ? "" : query.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be empty");
        }
        List<Profile> matches = profileRepository
                .findByNameContainingIgnoreCaseOrStudentIdContainingIgnoreCaseOrderByNameAsc(trimmed, trimmed);
        if (matches.isEmpty()) {
            throw new NoSuchElementException("No profile found matching \"" + trimmed + "\"");
        }
        return matches.get(0);
    }

    @Transactional
    public Profile createProfile(String name, String studentId, String course, Integer yearLevel) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        Profile profile = new Profile(name.trim(), studentId, course, yearLevel);
        return profileRepository.save(profile);
    }

    @Transactional
    public Profile updateStudentInfo(UUID id, String studentId, String course, Integer yearLevel) {
        Profile profile = getProfile(id);
        if (studentId != null) profile.setStudentId(studentId);
        if (course != null) profile.setCourse(course);
        if (yearLevel != null) profile.setYearLevel(yearLevel);
        return profileRepository.save(profile);
    }

    @Transactional
    public void deleteProfile(UUID id) {
        if (!profileRepository.existsById(id)) {
            throw new NoSuchElementException("Profile not found");
        }
        profileRepository.deleteById(id);
    }

    @Transactional
    public void updateStatus(UUID id, String status) {
        Profile profile = getProfile(id);
        profile.setStatus(status != null ? status.trim() : "");
        profileRepository.save(profile);
    }

    @Transactional
    public void updateQuote(UUID id, String quote) {
        Profile profile = getProfile(id);
        profile.setQuote(quote != null ? quote.trim() : "");
        profileRepository.save(profile);
    }

    @Transactional
    public void updatePictureUrl(UUID id, String pictureUrl) {
        Profile profile = getProfile(id);
        if (pictureUrl != null && !pictureUrl.trim().isEmpty()) {
            profile.setPicture(pictureUrl.trim());
            profileRepository.save(profile);
        }
    }

    @Transactional
    public String updatePictureFromUpload(UUID id, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file uploaded");
        }
        Profile profile = getProfile(id);
        try {
            byte[] original = file.getBytes();
            byte[] compressed = imageCompressionService.compressToWebp(original);
            String path = "avatars/" + id + ".webp";
            String url = supabaseStorageService.uploadAndGetPublicUrl(path, compressed, "image/webp");
            profile.setPicture(url);
            profileRepository.save(profile);
            return url;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload profile picture", e);
        }
    }

    @Transactional
    public String addFriend(UUID profileId, String friendName) {
        if (friendName == null || friendName.trim().isEmpty()) {
            throw new IllegalArgumentException("Friend name cannot be empty");
        }
        Profile friend = lookupFirstMatch(friendName.trim());
        if (friend.getId().equals(profileId)) {
            throw new IllegalArgumentException("Cannot add yourself as a friend");
        }
        boolean exists = friendRepository.existsByProfileIdAndFriendId(profileId, friend.getId()) ||
                         friendRepository.existsByProfileIdAndFriendId(friend.getId(), profileId);
        if (exists) {
            throw new IllegalStateException("Already friends");
        }
        Friend friend1 = new Friend(profileId, friend.getId());
        Friend friend2 = new Friend(friend.getId(), profileId);
        friendRepository.save(friend1);
        friendRepository.save(friend2);
        return friend.getName();
    }

    @Transactional
    public String removeFriend(UUID profileId, String friendName) {
        if (friendName == null || friendName.trim().isEmpty()) {
            throw new IllegalArgumentException("Friend name cannot be empty");
        }
        Profile friend = lookupFirstMatch(friendName.trim());
        friendRepository.deleteByProfileIdAndFriendId(profileId, friend.getId());
        friendRepository.deleteByProfileIdAndFriendId(friend.getId(), profileId);
        return friend.getName();
    }
}
