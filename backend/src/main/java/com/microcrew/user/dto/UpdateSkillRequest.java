package com.microcrew.user.dto;

import com.microcrew.user.entity.SkillLevel;
import jakarta.validation.constraints.Size;

public record UpdateSkillRequest(
        @Size(min = 1, max = 100, message = "Skill name must be between 1 and 100 characters")
        String skillName,

        SkillLevel skillLevel
) {
}
