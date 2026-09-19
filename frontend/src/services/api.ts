import { supabase } from '../lib/supabase'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export type ExperienceLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT'
export type SkillLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT'

export interface UserProfile {
  id: number
  authUserId: string
  displayName: string
  bio?: string | null
  college?: string | null
  course?: string | null
  yearOfStudy?: number | null
  experienceLevel?: ExperienceLevel | null
  createdAt: string
  updatedAt: string
}

export interface CreateProfileData {
  displayName: string
  bio?: string
  college?: string
  course?: string
  yearOfStudy?: number
  experienceLevel?: ExperienceLevel
}

export interface UpdateProfileData {
  displayName?: string
  bio?: string
  college?: string
  course?: string
  yearOfStudy?: number
  experienceLevel?: ExperienceLevel
}

export interface UserSkill {
  id: number
  userId: number
  skillName: string
  skillLevel: SkillLevel
  createdAt: string
}

export interface CreateSkillData {
  skillName: string
  skillLevel: SkillLevel
}

export interface UpdateSkillData {
  skillName?: string
  skillLevel?: SkillLevel
}

export class ApiError extends Error {
  status: number
  error: string
  details?: string[]

  constructor(status: number, error: string, message: string, details?: string[]) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.error = error
    this.details = details
  }
}

async function getAuthHeaders(): Promise<HeadersInit> {
  const { data: { session } } = await supabase.auth.getSession()
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  }

  if (session?.access_token) {
    headers['Authorization'] = `Bearer ${session.access_token}`
  }

  return headers
}

async function request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const headers = await getAuthHeaders()
  const url = `${API_BASE_URL}${endpoint}`

  const response = await fetch(url, {
    ...options,
    headers: {
      ...headers,
      ...options.headers,
    },
  })

  if (response.status === 204) {
    return {} as T
  }

  let data: any
  try {
    data = await response.json()
  } catch {
    data = null
  }

  if (!response.ok) {
    const error = data?.error || response.statusText || 'Error'
    const message = data?.message || `Request failed with status ${response.status}`
    const details = data?.details
    throw new ApiError(response.status, error, message, details)
  }

  return data as T
}

export const api = {
  getProfile(): Promise<UserProfile> {
    return request<UserProfile>('/api/users/me', { method: 'GET' })
  },

  createProfile(data: CreateProfileData): Promise<UserProfile> {
    return request<UserProfile>('/api/users/me/profile', {
      method: 'POST',
      body: JSON.stringify(data),
    })
  },

  updateProfile(data: UpdateProfileData): Promise<UserProfile> {
    return request<UserProfile>('/api/users/me', {
      method: 'PATCH',
      body: JSON.stringify(data),
    })
  },

  getSkills(): Promise<UserSkill[]> {
    return request<UserSkill[]>('/api/users/me/skills', { method: 'GET' })
  },

  addSkill(data: CreateSkillData): Promise<UserSkill> {
    return request<UserSkill>('/api/users/me/skills', {
      method: 'POST',
      body: JSON.stringify(data),
    })
  },

  updateSkill(skillId: number, data: UpdateSkillData): Promise<UserSkill> {
    return request<UserSkill>(`/api/users/me/skills/${skillId}`, {
      method: 'PATCH',
      body: JSON.stringify(data),
    })
  },

  deleteSkill(skillId: number): Promise<void> {
    return request<void>(`/api/users/me/skills/${skillId}`, {
      method: 'DELETE',
    })
  },
}
