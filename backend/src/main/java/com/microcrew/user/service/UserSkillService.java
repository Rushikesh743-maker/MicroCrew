package com.microcrew.user.service;

import com.microcrew.common.ConflictException;
import com.microcrew.common.ResourceNotFoundException;
import com.microcrew.user.dto.CreateSkillRequest;
import com.microcrew.user.dto.SkillResponse;
import com.microcrew.user.dto.UpdateSkillRequest;
import com.microcrew.user.entity.AppUser;
import com.microcrew.user.entity.UserSkill;
import com.microcrew.user.repository.UserSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserSkillService {

    private final UserService userService;
    private final UserSkillRepository userSkillRepository;

    public UserSkillService(UserService userService, UserSkillRepository userSkillRepository) {
        this.userService = userService;
        this.userSkillRepository = userSkillRepository;
    }

    /**
     * Lists all skills belonging to the authenticated user.
     */
    public List<SkillResponse> getUserSkills(UUID authUserId) {
        AppUser appUser = userService.getAppUserByAuthUserId(authUserId);
        return userSkillRepository.findByUserId(appUser.getId())
                .stream()
                .map(SkillResponse::fromEntity)
                .toList();
    }

    /**
     * Adds a new skill to the authenticated user's profile.
     * Prevents duplicates by trimming whitespace and enforcing UNIQUE(user_id, skill_name).
     */
    @Transactional
    public SkillResponse addSkill(UUID authUserId, CreateSkillRequest request) {
        AppUser appUser = userService.getAppUserByAuthUserId(authUserId);
        String trimmedName = request.skillName().trim();

        userSkillRepository.findByUserIdAndSkillName(appUser.getId(), trimmedName).ifPresent(s -> {
            throw new ConflictException("SKILL_ALREADY_EXISTS", "This skill is already present on your profile.");
        });

        UserSkill skill = new UserSkill(appUser.getId(), trimmedName, request.skillLevel());
        UserSkill saved = userSkillRepository.save(skill);
        return SkillResponse.fromEntity(saved);
    }

    /**
     * Updates an existing skill on the authenticated user's profile.
     * Enforces ownership and ensures privacy by not leaking other users' skill existence.
     */
    @Transactional
    public SkillResponse updateSkill(UUID authUserId, Long skillId, UpdateSkillRequest request) {
        AppUser appUser = userService.getAppUserByAuthUserId(authUserId);

        UserSkill skill = userSkillRepository.findById(skillId)
                .filter(s -> s.getUserId().equals(appUser.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));

        if (request.skillName() != null && !request.skillName().isBlank()) {
            String trimmedName = request.skillName().trim();
            if (!trimmedName.equals(skill.getSkillName())) {
                userSkillRepository.findByUserIdAndSkillName(appUser.getId(), trimmedName)
                        .filter(existing -> !existing.getId().equals(skillId))
                        .ifPresent(existing -> {
                            throw new ConflictException("SKILL_ALREADY_EXISTS", "This skill is already present on your profile.");
                        });
                skill.setSkillName(trimmedName);
            }
        }

        if (request.skillLevel() != null) {
            skill.setSkillLevel(request.skillLevel());
        }

        UserSkill saved = userSkillRepository.save(skill);
        return SkillResponse.fromEntity(saved);
    }

    /**
     * Deletes a skill from the authenticated user's profile.
     * Enforces ownership strictly.
     */
    @Transactional
    public void deleteSkill(UUID authUserId, Long skillId) {
        AppUser appUser = userService.getAppUserByAuthUserId(authUserId);

        UserSkill skill = userSkillRepository.findById(skillId)
                .filter(s -> s.getUserId().equals(appUser.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));

        userSkillRepository.delete(skill);
    }
}
