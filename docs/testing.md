# MicroCrew Testing Strategy

## Overview
Foundation includes tests for authentication, profile, skills, and database constraints.

## Backend Testing

### Stack
- JUnit 5
- Mockito
- Spring Boot Test
- Spring Security Test
- H2 for unit tests (or Testcontainers PostgreSQL for integration)
- AssertJ

### Test Categories

#### 1. Authentication Tests

**Goal**: Verify unauthenticated requests rejected, authenticated accepted.

- `SecurityConfigTest` or `AuthIntegrationTest`
  - `unauthenticatedRequest_shouldReturn401`
  - `requestWithInvalidToken_shouldReturn401`
  - `requestWithValidToken_shouldReachController` (mock JWT)

Example:
```java
@WebMvcTest
@Import(SecurityConfig.class)
class SecurityTest {
    @Test
    void unauthenticated_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/users/me"))
            .andExpect(status().isUnauthorized());
    }
}
```

#### 2. Profile Tests

- `UserServiceTest` (unit with Mockito)
  - `getCurrentUser_existingUser_returnsProfile`
  - `getCurrentUser_nonExisting_throwsNotFound`
  - `updateCurrentUser_validRequest_updatesAllowedFields`
  - `updateCurrentUser_cannotModifyIdOrAuthUserId`

- `UserControllerIntegrationTest`
  - `getMe_authenticated_returnsOwnProfile`
  - `patchMe_authenticated_updatesOwnProfile`
  - `patchMe_cannotUpdateAnotherUser` - ensure authUserId from token, not body

#### 3. Skills Tests

- `UserSkillServiceTest`
  - `addSkill_valid_returnsCreated`
  - `addSkill_duplicate_throwsConflict`
  - `deleteSkill_ownSkill_deletes`
  - `deleteSkill_otherUsersSkill_throwsNotFound`

- `UserSkillControllerTest`
  - `getSkills_returnsOwnSkillsOnly`
  - `deleteSkill_otherUserSkill_returns404`

#### 4. Database Constraint Tests

- `AppUserRepositoryTest` (with Testcontainers or H2)
  - `authUserId_uniqueConstraint_enforced`
  - `displayName_notNull_enforced`

- `UserSkillRepositoryTest`
  - `uniqueUserSkillConstraint_enforced`
  - `cascadeDelete_whenUserDeleted_skillsDeleted`

### Running Backend Tests

```bash
cd backend
./mvnw test
# or
mvn test
```

With Testcontainers, Docker must be running.

### Test Configuration

`src/test/resources/application-test.yml`:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: disabled-for-tests
```

For JWT tests, mock JwtDecoder.

## Frontend Testing (Future)

### Stack
- Vitest or Jest
- React Testing Library
- Playwright for E2E

### Planned Tests

- AuthProvider:
  - restores session on reload
  - login/logout flow
  - protected route redirects when not authenticated

- Profile page:
  - fetches and displays profile
  - updates profile

- Skills:
  - adds skill
  - deletes skill

### E2E with Playwright

```bash
cd frontend
npx playwright test
```

Tests:
- signup → dashboard → profile → add skill → delete skill → logout

## CI

GitHub Actions runs:
- Backend build + tests
- Frontend install + build
- (Future) E2E tests

## Coverage Goals

- Foundation: 70%+ for user and auth modules
- Future: 80%+ for critical business logic (contributions ledger, matching)

## Manual Testing Checklist (Foundation)

- [ ] Signup works via Supabase
- [ ] Login works
- [ ] Session persists after refresh
- [ ] Logout works
- [ ] GET /api/users/me returns profile
- [ ] PATCH /api/users/me updates own profile
- [ ] User A cannot update User B (verify via token manipulation attempt)
- [ ] POST skill works
- [ ] Duplicate skill returns 409
- [ ] GET skills returns own only
- [ ] DELETE own skill works
- [ ] DELETE other user's skill returns 404
- [ ] Unauthenticated API returns 401
- [ ] Frontend protected routes redirect to login when no session
- [ ] No service_role key in frontend bundle (check build output)

## Known Limitations

- H2 used for some tests doesn't fully mimic PostgreSQL ENUM and RLS - Testcontainers PostgreSQL is better for integration tests
- Supabase Auth not fully mocked in integration tests - JWT decoder mocked
- No Playwright tests yet in foundation (prepared but not implemented)
