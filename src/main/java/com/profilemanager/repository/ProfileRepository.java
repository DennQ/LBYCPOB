package com.profilemanager.repository;

import com.profilemanager.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Represents the interface component in the SocialNet system. */
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    List<Profile> findAllByOrderByNameAsc();

    Optional<Profile> findByNameIgnoreCase(String name);

    Optional<Profile> findByStudentIdIgnoreCase(String studentId);

    List<Profile>
    findByNameContainingIgnoreCaseOrStudentIdContainingIgnoreCaseOrderByNameAsc(
            String name,
            String studentId
    );
}