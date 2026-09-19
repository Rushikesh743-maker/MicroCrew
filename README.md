# MicroCrew

> A collaboration platform for students to discover hackathons, create projects, find teammates based on skills and availability, form project crews, collaborate through tasks and milestones, track verifiable contributions, and generate evidence-based project portfolios.

## Project Status: Foundation (v0.1.0)

Foundation implements secure authentication, user profiles, and skills management with production-grade architecture.

### Implemented ✅
- Supabase Auth integration (signup, login, session persistence, logout)
- JWT verification in Spring Boot
- Protected routes (frontend) and protected APIs (backend)
- User profile creation (idempotent, unique constraint on auth_user_id)
- User profile retrieval and update (object-level authorization)
- User skills CRUD (unique constraint, ownership enforcement)
- Row Level Security policies (defense-in-depth)
- Consistent error handling
- Testing foundation (JUnit, Mockito, Security tests, Repository tests)
- CI pipeline (GitHub Actions)
- Documentation

### Planned (Next Stages) 🚧
- Hackathon Hub
- Project creation and crew formation
- Applications and matching
- Tasks and milestones
- Contribution ledger (append-only, verifiable)
- Chat and notifications
- Portfolio generation
- Admin dashboard
- Supabase Storage for assets

## Architecture

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
```

### Authentication Flow
```
React → Supabase Auth → Access Token → Spring Boot JWT Verification → Authenticated Principal → Object-Level Authorization
```

## Technology Stack

- **Frontend**: React 18, Vite, TypeScript, Tailwind CSS, React Router, Supabase JS
- **Backend**: Java 21, Spring Boot 3.2, Spring Security 6, Spring Data JPA, Maven
- **Database**: Supabase PostgreSQL (production), H2 for tests
- **Auth**: Supabase Auth (no custom password storage)
- **Migrations**: Supabase CLI SQL migrations (source of truth)
- **Testing**: JUnit 5, Mockito, Spring Boot Test, Testcontainers (ready), Playwright (planned)
- **CI**: GitHub Actions

## Project Structure

```
microcrew/
├── frontend/               # React Vite app
│   ├── src/
│   │   ├── auth/           # AuthProvider, context
│   │   ├── components/     # Layout, ProtectedRoute
│   │   ├── routes/         # Home, Login, Signup, Dashboard, Profile
│   │   ├── lib/            # supabase client, api client
│   │   └── ...
│   └── package.json
├── backend/                # Spring Boot API
│   ├── src/main/java/com/microcrew/
│   │   ├── auth/           # JWT handling, CurrentUser
│   │   ├── user/           # app_user & user_skill domain
│   │   ├── config/         # Security, CORS, JPA
│   │   ├── common/         # Error handling
│   │   └── ... (future modules)
│   └── pom.xml
├── supabase/
│   ├── migrations/         # SQL migrations (source of truth)
│   ├── seed.sql
│   └── config.toml
├── docs/                   # Architecture, DB, Auth, Decisions, API, Testing, Deployment
├── .github/workflows/      # CI
├── docker-compose.yml
├── .env.example
└── README.md
```

## Local Setup

### Prerequisites
- Node.js 20+
- Java 21
- Maven 3.9+
- Supabase account (or local Supabase CLI)
- Git

### 1. Clone and Env

```bash
git clone <repo>
cd microcrew
cp .env.example .env
# Fill in Supabase credentials
```

### 2. Supabase Setup

Create a project at https://supabase.com and get:

- `VITE_SUPABASE_URL` = Project URL
- `VITE_SUPABASE_ANON_KEY` = anon key
- `SUPABASE_SERVICE_ROLE_KEY` = service_role (backend only)
- `DATABASE_URL` = `jdbc:postgresql://db.<ref>.supabase.co:5432/postgres`
- `SUPABASE_JWKS_URL` = `https://<ref>.supabase.co/auth/v1/.well-known/jwks.json` (check Supabase docs - may need to use /auth/v1/keys)

Run migrations:

