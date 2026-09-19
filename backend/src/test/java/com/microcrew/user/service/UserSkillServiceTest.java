package com.microcrew.user.service;

import com.microcrew.common.ConflictException;
import com.microcrew.common.ResourceNotFoundException;
import com.microcrew.user.dto.CreateSkillRequest;
import com.microcrew.user.dto.SkillResponse;
import com.microcrew.user.dto.UpdateSkillRequest;
import com.microcrew.user.entity.AppUser;
import com.microcrew.user.entity.SkillLevel;
import com.microcrew.user.entity.UserSkill;
import com.microcrew.user.repository.UserSkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSkillServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserSkillRepository userSkillRepository;

    @InjectMocks
    private UserSkillService userSkillService;

    private UUID authUserId;
    private AppUser appUser;

    @BeforeEach
    void setUp() {
        authUserId = UUID.randomUUID();
        appUser = new AppUser();
        appUser.setId(10L);
        appUser.setAuthUserId(authUserId);
        appUser.setDisplayName("Coder Student");
    }

    @Test
    void getUserSkills_success() {
        UserSkill skill1 = new UserSkill(10L, "Java", SkillLevel.INTERMEDIATE);
        skill1.setId(1L);
        UserSkill skill2 = new UserSkill(10L, "React", SkillLevel.ADVANCED);
        skill2.setId(2L);

        when(userService.getAppUserByAuthUserId(authUserId)).thenReturn(appUser);
        when(userSkillRepository.findByUserId(10L)).thenReturn(List.of(skill1, skill2));

        List<SkillResponse> skills = userSkillService.getUserSkills(authUserId);

        assertEquals(2, skills.size());
        assertEquals("Java", skills.get(0).skillName());
        assertEquals("React", skills.get(1).skillName());
    }

    @Test
    void addSkill_success() {
        CreateSkillRequest request = new CreateSkillRequest("TypeScript", SkillLevel.INTERMEDIATE);

        when(userService.getAppUserByAuthUserId(authUserId)).thenReturn(appUser);
        when(userSkillRepository.findByUserIdAndSkillName(10L, "TypeScript")).thenReturn(Optional.empty());
        when(userSkillRepository.save(any(UserSkill.class))).thenAnswer(invocation -> {
            UserSkill s = invocation.getArgument(0);
            s.setId(3L);
            return s;
        });

        SkillResponse response = userSkillService.addSkill(authUserId, request);

        assertNotNull(response);
        assertEquals("TypeScript", response.skillName());
        assertEquals(SkillLevel.INTERMEDIATE, response.skillLevel());
        verify(userSkillRepository).save(any(UserSkill.class));
    }

    @Test
    void addSkill_duplicate_throwsConflict() {
        CreateSkillRequest request = new CreateSkillRequest("TypeScript", SkillLevel.BEGINNER);
        UserSkill existing = new UserSkill(10L, "TypeScript", SkillLevel.INTERMEDIATE);

        when(userService.getAppUserByAuthUserId(authUserId)).thenReturn(appUser);
        when(userSkillRepository.findByUserIdAndSkillName(10L, "TypeScript")).thenReturn(Optional.of(existing));

        assertThrows(ConflictException.class, () -> userSkillService.addSkill(authUserId, request));
        verify(userSkillRepository, never()).save(any(UserSkill.class));
    }

    @Test
    void updateSkill_success() {
        UserSkill skill = new UserSkill(10L, "React", SkillLevel.BEGINNER);
        skill.setId(5L);

        UpdateSkillRequest request = new UpdateSkillRequest("React", SkillLevel.ADVANCED);

        when(userService.getAppUserByAuthUserId(authUserId)).thenReturn(appUser);
        when(userSkillRepository.findById(5L)).thenReturn(Optional.of(skill));
        when(userSkillRepository.save(any(UserSkill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SkillResponse response = userSkillService.updateSkill(authUserId, 5L, request);

        assertEquals(SkillLevel.ADVANCED, response.skillLevel());
    }

    @Test
    void updateSkill_anotherUserSkill_throwsNotFoundWithoutLeaking() {
        // Skill belongs to user 99L, while authenticated user is 10L
        UserSkill otherUserSkill = new UserSkill(99L, "Go", SkillLevel.INTERMEDIATE);
        otherUserSkill.setId(7L);

        UpdateSkillRequest request = new UpdateSkillRequest("Go", SkillLevel.EXPERT);

        when(userService.getAppUserByAuthUserId(authUserId)).thenReturn(appUser);
        when(userSkillRepository.findById(7L)).thenReturn(Optional.of(otherUserSkill));

        assertThrows(ResourceNotFoundException.class, () -> userSkillService.updateSkill(authUserId, 7L, request));
        verify(userSkillRepository, never()).save(any(UserSkill.class));
    }

    @Test
    void deleteSkill_success() {
        UserSkill skill = new UserSkill(10L, "Python", SkillLevel.BEGINNER);
        skill.setId(8L);

        when(userService.getAppUserByAuthUserId(authUserId)).thenReturn(appUser);
        when(userSkillRepository.findById(8L)).thenReturn(Optional.of(skill));

        userSkillService.deleteSkill(authUserId, 8L);

        verify(userSkillRepository).delete(skill);
    }

    @Test
    void deleteSkill_anotherUserSkill_throwsNotFound() {
        UserSkill otherUserSkill = new UserSkill(99L, "Rust", SkillLevel.EXPERT);
        otherUserSkill.setId(9L);

        when(userService.getAppUserByAuthUserId(authUserId)).thenReturn(appUser);
        when(userSkillRepository.findById(9L)).thenReturn(Optional.of(otherUserSkill));

        assertThrows(ResourceNotFoundException.class, () -> userSkillService.deleteSkill(authUserId, 9L));
        verify(userSkillRepository, never()).delete(any(UserSkill.class));
    }
}
