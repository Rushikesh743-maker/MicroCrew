package com.microcrew.user.dto;

import com.microcrew.user.entity.SkillLevel;
import com.microcrew.user.entity.UserSkill;

import java.time.OffsetDateTime;

public record SkillResponse(
        Long id,
        Long userId,
        String skillName,
        SkillLevel skillLevel,
        OffsetDateTime createdAt
) {
    public static SkillResponse fromEntity(UserSkill skill) {
        return new SkillResponse(
                skill.getId(),
                skill.getUserId(),
                skill.getSkillName(),
                skill.getSkillLevel(),
                skill.getCreatedAt()
        );
    }
}
