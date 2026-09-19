package com.microcrew.user.repository;

import com.microcrew.user.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByAuthUserId(UUID authUserId);

    boolean existsByAuthUserId(UUID authUserId);
}
