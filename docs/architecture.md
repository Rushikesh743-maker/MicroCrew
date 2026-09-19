# MicroCrew Architecture

## Overview
MicroCrew is a collaboration platform for students. This document describes the foundational architecture.

## High-Level Architecture

```
                    MICROCREW
                        |
          +-------------+-------------+
          |                           |
      React Frontend             Spring Boot API
          |                           |
          |                    Spring Security
          |                           |
          |                    Business Services
          |                           |
          +-----------+---------------+
                      |
                Supabase
             +--------+--------+
             |                 |
        Supabase Auth     PostgreSQL
             |                 |
          auth.users      Application Data
                               |
                         Supabase Storage (planned)
```

## Components

### Frontend
- **Framework**: React 18 + Vite + TypeScript
- **Styling**: Tailwind CSS
- **Routing**: React Router v6
- **State**: React Context for Auth, local state for UI
- **Backend Client**: Supabase JS for Auth, custom fetch wrapper for Spring Boot API
- **Build**: Vite

Responsibilities:
- User signup/login via Supabase Auth
- Session management and token storage (Supabase client handles this securely)
- Calling backend APIs with Bearer token
- UI rendering, form validation

### Backend
- **Framework**: Spring Boot 3.x (Java 21)
- **Security**: Spring Security 6 with JWT resource server
- **Data**: Spring Data JPA + Hibernate (validate mode)
- **Build**: Maven
- **Testing**: JUnit 5, Mockito, Testcontainers

Responsibilities:
- Verify Supabase JWTs
- Provide authenticated principal
- Business logic and object-level authorization
- Database access via JPA
- Consistent error handling

### Supabase
- **Auth**: Owns user authentication, stores in `auth.users`
- **PostgreSQL**: Application data (`app_user`, `user_skill`, future tables)
- **Storage**: Planned for portfolio assets

## Authentication Flow

```
React
  ↓ (signup/login via Supabase client)
Supabase Auth
  ↓ (returns session with access_token + refresh_token)
Supabase Access Token (JWT, stored securely by supabase-js)
  ↓ (Authorization: Bearer <token> header)
Spring Boot - JwtDecoder verifies signature via JWKS
  ↓
Authenticated Principal (auth_user_id = JWT sub claim)
  ↓
Load or create app_user
  ↓
Object-Level Authorization in Service layer
```

### Token Details
- Frontend gets JWT from Supabase Auth after login
- JWT contains `sub` = `auth.users.id` (UUID)
- Backend validates JWT signature using Supabase JWKS URL: `https://<project>.supabase.co/auth/v1/.well-known/jwks.json`
- Backend does NOT trust any user ID from request body - always derives from SecurityContext
- Supabase client automatically refreshes tokens and persists session in localStorage (managed by library, not custom code)

## API Boundary

- Frontend → Supabase Auth: Direct via `@supabase/supabase-js`
- Frontend → Spring Boot: `VITE_API_URL/api/*` with Bearer token
- Spring Boot → Supabase PostgreSQL: JDBC via `DATABASE_URL`
- Spring Boot → Supabase JWKS: HTTP fetch for JWT verification

Public endpoints:
- `GET /api/public/health` - health check
- `POST /api/public/auth/profile` - idempotent profile creation after signup (requires auth but special handling)

Protected endpoints (require valid JWT):
- `GET /api/users/me`
- `PATCH /api/users/me`
- `GET /api/users/me/skills`
- `POST /api/users/me/skills`
- `DELETE /api/users/me/skills/{id}`

Future:
- `/api/hackathons/*`
- `/api/projects/*`
- `/api/tasks/*`
- etc.

## Authorization Flow

1. **Authentication**: JWT verified, `auth_user_id` extracted from `sub` claim
2. **Profile Resolution**: `app_user` loaded by `auth_user_id`
3. **Service-Level Authorization**: Service methods check ownership
   - Example: `userSkillRepository.findByIdAndUserId(skillId, currentUserId)` ensures user can only delete own skill
   - No client-controlled ownership
4. **RLS (Defense-in-Depth)**: PostgreSQL RLS policies enforce same rules at DB layer
   - Even if backend bug allows wrong query, RLS would block if using anon key
   - Backend uses service_role which bypasses RLS, but we still enforce in service layer
   - RLS ensures direct Supabase client access (if ever used) is also safe

## Data Flow for Profile Creation

```
1. User signs up via frontend -> Supabase Auth creates auth.users row
2. Frontend calls POST /api/public/auth/profile or backend lazily creates on first GET /me
3. Backend:
   - Extract auth_user_id from JWT
   - Check if app_user exists for that auth_user_id
   - If not, create with display_name from user_metadata or email prefix
   - UNIQUE constraint on auth_user_id prevents duplicates
   - Transactional, idempotent
4. Profile now available via GET /api/users/me
```

## Module Boundaries (Future-Proofing)

Backend packages:
- `auth/` - JWT handling, CurrentUser, SecurityConfig
- `user/` - app_user and user_skill domain
- `config/` - Web, Security, JPA config
- `common/` - Error handling, DTOs
- Future: `hackathon/`, `project/`, `task/`, `contribution/`, etc. each with controller/service/repo/entity/dto

Frontend modules:
- `auth/` - AuthProvider, Login, Signup
- `dashboard/` - Protected dashboard
- `profile/` - Profile view/edit
- `components/` - Reusable UI
- `services/` - API client, supabase client
- `lib/` - utils
- Future modules as placeholders

## Deployment Considerations

- Frontend: Vercel / Netlify / Supabase Hosting - static build
- Backend: Render / Fly.io / Railway / Docker - needs DATABASE_URL and SUPABASE_JWKS_URL
- Database: Supabase Cloud PostgreSQL
- Migrations: Supabase CLI `supabase db push`
- Backend must use `validate` not `update` for Hibernate ddl-auto in production

## Non-Goals for Foundation
- Hackathon, Project, Task, Contribution, Chat, Notification, Portfolio not implemented yet
- But architecture leaves clean extension points
