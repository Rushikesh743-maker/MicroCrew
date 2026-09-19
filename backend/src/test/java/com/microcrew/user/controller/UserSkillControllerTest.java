package com.microcrew.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microcrew.auth.AuthenticatedUser;
import com.microcrew.auth.CurrentUser;
import com.microcrew.common.ConflictException;
import com.microcrew.common.GlobalExceptionHandler;
import com.microcrew.common.ResourceNotFoundException;
import com.microcrew.user.dto.CreateSkillRequest;
import com.microcrew.user.dto.SkillResponse;
import com.microcrew.user.dto.UpdateSkillRequest;
import com.microcrew.user.entity.SkillLevel;
import com.microcrew.user.service.UserSkillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserSkillControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserSkillService userSkillService;

    @InjectMocks
    private UserSkillController userSkillController;

    private ObjectMapper objectMapper;
    private UUID authUserId;

    @BeforeEach
    void setUp() {
        authUserId = UUID.randomUUID();
        objectMapper = new ObjectMapper();

        HandlerMethodArgumentResolver mockCurrentUserResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(CurrentUser.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter,
                                          ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest,
                                          WebDataBinderFactory binderFactory) {
                return new AuthenticatedUser(authUserId, "test@example.com");
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(userSkillController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(mockCurrentUserResolver)
                .build();
    }

    @Test
    void getSkills_returnsList() throws Exception {
        SkillResponse skill = new SkillResponse(1L, 10L, "Java", SkillLevel.ADVANCED, OffsetDateTime.now());
        when(userSkillService.getUserSkills(authUserId)).thenReturn(List.of(skill));

        mockMvc.perform(get("/api/users/me/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].skillName").value("Java"))
                .andExpect(jsonPath("$[0].skillLevel").value("ADVANCED"));
    }

    @Test
    void addSkill_valid_returns201() throws Exception {
        CreateSkillRequest request = new CreateSkillRequest("PostgreSQL", SkillLevel.INTERMEDIATE);
        SkillResponse response = new SkillResponse(2L, 10L, "PostgreSQL", SkillLevel.INTERMEDIATE, OffsetDateTime.now());

        when(userSkillService.addSkill(eq(authUserId), any(CreateSkillRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users/me/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.skillName").value("PostgreSQL"));
    }

    @Test
    void addSkill_invalidInput_returns400() throws Exception {
        // Missing skillLevel and blank skillName
        CreateSkillRequest request = new CreateSkillRequest("", null);

        mockMvc.perform(post("/api/users/me/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void addSkill_duplicate_returns409() throws Exception {
        CreateSkillRequest request = new CreateSkillRequest("PostgreSQL", SkillLevel.INTERMEDIATE);

        when(userSkillService.addSkill(eq(authUserId), any(CreateSkillRequest.class)))
                .thenThrow(new ConflictException("SKILL_ALREADY_EXISTS", "This skill is already present on your profile."));

        mockMvc.perform(post("/api/users/me/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("SKILL_ALREADY_EXISTS"));
    }

    @Test
    void updateSkill_valid_returns200() throws Exception {
        UpdateSkillRequest request = new UpdateSkillRequest("Python 3", SkillLevel.EXPERT);
        SkillResponse response = new SkillResponse(3L, 10L, "Python 3", SkillLevel.EXPERT, OffsetDateTime.now());

        when(userSkillService.updateSkill(eq(authUserId), eq(3L), any(UpdateSkillRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/users/me/skills/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skillName").value("Python 3"))
                .andExpect(jsonPath("$.skillLevel").value("EXPERT"));
    }

    @Test
    void deleteSkill_returns204() throws Exception {
        doNothing().when(userSkillService).deleteSkill(authUserId, 4L);

        mockMvc.perform(delete("/api/users/me/skills/4"))
                .andExpect(status().isNoContent());

        verify(userSkillService).deleteSkill(authUserId, 4L);
    }

    @Test
    void deleteSkill_notFoundOrNotOwner_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Skill not found")).when(userSkillService).deleteSkill(authUserId, 99L);

        mockMvc.perform(delete("/api/users/me/skills/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