**Option A: Supabase CLI**
```bash
supabase link --project-ref <your-project-ref>
supabase db push
```

**Option B: SQL Editor**
Copy contents of `supabase/migrations/*.sql` and run in Supabase Dashboard SQL Editor in order.

Verify tables exist: `app_user`, `user_skill`, RLS enabled.

### 3. Environment Variables

See `.env.example` for all vars.

**Frontend (.env in frontend/ or root):**
```
VITE_SUPABASE_URL=https://your-project.supabase.co
VITE_SUPABASE_ANON_KEY=your-anon-key
VITE_API_URL=http://localhost:8080
```

**Backend (env vars or .env):**
```
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_JWKS_URL=https://your-project.supabase.co/auth/v1/.well-known/jwks.json
SUPABASE_SERVICE_ROLE_KEY=...
DATABASE_URL=jdbc:postgresql://db.your-project.supabase.co:5432/postgres
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=...
FRONTEND_URL=http://localhost:5173
```

### 4. Running Frontend

```bash
cd frontend
npm install
npm run dev
# Frontend at http://localhost:5173
```

### 5. Running Backend

```bash
cd backend
# Set env vars (example with export, or use .env with dotenv plugin, or IDE run config)
export DATABASE_URL=jdbc:postgresql://...
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=...
export SUPABASE_URL=https://...
export SUPABASE_JWKS_URL=https://.../.well-known/jwks.json
export FRONTEND_URL=http://localhost:5173

./mvnw spring-boot:run
# Or: mvn spring-boot:run
# Backend at http://localhost:8080
# Health: http://localhost:8080/api/public/health
```

For local dev without Supabase cloud, you can use local Postgres via docker-compose:

```bash
docker-compose up postgres -d
# Then DATABASE_URL=jdbc:postgresql://localhost:5432/microcrew
```

But you still need Supabase Auth for JWTs - use Supabase cloud for auth even if using local Postgres, or run `supabase start` locally.

### 6. Testing

**Backend:**
```bash
cd backend
mvn test
```

**Frontend:**
```bash
cd frontend
npm run build # verifies TypeScript and build
```

## API Endpoints (Foundation)

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | /api/public/health | No | Health check |
| POST | /api/public/auth/profile | Yes | Idempotent profile creation |
| GET | /api/users/me | Yes | Get own profile (auto-creates) |
| PATCH | /api/users/me | Yes | Update own profile |
| GET | /api/users/me/skills | Yes | List own skills |
| POST | /api/users/me/skills | Yes | Add skill |
| DELETE | /api/users/me/skills/{id} | Yes | Delete own skill |

All protected endpoints require `Authorization: Bearer <supabase_jwt>`.

## Security

- No hardcoded credentials
- No fake auth - Supabase Auth is source of truth
- Backend derives identity from JWT `sub` claim, never trusts client-sent user ID
- Object-level authorization: users can only modify own profile/skills via `findByAuthUserId` and `findByIdAndUserId`
- RLS enabled as defense-in-depth
- Service role key never exposed to frontend
- Consistent error format, no stack traces leaked
- Validation on all DTOs
- Unique constraints enforce invariants at DB level

## Deployment

See `docs/deployment.md`.

- Frontend: Vercel/Netlify - static build from `frontend/dist`
- Backend: Render/Railway/Fly.io - Docker or jar with env vars
- Database: Supabase Cloud PostgreSQL
- Migrations: `supabase db push`

CI runs on push/PR: backend build+test, frontend build.

## Documentation

- `docs/architecture.md` - Architecture and auth flow
- `docs/database.md` - Schema, RLS, constraints
- `docs/authorization.md` - AuthZ design
- `docs/decisions.md` - ADRs
- `docs/api.md` - API docs
- `docs/testing.md` - Testing strategy
- `docs/deployment.md` - Deployment guide

## Contributing

Foundation is complete. Next stages will implement hackathons, projects, etc. Keep modules independently understandable, thin controllers, business logic in services, DB access in repositories.

## License

MIT (or your choice)
