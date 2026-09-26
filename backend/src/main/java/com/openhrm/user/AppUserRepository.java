package com.openhrm.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    Optional<AppUser> findByOrganizationIdAndEmail(UUID organizationId, String email);

    // Login looks up by email alone since the tenant isn't known until the user is found.
    Optional<AppUser> findByEmail(String email);

    boolean existsByOrganizationIdAndEmail(UUID organizationId, String email);
}
