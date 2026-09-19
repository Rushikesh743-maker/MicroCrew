# MicroCrew Authorization Design

## Principles

1. **Authentication owned by Supabase Auth** - no custom password storage
2. **Authorization enforced at multiple layers** - backend service + RLS
3. **No client-controlled ownership** - backend derives identity from JWT
4. **Object-level authorization** - not just role-based, but ownership checks
5. **Defense-in-depth** - RLS as safety net, not primary enforcement

## Authentication

### Flow
1. User signs up/logs in via Supabase Auth (frontend)
2. Supabase returns JWT access_token (contains `sub` = auth.users.id)
3. Frontend stores session via supabase-js (secure, handles refresh)
4. Frontend sends `Authorization: Bearer <token>` to backend
5. Backend verifies JWT signature via JWKS URL
6. Backend extracts `sub` as authenticated identity

### JWT Verification (Backend)

- Uses Spring Security OAuth2 Resource Server
- Configured with `SUPABASE_JWKS_URL` = `https://<project>.supabase.co/auth/v1/.well-known/jwks.json` or `/auth/v1/keys`
- Validates:
  - Signature (RS256)
  - Expiration
  - Issuer (optional check)
- Does NOT trust `user_id` from body - only from token

### Authenticated Principal

Custom `AuthenticatedUser` record:
```java
public record AuthenticatedUser(UUID authUserId, String email, String role) {}
```

- `authUserId` = JWT `sub` claim (UUID)
- Available via `@AuthenticationPrincipal` or `SecurityContextHolder`
- Helper annotation `@CurrentUser` for controllers

## Authorization

### Role-Based (Coarse)

- `USER` - default authenticated user
- `ADMIN` - future, stored in `app_user` or via Supabase custom claims

Configured via `@EnableMethodSecurity` and `SecurityFilterChain`:

- `/api/public/**` - public
- `/api/admin/**` - requires ADMIN
- All other `/api/**` - requires authentication

### Object-Level (Fine-Grained)

This is critical. Example: User A must not modify User B's profile or skills.

Enforced in **service layer**, not just controller:

```java
// UserService
public AppUser getCurrentUser(UUID authUserId) {
    return repository.findByAuthUserId(authUserId)
        .orElseThrow(() -> new NotFoundException("Profile not found"));
}

public AppUser updateCurrentUser(UUID authUserId, UpdateProfileRequest req) {
    AppUser user = getCurrentUser(authUserId); // ownership guaranteed
    // update only allowed fields, never id/auth_user_id
    user.setDisplayName(req.displayName());
    ...
    return repository.save(user);
}
```

```java
// UserSkillService
public void deleteSkill(UUID authUserId, Long skillId) {
    AppUser user = userRepository.findByAuthUserId(authUserId).orElseThrow();
    UserSkill skill = skillRepository.findByIdAndUserId(skillId, user.getId())
        .orElseThrow(() -> new NotFoundException("Skill not found or not owned"));
    // If skill exists but belongs to another user, findByIdAndUserId returns empty -> 404, not 403
    // This prevents ID enumeration
    skillRepository.delete(skill);
}
```

Key patterns:
- Always query with both `id` AND `user_id` derived from auth
- Never accept `userId` from request body for ownership
- Use `findByAuthUserId` to resolve `app_user.id` from JWT
- Return 404 instead of 403 for not-owned resources to avoid leaking existence

### Why Service Layer, Not Just Controller?

- Controllers are thin - they extract auth and delegate
- Services contain business logic and authorization
- Reusable across controllers, schedulers, events
- Testable

Frontend authorization is for UX only, never for security.

## RLS (Row Level Security)

### Purpose
Defense-in-depth. If backend has bug, RLS still protects. If someone gets anon key and tries direct Supabase queries, RLS blocks.

### Enabled Tables
- `app_user`
- `user_skill`

### Policies

**app_user:**
```sql
-- Read: authenticated can read all (public profiles for collaboration)
CREATE POLICY "Authenticated users can read all profiles"
    ON app_user FOR SELECT TO authenticated USING (true);

-- Write: only own
CREATE POLICY "Users can insert own profile"
    ON app_user FOR INSERT TO authenticated WITH CHECK (auth.uid() = auth_user_id);

CREATE POLICY "Users can update own profile"
    ON app_user FOR UPDATE TO authenticated USING (auth.uid() = auth_user_id);

CREATE POLICY "Users can delete own profile"
    ON app_user FOR DELETE TO authenticated USING (auth.uid() = auth_user_id);
```

**user_skill:**
```sql
-- Read all
CREATE POLICY "Authenticated can read all skills" ON user_skill FOR SELECT TO authenticated USING (true);

-- Write own only via app_user ownership check
CREATE POLICY "Users can insert own skills" ON user_skill FOR INSERT TO authenticated WITH CHECK (
    EXISTS (SELECT 1 FROM app_user WHERE app_user.id = user_skill.user_id AND auth_user_id = auth.uid())
);
-- Similar for UPDATE/DELETE
```

### Service Role Bypass

- Supabase `service_role` key bypasses RLS
- Backend uses service_role for DB connection? Actually backend uses direct Postgres connection (DATABASE_URL) which bypasses RLS at DB level anyway because it's superuser
- But if backend ever uses Supabase client with service_role, it bypasses RLS - that's okay because service layer enforces auth
- Frontend uses `anon` key which respects RLS

### RLS is NOT Replacement

We do NOT rely solely on RLS:
- Backend still checks ownership via queries
- RLS is second layer
- Documented here to explain why both exist

## Security Checks for Foundation

- [x] Unauthenticated request to `/api/users/me` → 401
- [x] User A cannot update User B profile → enforced via authUserId from token, not body
- [x] User A cannot delete User B skill → `findByIdAndUserId` ensures ownership
- [x] No service_role key in frontend - only `VITE_SUPABASE_ANON_KEY`
- [x] No password storage in app_user - Supabase Auth owns it
- [x] No trust of client-sent user ID

## Future Authorization

- `PROJECT_OWNER` permission: user who created project can manage members, tasks
- `PROJECT_MEMBER` permission: member can view and update own tasks
- `ADMIN` role: manage hackathons, moderate
- Will use custom annotations like `@IsProjectOwner` or service checks
- Audit log for sensitive operations

## Threat Model (Foundation)

- **IDOR**: Prevented by ownership checks using JWT-derived identity
- **Broken Auth**: Supabase Auth handles, backend verifies JWT
- **Credential Exposure**: .env.example has no real secrets, frontend only anon key
- **SQL Injection**: JPA parameterized queries, no raw SQL concatenation
- **Mass Assignment**: DTOs only allow specific fields, never `id` or `auth_user_id` writable
