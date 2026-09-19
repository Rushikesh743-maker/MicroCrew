package com.microcrew.user.dto;

import com.microcrew.user.entity.SkillLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateSkillRequest(
        @NotBlank(message = "Skill name is required")
        @Size(min = 1, max = 100, message = "Skill name must be between 1 and 100 characters")
        String skillName,

        @NotNull(message = "Skill level is required")
        SkillLevel skillLevel
) {
}
