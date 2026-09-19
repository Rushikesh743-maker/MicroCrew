package com.microcrew.user.dto;

import com.microcrew.user.entity.AppUser;
import com.microcrew.user.entity.ExperienceLevel;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response DTO for the current authenticated user's profile.
 */
public record CurrentUserResponse(
        Long id,
        UUID authUserId,
        String displayName,
        String bio,
        String college,
        String course,
        Integer yearOfStudy,
        ExperienceLevel experienceLevel,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static CurrentUserResponse fromEntity(AppUser user) {
        return new CurrentUserResponse(
                user.getId(),
                user.getAuthUserId(),
                user.getDisplayName(),
                user.getBio(),
                user.getCollege(),
                user.getCourse(),
                user.getYearOfStudy(),
                user.getExperienceLevel(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
