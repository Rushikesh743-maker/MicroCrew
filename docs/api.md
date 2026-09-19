# MicroCrew API Documentation

## Base URL
- Local: `http://localhost:8080`
- Production: `https://api.microcrew.example.com` (placeholder)

## Authentication

All protected endpoints require:
```
Authorization: Bearer <supabase_access_token>
```

Get token via Supabase Auth login in frontend. Backend verifies via JWKS.

## Public Endpoints

### GET /api/public/health

Health check, no auth required.

**Response 200:**
```json
{
  "status": "UP",
  "timestamp": "2025-09-19T...",
  "service": "microcrew-api"
}
```

### POST /api/public/auth/profile

Idempotent profile creation after signup. Requires auth but is under /public for initial creation flow.

Creates `app_user` if not exists, returns existing if already exists.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "id": 1,
  "authUserId": "uuid",
  "displayName": "John Doe",
  "bio": null,
  "college": null,
  "course": null,
  "yearOfStudy": null,
  "experienceLevel": null,
  "createdAt": "...",
  "updatedAt": "..."
}
```

**Response 401:** Invalid token

---

## User Endpoints (Authenticated)

### GET /api/users/me

Get current user's profile. Auto-creates profile if not exists (lazy creation).

**Auth:** Required

**Response 200:**
```json
{
  "id": 1,
  "authUserId": "uuid-from-jwt",
  "displayName": "John Doe",
  "bio": "CS student passionate about...",
  "college": "MIT",
  "course": "Computer Science",
  "yearOfStudy": 3,
  "experienceLevel": "INTERMEDIATE",
  "createdAt": "2025-09-19T10:00:00Z",
  "updatedAt": "2025-09-19T10:00:00Z"
}
```

**Response 401:** Not authenticated
**Response 404:** Profile not found (should auto-create, so rare)

---

### PATCH /api/users/me

Update own profile. Only allowed fields can be updated. `id` and `authUserId` are immutable.

**Auth:** Required

**Request Body:**
```json
{
  "displayName": "John Doe Updated",
  "bio": "Updated bio",
  "college": "Stanford",
  "course": "CS",
  "yearOfStudy": 4,
  "experienceLevel": "ADVANCED"
}
```

All fields optional. Validation:
- `displayName`: 2-100 chars if provided
- `bio`: max 1000 chars
- `college`: max 200
- `course`: max 200
- `yearOfStudy`: 1-10
- `experienceLevel`: BEGINNER, INTERMEDIATE, ADVANCED, EXPERT

**Response 200:** Updated profile

**Response 400:** Validation error
```json
{
  "error": "VALIDATION_ERROR",
  "message": "Invalid request",
  "details": { "displayName": "must be between 2 and 100 characters" },
  "timestamp": "..."
}
```

**Response 401:** Not authenticated

---

### GET /api/users/me/skills

List current user's skills.

**Auth:** Required

**Response 200:**
```json
[
  {
    "id": 1,
    "userId": 1,
    "skillName": "React",
    "skillLevel": "ADVANCED",
    "createdAt": "2025-09-19T10:00:00Z"
  },
  {
    "id": 2,
    "userId": 1,
    "skillName": "Java",
    "skillLevel": "INTERMEDIATE",
    "createdAt": "2025-09-19T10:00:00Z"
  }
]
```

---

### POST /api/users/me/skills

Add a skill to current user. Enforces uniqueness per user.

**Auth:** Required

**Request Body:**
```json
{
  "skillName": "Python",
  "skillLevel": "BEGINNER"
}
```

Validation:
- `skillName`: 1-100 chars, required, trimmed, case-sensitive uniqueness (recommend normalize to Title Case in frontend)
- `skillLevel`: required, enum

**Response 201:**
```json
{
  "id": 3,
  "userId": 1,
  "skillName": "Python",
  "skillLevel": "BEGINNER",
  "createdAt": "..."
}
```

**Response 400:** Validation error
**Response 401:** Not authenticated
**Response 409:** Duplicate skill
```json
{
  "error": "CONFLICT",
  "message": "Skill 'Python' already exists for this user",
  "timestamp": "..."
}
```

---

### DELETE /api/users/me/skills/{id}

Delete own skill. Object-level authorization ensures only owner can delete.

**Auth:** Required

**Path Param:** `id` - skill ID (Long)

**Response 204:** No content, deleted

**Response 401:** Not authenticated
**Response 404:** Skill not found or not owned (returns 404 not 403 to avoid enumeration)

---

## Error Format

Consistent error response:

```json
{
  "error": "ERROR_CODE",
  "message": "Human readable message",
  "details": { ... optional },
  "timestamp": "2025-09-19T10:00:00Z",
  "path": "/api/users/me"
}
```

Error codes:
- `VALIDATION_ERROR` - 400
- `UNAUTHORIZED` - 401
- `FORBIDDEN` - 403
- `NOT_FOUND` - 404
- `CONFLICT` - 409 (duplicate)
- `UNPROCESSABLE_ENTITY` - 422
- `INTERNAL_ERROR` - 500

No stack traces exposed.

## Future Endpoints (Planned, Not Implemented)

- `GET /api/hackathons` - list hackathons
- `POST /api/projects` - create project
- `GET /api/projects/{id}` - project details
- `POST /api/projects/{id}/applications` - apply to project
- `GET /api/projects/{id}/members`
- `POST /api/projects/{id}/tasks`
- `POST /api/contributions` - append-only ledger
- etc.

## Rate Limiting (Future)

Planned: bucket4j or gateway rate limiting.

## Pagination (Future)

For list endpoints, use:
```
GET /api/projects?page=0&size=20&sort=createdAt,desc
```

## CORS

Backend allows `FRONTEND_URL` (default http://localhost:5173) for dev.
Production will allow configured frontend domain.

## Idempotency

- Profile creation is idempotent via UNIQUE constraint
- Skill creation: duplicate returns 409, not 500
- Future: contributions will use idempotency keys
