# MicroCrew Deployment Guide

## Overview
Foundation is deployment-ready in structure, but deployment not automated yet per requirements (CI reliable first).

## Environments

- **Local**: Frontend Vite dev server + Spring Boot + Supabase local or cloud
- **Staging**: Future - Vercel + Render + Supabase cloud
- **Production**: Future - same as staging with prod env vars

## Prerequisites

- Supabase project (cloud) created
- Supabase URL, anon key, service_role key
- Database password
- JWKS URL

## Supabase Setup

1. Create project at https://supabase.com
2. Get credentials from Project Settings > API:
   - `VITE_SUPABASE_URL` = Project URL
   - `VITE_SUPABASE_ANON_KEY` = anon public key
   - `SUPABASE_SERVICE_ROLE_KEY` = service_role (backend only!)
3. Get DB connection string from Database settings
4. Get JWKS URL: `https://<project>.supabase.co/auth/v1/.well-known/jwks.json` or check docs - may be `https://<project>.supabase.co/auth/v1/keys` depending on Supabase version. Try both.

5. Run migrations:
   ```bash
   # Using Supabase CLI
   supabase link --project-ref <project-ref>
   supabase db push

   # Or via SQL editor in dashboard - copy/paste migration files
   ```

6. Verify tables:
   - `app_user` exists
   - `user_skill` exists
   - RLS enabled
   - Policies exist

## Backend Deployment

### Docker

`backend/Dockerfile` (to be created, example):

```dockerfile
FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline
COPY src src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:
```bash
cd backend
docker build -t microcrew-backend .
docker run -p 8080:8080 --env-file ../.env microcrew-backend
```

### Environment Variables (Backend)

Required:
- `DATABASE_URL` - jdbc:postgresql://...
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`
- `SUPABASE_URL`
- `SUPABASE_JWKS_URL`
- `SUPABASE_SERVICE_ROLE_KEY` (if using Supabase client in backend)
- `FRONTEND_URL` - for CORS
- `SPRING_PROFILES_ACTIVE=prod`

### Health Check

`GET /api/public/health` should return 200.

### Platforms

- **Render**: Connect GitHub, set build command `cd backend && ./mvnw package`, start `java -jar target/*.jar`
- **Railway**: Similar
- **Fly.io**: `fly launch` with Dockerfile
- **AWS ECS / GCP Cloud Run**: Docker image

## Frontend Deployment

### Build

```bash
cd frontend
npm install
npm run build
# output in dist/
```

### Environment Variables (Frontend Build Time)

- `VITE_SUPABASE_URL`
- `VITE_SUPABASE_ANON_KEY`
- `VITE_API_URL` - backend URL

### Platforms

- **Vercel**: Import repo, set root to `frontend`, build command `npm run build`, output `dist`, set env vars
- **Netlify**: Similar
- **Supabase Hosting**: Future

### Docker (Optional)

`frontend/Dockerfile`:
```dockerfile
FROM node:20-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## Docker Compose (Local Full Stack)

```bash
docker-compose up --build
```

- Postgres at 5432
- Backend at 8080
- Frontend at 5173

But for local dev, better to run Supabase cloud + `npm run dev` + `./mvnw spring-boot:run` separately.

## CI/CD (GitHub Actions)

Current CI (`.github/workflows/ci.yml`):
- On push/PR: backend build + test, frontend build

Future:
- Deploy to staging on main merge
- Require tests pass

## Security Checklist for Deployment

- [ ] No real secrets in repo
- [ ] `.env` not committed, only `.env.example`
- [ ] Frontend only has anon key, never service_role
- [ ] Backend uses `validate` not `update` for ddl-auto in prod
- [ ] CORS restricted to frontend domain
- [ ] HTTPS enforced
- [ ] Supabase RLS enabled
- [ ] JWT verification via JWKS, not hardcoded
- [ ] Logs don't contain tokens or passwords
- [ ] Error responses don't leak stack traces

## Monitoring (Future)

- Spring Actuator `/actuator/health`
- Logging with structured JSON
- Supabase dashboard for DB
- Sentry for errors

## Known Limitations (Foundation)

- No auto-deployment yet
- No staging environment
- No DB backup automation beyond Supabase default
- No rate limiting
- No CDN for frontend
