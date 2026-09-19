package com.microcrew.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microcrew.auth.AuthenticatedUser;
import com.microcrew.auth.CurrentUser;
import com.microcrew.common.ConflictException;
import com.microcrew.common.GlobalExceptionHandler;
import com.microcrew.common.ResourceNotFoundException;
import com.microcrew.user.dto.CreateProfileRequest;
import com.microcrew.user.dto.CurrentUserResponse;
import com.microcrew.user.dto.UpdateProfileRequest;
import com.microcrew.user.entity.ExperienceLevel;
import com.microcrew.user.service.UserService;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

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

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(mockCurrentUserResolver)
                .build();
    }

    @Test
    void getCurrentUser_returnsProfile() throws Exception {
        CurrentUserResponse response = new CurrentUserResponse(
                1L, authUserId, "Alice", "Bio", "College", "CS", 2,
                ExperienceLevel.INTERMEDIATE, OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(userService.getCurrentUser(authUserId)).thenReturn(response);

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Alice"))
                .andExpect(jsonPath("$.college").value("College"));
    }

    @Test
    void getCurrentUser_notFound_returns404() throws Exception {
        when(userService.getCurrentUser(authUserId)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createProfile_valid_returns201() throws Exception {
        CreateProfileRequest request = new CreateProfileRequest(
                "Bob", "Bio text", "MIT", "CS", 1, ExperienceLevel.BEGINNER
        );

        CurrentUserResponse response = new CurrentUserResponse(
                2L, authUserId, "Bob", "Bio text", "MIT", "CS", 1,
                ExperienceLevel.BEGINNER, OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(userService.createProfile(eq(authUserId), any(CreateProfileRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users/me/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.displayName").value("Bob"));
    }

    @Test
    void createProfile_invalidInput_returns400() throws Exception {
        // displayName blank
        CreateProfileRequest request = new CreateProfileRequest(
                "", "Bio text", "MIT", "CS", 1, ExperienceLevel.BEGINNER
        );

        mockMvc.perform(post("/api/users/me/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createProfile_duplicate_returns409() throws Exception {
        CreateProfileRequest request = new CreateProfileRequest(
                "Bob", "Bio text", "MIT", "CS", 1, ExperienceLevel.BEGINNER
        );

        when(userService.createProfile(eq(authUserId), any(CreateProfileRequest.class)))
                .thenThrow(new ConflictException("PROFILE_ALREADY_EXISTS", "Profile already exists"));

        mockMvc.perform(post("/api/users/me/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("PROFILE_ALREADY_EXISTS"));
    }

    @Test
    void updateProfile_valid_returns200() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest(
                "Updated Name", null, null, null, 3, ExperienceLevel.ADVANCED
        );

        CurrentUserResponse response = new CurrentUserResponse(
                1L, authUserId, "Updated Name", null, null, null, 3,
                ExperienceLevel.ADVANCED, OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(userService.updateProfile(eq(authUserId), any(UpdateProfileRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Updated Name"));
    }
}
