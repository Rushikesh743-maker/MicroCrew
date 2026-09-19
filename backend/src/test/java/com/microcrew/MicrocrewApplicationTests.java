package com.microcrew;

import com.microcrew.auth.AuthenticatedUser;
import com.microcrew.user.dto.CurrentUserResponse;
import com.microcrew.user.entity.AppUser;
import com.microcrew.user.entity.ExperienceLevel;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MicrocrewApplicationTests {

    @Test
    void authenticatedUser_holdsCorrectIdentity() {
        UUID authUserId = UUID.randomUUID();
        AuthenticatedUser user = new AuthenticatedUser(authUserId, "test@example.com");

        assertEquals(authUserId, user.authUserId());
        assertEquals("test@example.com", user.email());
    }

    @Test
    void currentUserResponse_mapsCorrectlyFromEntity() {
        UUID authUserId = UUID.randomUUID();
        AppUser user = new AppUser(authUserId, "Test User");
        user.setId(1L);
        user.setBio("Test bio");
        user.setCollege("Test College");
        user.setCourse("Computer Science");
        user.setYearOfStudy(3);
        user.setExperienceLevel(ExperienceLevel.INTERMEDIATE);

        CurrentUserResponse response = CurrentUserResponse.fromEntity(user);

        assertEquals(1L, response.id());
        assertEquals(authUserId, response.authUserId());
        assertEquals("Test User", response.displayName());
        assertEquals("Test bio", response.bio());
        assertEquals("Test College", response.college());
        assertEquals("Computer Science", response.course());
        assertEquals(3, response.yearOfStudy());
        assertEquals(ExperienceLevel.INTERMEDIATE, response.experienceLevel());
    }
}
