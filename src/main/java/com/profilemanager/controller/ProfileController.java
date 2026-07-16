package com.profilemanager.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.profilemanager.dto.Dtos;
import com.profilemanager.model.Profile;
import com.profilemanager.repository.ProfileRepository;
import com.profilemanager.service.ImageCompressionService;
import com.profilemanager.service.SupabaseStorageService;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileRepository profileRepository;
    private final ImageCompressionService imageCompressionService;
    private final SupabaseStorageService supabaseStorageService;

    public ProfileController(
            ProfileRepository profileRepository,
            ImageCompressionService imageCompressionService,
            SupabaseStorageService supabaseStorageService) {
        this.profileRepository = profileRepository;
        this.imageCompressionService = imageCompressionService;
        this.supabaseStorageService = supabaseStorageService;
    }

    // Basic CRUD (will be replaced by Julio)
    @GetMapping
    public List<Dtos.ProfileListItem> listProfiles() {
        return profileRepository.findAll().stream()
                .map(Dtos.ProfileListItem::of)
                .toList();
    }

    @GetMapping("/{id}")
    public Dtos.ProfileDetail getProfile(@PathVariable UUID id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Profile not found."));
        return Dtos.ProfileDetail.of(profile, List.of());
    }

    @PostMapping
    public Dtos.ProfileDetail createProfile(@RequestBody Dtos.NewProfileRequest request) {
        String trimmed = request.name().trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        Profile profile = new Profile();
        profile.setName(trimmed);
        Profile saved = profileRepository.save(profile);
        return Dtos.ProfileDetail.of(saved, List.of());
    }

    // === IMAGE ENDPOINTS ===
    @PostMapping(value = "/{id}/avatar", consumes = "multipart/form-data")
    public Dtos.PictureResult uploadAvatar(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file was uploaded.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("The selected file is not an image.");
        }

        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Profile not found."));

        byte[] originalBytes;
        try {
            originalBytes = file.getBytes();
        } catch (Exception e) {
            throw new IllegalStateException("Could not read the uploaded file.");
        }

        byte[] webpBytes;
        try {
            webpBytes = imageCompressionService.compressToWebp(originalBytes);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Image compression failed: " + e.getMessage(), e);
        }

        String path = "avatars/" + id + ".webp";
        String publicUrl;
        try {
            publicUrl = supabaseStorageService.uploadAndGetPublicUrl(path, webpBytes, "image/webp");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to upload image to storage: " + e.getMessage(), e);
        }

        profile.setPicture(publicUrl);
        profileRepository.save(profile);

        return new Dtos.PictureResult(publicUrl);
    }

    @PatchMapping("/{id}/picture")
    public Map<String, String> updatePictureUrl(
            @PathVariable UUID id,
            @RequestBody Dtos.UpdatePictureRequest request) {

        String trimmed = request.pictureUrl().trim();
        if (!trimmed.startsWith("https://")) {
            throw new IllegalArgumentException("URL must start with https://");
        }

        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Profile not found."));

        profile.setPicture(trimmed);
        profileRepository.save(profile);

        return Map.of("picture", trimmed);
    }
}
