package com.microcrew.user.service;

import com.microcrew.common.ConflictException;
import com.microcrew.common.ResourceNotFoundException;
import com.microcrew.user.dto.CreateProfileRequest;
import com.microcrew.user.dto.CurrentUserResponse;
import com.microcrew.user.dto.UpdateProfileRequest;
import com.microcrew.user.entity.AppUser;
import com.microcrew.user.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final AppUserRepository appUserRepository;

    public UserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    /**
     * Retrieves the profile for the authenticated Supabase user.
     *
     * @param authUserId the Supabase auth.users.id UUID from verified JWT
     * @return CurrentUserResponse DTO
     * @throws ResourceNotFoundException if no app_user row exists for the auth user
     */
    public CurrentUserResponse getCurrentUser(UUID authUserId) {
        AppUser appUser = getAppUserByAuthUserId(authUserId);
        return CurrentUserResponse.fromEntity(appUser);
    }

    /**
     * Resolves the internal AppUser entity for the authenticated Supabase user.
     */
    public AppUser getAppUserByAuthUserId(UUID authUserId) {
        if (authUserId == null) {
            throw new IllegalArgumentException("authUserId cannot be null");
        }
        return appUserRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for auth user: " + authUserId));
    }

    /**
     * Creates an application profile for the authenticated Supabase user.
     *
     * @param authUserId the Supabase auth.users.id UUID from verified JWT
     * @param request profile details
     * @return CurrentUserResponse DTO
     * @throws ConflictException if app_user already exists for this auth user
     */
    @Transactional
    public CurrentUserResponse createProfile(UUID authUserId, CreateProfileRequest request) {
        if (authUserId == null) {
            throw new IllegalArgumentException("authUserId cannot be null");
        }

        if (appUserRepository.existsByAuthUserId(authUserId)) {
            throw new ConflictException("PROFILE_ALREADY_EXISTS", "A profile already exists for this account.");
        }

        AppUser appUser = new AppUser();
        appUser.setAuthUserId(authUserId);
        appUser.setDisplayName(request.displayName().trim());
        appUser.setBio(request.bio() != null ? request.bio().trim() : null);
        appUser.setCollege(request.college() != null ? request.college().trim() : null);
        appUser.setCourse(request.course() != null ? request.course().trim() : null);
        appUser.setYearOfStudy(request.yearOfStudy());
        appUser.setExperienceLevel(request.experienceLevel());

        AppUser saved = appUserRepository.save(appUser);
        return CurrentUserResponse.fromEntity(saved);
    }

    /**
     * Updates profile fields for the authenticated Supabase user.
     *
     * @param authUserId the Supabase auth.users.id UUID from verified JWT
     * @param request fields to update
     * @return CurrentUserResponse DTO
     */
    @Transactional
    public CurrentUserResponse updateProfile(UUID authUserId, UpdateProfileRequest request) {
        AppUser appUser = getAppUserByAuthUserId(authUserId);

        if (request.displayName() != null && !request.displayName().isBlank()) {
            appUser.setDisplayName(request.displayName().trim());
        }
        if (request.bio() != null) {
            appUser.setBio(request.bio().trim());
        }
        if (request.college() != null) {
            appUser.setCollege(request.college().trim());
        }
        if (request.course() != null) {
            appUser.setCourse(request.course().trim());
        }
        if (request.yearOfStudy() != null) {
            appUser.setYearOfStudy(request.yearOfStudy());
        }
        if (request.experienceLevel() != null) {
            appUser.setExperienceLevel(request.experienceLevel());
        }

        AppUser saved = appUserRepository.save(appUser);
        return CurrentUserResponse.fromEntity(saved);
    }
}
