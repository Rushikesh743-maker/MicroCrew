package com.microcrew.user.dto;

import com.microcrew.user.entity.ExperienceLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(min = 2, max = 100, message = "Display name must be between 2 and 100 characters")
        String displayName,

        @Size(max = 1000, message = "Bio must not exceed 1000 characters")
        String bio,

        @Size(max = 200, message = "College must not exceed 200 characters")
        String college,

        @Size(max = 200, message = "Course must not exceed 200 characters")
        String course,

        @Min(value = 1, message = "Year of study must be between 1 and 10")
        @Max(value = 10, message = "Year of study must be between 1 and 10")
        Integer yearOfStudy,

        ExperienceLevel experienceLevel
) {
}
