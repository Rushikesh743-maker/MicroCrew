package com.microcrew.user.controller;

import com.microcrew.auth.AuthenticatedUser;
import com.microcrew.auth.CurrentUser;
import com.microcrew.user.dto.CreateSkillRequest;
import com.microcrew.user.dto.SkillResponse;
import com.microcrew.user.dto.UpdateSkillRequest;
import com.microcrew.user.service.UserSkillService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/me/skills")
public class UserSkillController {

    private final UserSkillService userSkillService;

    public UserSkillController(UserSkillService userSkillService) {
        this.userSkillService = userSkillService;
    }

    /**
     * Lists all skills of the authenticated user.
     */
    @GetMapping
    public ResponseEntity<List<SkillResponse>> getSkills(@CurrentUser AuthenticatedUser currentUser) {
        List<SkillResponse> skills = userSkillService.getUserSkills(currentUser.authUserId());
        return ResponseEntity.ok(skills);
    }

    /**
     * Adds a new skill to the authenticated user's profile.
     */
    @PostMapping
    public ResponseEntity<SkillResponse> addSkill(
            @CurrentUser AuthenticatedUser currentUser,
            @Valid @RequestBody CreateSkillRequest request) {
        SkillResponse response = userSkillService.addSkill(currentUser.authUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates an existing skill on the authenticated user's profile.
     */
    @PatchMapping("/{skillId}")
    public ResponseEntity<SkillResponse> updateSkill(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable Long skillId,
            @Valid @RequestBody UpdateSkillRequest request) {
        SkillResponse response = userSkillService.updateSkill(currentUser.authUserId(), skillId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a skill from the authenticated user's profile.
     */
    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> deleteSkill(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable Long skillId) {
        userSkillService.deleteSkill(currentUser.authUserId(), skillId);
        return ResponseEntity.noContent().build();
    }
}
