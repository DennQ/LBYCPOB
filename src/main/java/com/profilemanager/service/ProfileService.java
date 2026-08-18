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

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final FriendRepository friendRepository;
    private final ImageCompressionService imageCompressionService;
    private final SupabaseStorageService supabaseStorageService;

    public ProfileService(
            ProfileRepository profileRepository,
            FriendRepository friendRepository,
            ImageCompressionService imageCompressionService,
            SupabaseStorageService supabaseStorageService
    ) {
        this.profileRepository = profileRepository;
        this.friendRepository = friendRepository;
        this.imageCompressionService = imageCompressionService;
        this.supabaseStorageService = supabaseStorageService;
    }

    // ============================================================
    // PROFILE LIST / VIEW
    // ============================================================

    public List<Profile> listProfiles() {
        return profileRepository.findAllByOrderByNameAsc();
    }

    public Profile getProfile(UUID id) {
        return profileRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Student profile not found."));
    }

    public List<Profile> getFriendsOf(UUID profileId) {

        List<UUID> friendIds =
                friendRepository.findByProfileId(profileId)
                        .stream()
                        .map(Friend::getFriendId)
                        .toList();

        if (friendIds.isEmpty()) {
            return List.of();
        }

        return profileRepository.findAllById(friendIds);
    }

    // ============================================================
    // SEARCH STUDENT
    // ============================================================

    public Profile lookupFirstMatch(String query) {

        String search = clean(query);

        if (search.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please enter a student name or student ID."
            );
        }

        List<Profile> matches =
                profileRepository
                        .findByNameContainingIgnoreCaseOrStudentIdContainingIgnoreCaseOrderByNameAsc(
                                search,
                                search
                        );

        if (matches.isEmpty()) {
            throw new NoSuchElementException(
                    "No student found matching \"" + search + "\"."
            );
        }

        return matches.getFirst();
    }

    // ============================================================
    // CREATE STUDENT PROFILE
    // ============================================================

    @Transactional
    public Profile createProfile(
            String name,
            String studentId,
            String course,
            Integer yearLevel
    ) {

        String cleanName = clean(name);
        String cleanStudentId = clean(studentId);
        String cleanCourse = clean(course);

        if (cleanName.isEmpty()) {
            throw new IllegalArgumentException(
                    "Student name is required."
            );
        }

        if (cleanStudentId.isEmpty()) {
            throw new IllegalArgumentException(
                    "Student ID is required."
            );
        }

        if (cleanCourse.isEmpty()) {
            throw new IllegalArgumentException(
                    "Course is required."
            );
        }

        if (yearLevel == null || yearLevel <= 0) {
            throw new IllegalArgumentException(
                    "Year level must be greater than 0."
            );
        }

        if (profileRepository.findByNameIgnoreCase(cleanName).isPresent()) {
            throw new IllegalStateException(
                    "A student named \"" + cleanName + "\" already exists."
            );
        }

        if (profileRepository
                .findByStudentIdIgnoreCase(cleanStudentId)
                .isPresent()) {

            throw new IllegalStateException(
                    "Student ID \"" + cleanStudentId + "\" is already in use."
            );
        }

        Profile student = Profile.builder()
                .name(cleanName)
                .studentId(cleanStudentId)
                .course(cleanCourse)
                .yearLevel(yearLevel)
                .build();

        return profileRepository.save(student);
    }

    /*
     * Temporary compatibility method.
     *
     * We keep this until ProfileController is changed in the
     * next commit so the existing controller still compiles.
     */
    @Transactional
    public Profile createProfile(String name) {

        return createProfile(
                name,
                "TEMP-" + UUID.randomUUID(),
                "Not Set",
                1
        );
    }

    // ============================================================
    // UPDATE STUDENT INFORMATION
    // ============================================================

    @Transactional
    public Profile updateStudentInfo(
            UUID id,
            String studentId,
            String course,
            Integer yearLevel
    ) {

        Profile profile = getProfile(id);

        String cleanStudentId = clean(studentId);
        String cleanCourse = clean(course);

        if (cleanStudentId.isEmpty()) {
            throw new IllegalArgumentException(
                    "Student ID is required."
            );
        }

        if (cleanCourse.isEmpty()) {
            throw new IllegalArgumentException(
                    "Course is required."
            );
        }

        if (yearLevel == null || yearLevel <= 0) {
            throw new IllegalArgumentException(
                    "Year level must be greater than 0."
            );
        }

        profileRepository
                .findByStudentIdIgnoreCase(cleanStudentId)
                .ifPresent(existing -> {

                    if (!existing.getId().equals(profile.getId())) {
                        throw new IllegalStateException(
                                "Student ID \"" +
                                        cleanStudentId +
                                        "\" is already in use."
                        );
                    }
                });

        profile.setStudentId(cleanStudentId);
        profile.setCourse(cleanCourse);
        profile.setYearLevel(yearLevel);

        return profile;
    }

    // ============================================================
    // DELETE PROFILE
    // ============================================================

    @Transactional
    public void deleteProfile(UUID id) {

        if (!profileRepository.existsById(id)) {
            throw new NoSuchElementException(
                    "Student profile not found."
            );
        }

        profileRepository.deleteById(id);
    }

    // ============================================================
    // STATUS
    // ============================================================

    @Transactional
    public void updateStatus(UUID id, String status) {

        String value = clean(status);

        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                    "Status field is empty."
            );
        }

        getProfile(id).setStatus(value);
    }

    // ============================================================
    // QUOTE
    // ============================================================

    @Transactional
    public void updateQuote(UUID id, String quote) {

        String value = clean(quote);

        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                    "Quote field is empty."
            );
        }

        getProfile(id).setQuote(value);
    }

    // ============================================================
    // PROFILE PICTURE - URL
    // ============================================================

    @Transactional
    public void updatePictureUrl(
            UUID id,
            String pictureUrl
    ) {

        String value = clean(pictureUrl);

        if (!value.startsWith("https://")) {
            throw new IllegalArgumentException(
                    "URL must start with https://"
            );
        }

        getProfile(id).setPicture(value);
    }

    // ============================================================
    // PROFILE PICTURE - FILE UPLOAD
    // ============================================================

    @Transactional
    public String updatePictureFromUpload(
            UUID id,
            MultipartFile file
    ) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "No file was uploaded."
            );
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !contentType.startsWith("image/")) {

            throw new IllegalArgumentException(
                    "The selected file is not an image."
            );
        }

        Profile profile = getProfile(id);

        byte[] original;

        try {
            original = file.getBytes();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not read the uploaded file."
            );
        }

        byte[] webp =
                imageCompressionService.compressToWebp(original);

        String path =
                "avatars/" + id + ".webp";

        String publicUrl =
                supabaseStorageService.uploadAndGetPublicUrl(
                        path,
                        webp,
                        "image/webp"
                );

        profile.setPicture(publicUrl);

        return publicUrl;
    }

    // ============================================================
    // FRIEND MANAGEMENT
    // ============================================================

    @Transactional
    public String addFriend(
            UUID profileId,
            String friendName
    ) {

        Profile self = getProfile(profileId);
        Profile friend = findByNameOrThrow(friendName);

        if (friend.getId().equals(self.getId())) {
            throw new IllegalArgumentException(
                    "A profile cannot be friends with itself."
            );
        }

        boolean forwardExists =
                friendRepository
                        .existsByProfileIdAndFriendId(
                                self.getId(),
                                friend.getId()
                        );

        boolean reverseExists =
                friendRepository
                        .existsByProfileIdAndFriendId(
                                friend.getId(),
                                self.getId()
                        );

        if (forwardExists && reverseExists) {
            throw new IllegalStateException(
                    "\"" + friend.getName() +
                            "\" is already a friend."
            );
        }

        if (!forwardExists) {
            friendRepository.save(
                    Friend.builder()
                            .profileId(self.getId())
                            .friendId(friend.getId())
                            .build()
            );
        }

        if (!reverseExists) {
            friendRepository.save(
                    Friend.builder()
                            .profileId(friend.getId())
                            .friendId(self.getId())
                            .build()
            );
        }

        return friend.getName();
    }

    @Transactional
    public String removeFriend(
            UUID profileId,
            String friendName
    ) {

        Profile friend = findByNameOrThrow(friendName);

        friendRepository.deleteByProfileIdAndFriendId(
                profileId,
                friend.getId()
        );

        friendRepository.deleteByProfileIdAndFriendId(
                friend.getId(),
                profileId
        );

        return friend.getName();
    }

    private Profile findByNameOrThrow(String friendName) {

        String name = clean(friendName);

        if (name.isEmpty()) {
            throw new IllegalArgumentException(
                    "Friend name field is empty."
            );
        }

        return profileRepository
                .findByNameIgnoreCase(name)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "No profile named \"" +
                                        name +
                                        "\" exists."
                        )
                );
    }

    // ============================================================
    // HELPER
    // ============================================================

    private String clean(String value) {
        return value == null
                ? ""
                : value.trim();
    }
}