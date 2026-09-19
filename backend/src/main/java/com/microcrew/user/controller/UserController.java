package com.microcrew.user.controller;

import com.microcrew.auth.AuthenticatedUser;
import com.microcrew.auth.CurrentUser;
import com.microcrew.user.dto.CreateProfileRequest;
import com.microcrew.user.dto.CurrentUserResponse;
import com.microcrew.user.dto.UpdateProfileRequest;
import com.microcrew.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves the profile of the currently authenticated Supabase user.
     * Identity is derived entirely from the verified JWT.
     *
     * @param currentUser injected authenticated Supabase user
     * @return 200 OK with CurrentUserResponse, or 404 NOT FOUND if user has no profile
     */
    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> getCurrentUser(@CurrentUser AuthenticatedUser currentUser) {
        CurrentUserResponse response = userService.getCurrentUser(currentUser.authUserId());
        return ResponseEntity.ok(response);
    }

    /**
     * Creates an application profile for the currently authenticated Supabase user.
     *
     * @param currentUser injected authenticated Supabase user
     * @param request profile creation details
     * @return 201 CREATED with CurrentUserResponse, or 409 CONFLICT if profile already exists
     */
    @PostMapping("/me/profile")
    public ResponseEntity<CurrentUserResponse> createProfile(
            @CurrentUser AuthenticatedUser currentUser,
            @Valid @RequestBody CreateProfileRequest request) {
        CurrentUserResponse response = userService.createProfile(currentUser.authUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates the profile of the currently authenticated Supabase user.
     *
     * @param currentUser injected authenticated Supabase user
     * @param request profile update details
     * @return 200 OK with updated CurrentUserResponse
     */
    @PatchMapping("/me")
    public ResponseEntity<CurrentUserResponse> updateProfile(
            @CurrentUser AuthenticatedUser currentUser,
            @Valid @RequestBody UpdateProfileRequest request) {
        CurrentUserResponse response = userService.updateProfile(currentUser.authUserId(), request);
        return ResponseEntity.ok(response);
    }
}
