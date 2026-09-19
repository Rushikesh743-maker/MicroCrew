# MicroCrew Architecture Decision Records (ADR)

## Decision 01: Supabase Auth instead of custom authentication

**Date**: 2025-09-19
**Status**: Accepted

**Context**:
Need secure authentication for students. Options: custom JWT/password storage, Auth0, Firebase Auth, Supabase Auth.

**Decision**:
Use Supabase Auth.

**Rationale**:
- Integrated with Supabase PostgreSQL (auth.users table, FK support)
- Handles password hashing, email confirmation, refresh tokens, security best practices
- JWT format compatible with Spring Security resource server
- No need to implement custom password storage (forbidden by requirements)
- Free tier sufficient for students
- Built-in RLS integration via auth.uid()
- Reduces backend complexity and security risk

**Consequences**:
- Backend must verify Supabase JWTs via JWKS
- Frontend must use supabase-js client
- Auth data lives in Supabase auth schema, not our control
- Migration away would require auth export

**Alternatives Considered**:
- Custom auth: rejected - security risk, against requirements
- Auth0: rejected - extra cost, separate from DB

---

## Decision 02: Supabase PostgreSQL as the production database

**Date**: 2025-09-19
**Status**: Accepted

**Context**:
Need production database. Options: separate RDS, PlanetScale, Supabase, Neon.

**Decision**:
Use Supabase PostgreSQL as sole production database.

**Rationale**:
- Single platform for Auth + DB + future Storage
- PostgreSQL full features (RLS, extensions, triggers)
- Managed backups, scaling
- Direct JDBC connection for Spring Boot
- RLS provides defense-in-depth
- Supabase CLI for migrations

**Consequences**:
- Backend connects via DATABASE_URL to Supabase
- Must handle connection pooling (Supabase pooler)
- Migrations managed via Supabase CLI SQL files
- No other production DB allowed per requirements

**Alternatives**:
- RDS + Supabase Auth: rejected - two systems, more complexity
- H2 for prod: rejected - not production ready

---

## Decision 03: Supabase SQL migrations as schema source of truth

**Date**: 2025-09-19
**Status**: Accepted

**Context**:
How to manage schema? Options: Hibernate auto DDL, Flyway, Liquibase, Supabase migrations.

**Decision**:
Supabase CLI SQL migrations are source of truth. Backend uses `validate` mode.

**Rationale**:
- Requirement: Do NOT use Hibernate auto creation in production
- Supabase migrations are versioned, auditable, run via `supabase db push`
- SQL files are explicit, reviewable
- Prevents drift between environments
- Hibernate validate ensures entity matches DB, but never modifies

**Consequences**:
- All schema changes must be new migration file
- Backend `application.yml` sets `ddl-auto: validate` for prod, `none` or `validate` for dev
- Local dev can use `supabase db reset` to apply migrations
- Entities must match migration exactly

**Alternatives**:
- Flyway: could work but duplicates Supabase migration system
- Hibernate update: rejected - unsafe for production

---

## Decision 04: Spring service-layer authorization in addition to RLS

**Date**: 2025-09-19
**Status**: Accepted

**Context**:
Where to enforce authorization? Options: only RLS, only backend, both.

**Decision**:
Enforce authorization in Spring service layer as primary, RLS as defense-in-depth.

**Rationale**:
- RLS alone: backend uses service_role or direct JDBC which bypasses RLS; also business logic authorization (e.g., project owner checks) complex in SQL policies
- Backend alone: if bug, DB exposed; direct Supabase client access not protected
- Both: defense-in-depth, best practice
- Service layer allows rich business rules, unit testable
- RLS protects if anon key leaked or frontend queries directly

**Consequences**:
- Every service method must check ownership via authUserId
- RLS policies must be maintained alongside service logic
- Slight duplication but intentional for security
- Documented in authorization.md

**Alternatives**:
- Only RLS: rejected - insufficient for complex business rules, bypassed by service_role
- Only backend: rejected - not defense-in-depth

---

## Decision 05: Monorepo structure

**Date**: 2025-09-19
**Status**: Accepted

**Context**:
Repo organization: separate repos vs monorepo.

**Decision**:
Monorepo with `frontend/`, `backend/`, `supabase/`, `docs/`.

**Rationale**:
- Single clone for full-stack dev
- Shared .env.example, docs, CI
- Easier to keep frontend/backend in sync for API contracts
- Simplifies local setup with docker-compose
- Required by task structure

**Consequences**:
- CI builds both frontend and backend
- Need to handle different tech stacks in one repo
- .gitignore must cover both

---

## Decision 06: TypeScript for frontend

**Date**: 2025-09-19
**Status**: Accepted

**Context**:
JS vs TS for React.

**Decision**:
TypeScript (preferred per requirements).

**Rationale**:
- Type safety for API contracts
- Better IDE support
- Catches errors early
- Industry standard for serious projects

**Consequences**:
- Need tsconfig, type definitions
- Supabase client types

---

## Decision 07: Skill levels as ENUM

**Date**: 2025-09-19
**Status**: Accepted

**Context**:
How to store skill level? String check vs ENUM type.

**Decision**:
PostgreSQL ENUM type `skill_level_enum` + CHECK in app_user for experience_level.

**Rationale**:
- ENUM enforces valid values at DB level
- Prevents typos
- Self-documenting
- For app_user experience_level, using TEXT + CHECK for flexibility (could be ENUM later)

**Consequences**:
- Adding new level requires migration to alter ENUM
- Backend Java enum must match DB enum

---

## Future Decisions (Planned)

- Decision 08: Contribution ledger append-only design
- Decision 09: Matching algorithm
- Decision 10: Real-time chat (Supabase Realtime vs WebSocket)
- Decision 11: File storage (Supabase Storage)
