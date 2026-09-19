package com.microcrew.auth;

import java.util.Objects;
import java.util.UUID;

/**
 * Immutable record representing the authenticated Supabase identity.
 * Contains the Supabase auth user UUID (auth.users.id).
 * NOTE: authUserId is distinct from app_user.id (the application database ID).
 */
public record AuthenticatedUser(
        UUID authUserId,
        String email
) {
    public AuthenticatedUser {
        Objects.requireNonNull(authUserId, "authUserId must not be null");
    }

    public AuthenticatedUser(UUID authUserId) {
        this(authUserId, null);
    }
}
