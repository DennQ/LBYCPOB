package ph.edu.dlsu.lbycpob.profilemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ph.edu.dlsu.lbycpob.profilemanager.model.Profile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    List<Profile> findAllByOrderByNameAsc();

    Optional<Profile> findByNameIgnoreCase(String name);

    List<Profile> findByNameContainingIgnoreCaseOrderByNameAsc(String query);
}