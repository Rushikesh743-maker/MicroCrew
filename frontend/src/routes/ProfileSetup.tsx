import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import { api, ExperienceLevel, ApiError } from '../services/api'

export const ProfileSetup: React.FC = () => {
  const { user } = useAuth()
  const navigate = useNavigate()

  const [displayName, setDisplayName] = useState('')
  const [bio, setBio] = useState('')
  const [college, setCollege] = useState('')
  const [course, setCourse] = useState('')
  const [yearOfStudy, setYearOfStudy] = useState<number | ''>('')
  const [experienceLevel, setExperienceLevel] = useState<ExperienceLevel>('BEGINNER')

  const [loading, setLoading] = useState(false)
  const [checkingExisting, setCheckingExisting] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    // Check if profile already exists. If yes, redirect to dashboard immediately.
    const checkProfile = async () => {
      try {
        await api.getProfile()
        navigate('/dashboard', { replace: true })
      } catch (err: any) {
        if (err instanceof ApiError && err.status === 404) {
          // Normal: No profile exists yet, initialize form
          const initialName = user?.user_metadata?.display_name || ''
          setDisplayName(initialName)
        } else {
          // If backend error/offline, display notice
          console.warn('Backend check:', err)
        }
      } finally {
        setCheckingExisting(false)
      }
    }

    checkProfile()
  }, [user, navigate])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)

    if (!displayName.trim() || displayName.trim().length < 2) {
      setError('Display name must be at least 2 characters.')
      return
    }

    try {
      setLoading(true)
      await api.createProfile({
        displayName: displayName.trim(),
        bio: bio.trim() || undefined,
        college: college.trim() || undefined,
        course: course.trim() || undefined,
        yearOfStudy: yearOfStudy !== '' ? Number(yearOfStudy) : undefined,
        experienceLevel,
      })

      navigate('/dashboard', { replace: true })
    } catch (err: any) {
      setError(err?.message || 'Failed to create profile. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  if (checkingExisting) {
    return (
      <div className="flex min-h-[50vh] items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
      </div>
    )
  }

  return (
    <div className="max-w-2xl mx-auto my-8 p-8 bg-white rounded-xl border border-slate-200 shadow-sm">
      <div className="mb-6">
        <span className="text-xs font-semibold text-indigo-600 uppercase tracking-wider">
          Step 2 of 2
        </span>
        <h1 className="text-2xl font-bold text-slate-900 mt-1">Set Up Your Profile</h1>
        <p className="text-sm text-slate-600 mt-1">
          Complete your student profile to collaborate with teammates on MicroCrew.
        </p>
      </div>

      {error && (
        <div className="mb-6 p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-5">
        <div>
          <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
            Display Name <span className="text-red-500">*</span>
          </label>
          <input
            type="text"
            required
            value={displayName}
            onChange={(e) => setDisplayName(e.target.value)}
            placeholder="e.g. Alex Johnson"
            className="w-full px-3.5 py-2 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 text-sm"
          />
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
            Bio
          </label>
          <textarea
            rows={3}
            value={bio}
            onChange={(e) => setBio(e.target.value)}
            placeholder="Brief intro about your interests, hackathons, or software focus..."
            className="w-full px-3.5 py-2 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 text-sm"
          />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
              College / University
            </label>
            <input
              type="text"
              value={college}
              onChange={(e) => setCollege(e.target.value)}
              placeholder="e.g. Stanford University"
              className="w-full px-3.5 py-2 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 text-sm"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
              Course / Major
            </label>
            <input
              type="text"
              value={course}
              onChange={(e) => setCourse(e.target.value)}
              placeholder="e.g. B.S. Computer Science"
              className="w-full px-3.5 py-2 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 text-sm"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
              Year of Study (1 - 10)
            </label>
            <input
              type="number"
              min={1}
              max={10}
              value={yearOfStudy}
              onChange={(e) => setYearOfStudy(e.target.value ? Number(e.target.value) : '')}
              placeholder="e.g. 2"
              className="w-full px-3.5 py-2 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 text-sm"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
              Experience Level
            </label>
            <select
              value={experienceLevel}
              onChange={(e) => setExperienceLevel(e.target.value as ExperienceLevel)}
              className="w-full px-3.5 py-2 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 text-sm bg-white"
            >
              <option value="BEGINNER">Beginner</option>
              <option value="INTERMEDIATE">Intermediate</option>
              <option value="ADVANCED">Advanced</option>
              <option value="EXPERT">Expert</option>
            </select>
          </div>
        </div>

        <div className="pt-4 flex justify-end">
          <button
            type="submit"
            disabled={loading}
            className="px-6 py-2.5 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 text-white font-medium rounded-lg text-sm shadow-sm transition"
          >
            {loading ? 'Saving Profile...' : 'Complete Profile & Continue'}
          </button>
        </div>
      </form>
    </div>
  )
}
