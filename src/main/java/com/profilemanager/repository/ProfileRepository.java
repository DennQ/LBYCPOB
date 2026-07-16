package com.profilemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.profilemanager.model.Profile;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
}
