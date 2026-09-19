package com.microcrew.user.service;

import com.microcrew.common.ConflictException;
import com.microcrew.common.ResourceNotFoundException;
import com.microcrew.user.dto.CreateProfileRequest;
import com.microcrew.user.dto.CurrentUserResponse;
import com.microcrew.user.dto.UpdateProfileRequest;
import com.microcrew.user.entity.AppUser;
import com.microcrew.user.entity.ExperienceLevel;
import com.microcrew.user.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private UserService userService;

    private UUID authUserId;
    private AppUser existingUser;

    @BeforeEach
    void setUp() {
        authUserId = UUID.randomUUID();
        existingUser = new AppUser();
        existingUser.setId(1L);
        existingUser.setAuthUserId(authUserId);
        existingUser.setDisplayName("John Doe");
        existingUser.setExperienceLevel(ExperienceLevel.BEGINNER);
    }

    @Test
    void getCurrentUser_success() {
        when(appUserRepository.findByAuthUserId(authUserId)).thenReturn(Optional.of(existingUser));

        CurrentUserResponse response = userService.getCurrentUser(authUserId);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("John Doe", response.displayName());
        verify(appUserRepository).findByAuthUserId(authUserId);
    }

    @Test
    void getCurrentUser_notFound_throwsResourceNotFound() {
        when(appUserRepository.findByAuthUserId(authUserId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getCurrentUser(authUserId));
    }

    @Test
    void createProfile_success() {
        CreateProfileRequest request = new CreateProfileRequest(
                "Jane Doe",
                "Computer science student",
                "Tech University",
                "Software Engineering",
                2,
                ExperienceLevel.INTERMEDIATE
        );

        when(appUserRepository.existsByAuthUserId(authUserId)).thenReturn(false);
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser u = invocation.getArgument(0);
            u.setId(2L);
            return u;
        });

        CurrentUserResponse response = userService.createProfile(authUserId, request);

        assertNotNull(response);
        assertEquals("Jane Doe", response.displayName());
        assertEquals("Tech University", response.college());
        assertEquals(ExperienceLevel.INTERMEDIATE, response.experienceLevel());
        verify(appUserRepository).save(any(AppUser.class));
    }

    @Test
    void createProfile_duplicate_throwsConflict() {
        CreateProfileRequest request = new CreateProfileRequest(
                "Jane Doe", null, null, null, null, null
        );

        when(appUserRepository.existsByAuthUserId(authUserId)).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.createProfile(authUserId, request));
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void updateProfile_success() {
        UpdateProfileRequest request = new UpdateProfileRequest(
                "John Updated",
                "Updated bio",
                null,
                null,
                4,
                ExperienceLevel.ADVANCED
        );

        when(appUserRepository.findByAuthUserId(authUserId)).thenReturn(Optional.of(existingUser));
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CurrentUserResponse response = userService.updateProfile(authUserId, request);

        assertNotNull(response);
        assertEquals("John Updated", response.displayName());
        assertEquals("Updated bio", response.bio());
        assertEquals(4, response.yearOfStudy());
        assertEquals(ExperienceLevel.ADVANCED, response.experienceLevel());
    }
}
