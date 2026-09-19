import React, { useEffect, useState } from 'react'
import { useAuth } from '../auth/AuthContext'
import {
  api,
  UserProfile,
  UserSkill,
  ExperienceLevel,
  SkillLevel,
  ApiError,
} from '../services/api'

export const Profile: React.FC = () => {
  const { user } = useAuth()

  const [profile, setProfile] = useState<UserProfile | null>(null)
  const [skills, setSkills] = useState<UserSkill[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  // Profile Edit State
  const [isEditingProfile, setIsEditingProfile] = useState(false)
  const [displayName, setDisplayName] = useState('')
  const [bio, setBio] = useState('')
  const [college, setCollege] = useState('')
  const [course, setCourse] = useState('')
  const [yearOfStudy, setYearOfStudy] = useState<number | ''>('')
  const [experienceLevel, setExperienceLevel] = useState<ExperienceLevel>('BEGINNER')
  const [profileSubmitting, setProfileSubmitting] = useState(false)
  const [profileError, setProfileError] = useState<string | null>(null)

  // Skill Add State
  const [newSkillName, setNewSkillName] = useState('')
  const [newSkillLevel, setNewSkillLevel] = useState<SkillLevel>('BEGINNER')
  const [skillSubmitting, setSkillSubmitting] = useState(false)
  const [skillError, setSkillError] = useState<string | null>(null)

  // Skill Edit State
  const [editingSkillId, setEditingSkillId] = useState<number | null>(null)
  const [editingSkillLevel, setEditingSkillLevel] = useState<SkillLevel>('BEGINNER')

  const loadData = async () => {
    try {
      setLoading(true)
      setError(null)
      const [p, s] = await Promise.all([
        api.getProfile().catch((err) => {
          if (err instanceof ApiError && err.status === 404) return null
          throw err
        }),
        api.getSkills().catch(() => []),
      ])

      setProfile(p)
      if (p) {
        setDisplayName(p.displayName || '')
        setBio(p.bio || '')
        setCollege(p.college || '')
        setCourse(p.course || '')
        setYearOfStudy(p.yearOfStudy ?? '')
        setExperienceLevel(p.experienceLevel || 'BEGINNER')
      }
      setSkills(s)
    } catch (err: any) {
      setError(err?.message || 'Failed to load profile data.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadData()
  }, [])

  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault()
    setProfileError(null)

    if (!displayName.trim() || displayName.trim().length < 2) {
      setProfileError('Display name must be at least 2 characters.')
      return
    }

    try {
      setProfileSubmitting(true)
      const updated = await api.updateProfile({
        displayName: displayName.trim(),
        bio: bio.trim() || undefined,
        college: college.trim() || undefined,
        course: course.trim() || undefined,
        yearOfStudy: yearOfStudy !== '' ? Number(yearOfStudy) : undefined,
        experienceLevel,
      })

      setProfile(updated)
      setIsEditingProfile(false)
    } catch (err: any) {
      setProfileError(err?.message || 'Failed to update profile.')
    } finally {
      setProfileSubmitting(false)
    }
  }

  const handleAddSkill = async (e: React.FormEvent) => {
    e.preventDefault()
    setSkillError(null)

    if (!newSkillName.trim()) {
      setSkillError('Skill name cannot be empty.')
      return
    }

    try {
      setSkillSubmitting(true)
      const newSkill = await api.addSkill({
        skillName: newSkillName.trim(),
        skillLevel: newSkillLevel,
      })
      setSkills((prev) => [...prev, newSkill])
      setNewSkillName('')
      setNewSkillLevel('BEGINNER')
    } catch (err: any) {
      setSkillError(err?.message || 'Failed to add skill.')
    } finally {
      setSkillSubmitting(false)
    }
  }

  const handleUpdateSkill = async (skillId: number) => {
    try {
      const updated = await api.updateSkill(skillId, {
        skillLevel: editingSkillLevel,
      })
      setSkills((prev) => prev.map((s) => (s.id === skillId ? updated : s)))
      setEditingSkillId(null)
    } catch (err: any) {
      alert(err?.message || 'Failed to update skill.')
    }
  }

  const handleDeleteSkill = async (skillId: number) => {
    if (!confirm('Are you sure you want to remove this skill?')) return

    try {
      await api.deleteSkill(skillId)
      setSkills((prev) => prev.filter((s) => s.id !== skillId))
    } catch (err: any) {
      alert(err?.message || 'Failed to delete skill.')
    }
  }

  if (loading) {
    return (
      <div className="flex min-h-[50vh] items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
      </div>
    )
  }

  return (
    <div className="max-w-4xl mx-auto space-y-8">
      {error && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-xl text-red-700 text-sm">
          {error}
        </div>
      )}

      {/* Profile Details Card */}
      <div className="bg-white rounded-xl border border-slate-200 p-6 sm:p-8 shadow-sm">
        <div className="flex justify-between items-start mb-6">
          <div>
            <h1 className="text-2xl font-bold text-slate-900">Student Profile</h1>
            <p className="text-sm text-slate-500 mt-0.5">
              Account Email: <span className="font-semibold text-slate-700">{user?.email}</span>
            </p>
          </div>
          {!isEditingProfile && profile && (
            <button
              onClick={() => setIsEditingProfile(true)}
              className="px-4 py-2 border border-slate-300 text-slate-700 hover:bg-slate-50 rounded-lg text-xs font-semibold transition"
            >
              Edit Profile
            </button>
          )}
        </div>

        {isEditingProfile ? (
          <form onSubmit={handleUpdateProfile} className="space-y-4">
            {profileError && (
              <div className="p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-xs">
                {profileError}
              </div>
            )}

            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                Display Name
              </label>
              <input
                type="text"
                required
                value={displayName}
                onChange={(e) => setDisplayName(e.target.value)}
                className="w-full px-3 py-2 rounded-lg border border-slate-300 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
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
                className="w-full px-3 py-2 rounded-lg border border-slate-300 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                  College
                </label>
                <input
                  type="text"
                  value={college}
                  onChange={(e) => setCollege(e.target.value)}
                  className="w-full px-3 py-2 rounded-lg border border-slate-300 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                  Course
                </label>
                <input
                  type="text"
                  value={course}
                  onChange={(e) => setCourse(e.target.value)}
                  className="w-full px-3 py-2 rounded-lg border border-slate-300 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
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
                  className="w-full px-3 py-2 rounded-lg border border-slate-300 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                  Experience Level
                </label>
                <select
                  value={experienceLevel}
                  onChange={(e) => setExperienceLevel(e.target.value as ExperienceLevel)}
                  className="w-full px-3 py-2 rounded-lg border border-slate-300 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
                >
                  <option value="BEGINNER">Beginner</option>
                  <option value="INTERMEDIATE">Intermediate</option>
                  <option value="ADVANCED">Advanced</option>
                  <option value="EXPERT">Expert</option>
                </select>
              </div>
            </div>

            <div className="flex justify-end gap-3 pt-3">
              <button
                type="button"
                onClick={() => setIsEditingProfile(false)}
                className="px-4 py-2 border border-slate-300 text-slate-700 rounded-lg text-xs font-semibold hover:bg-slate-50 transition"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={profileSubmitting}
                className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg text-xs font-semibold transition disabled:opacity-50"
              >
                {profileSubmitting ? 'Saving...' : 'Save Changes'}
              </button>
            </div>
          </form>
        ) : profile ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 text-sm">
            <div>
              <span className="text-xs text-slate-400 uppercase font-semibold">Display Name</span>
              <p className="font-semibold text-slate-900 mt-0.5">{profile.displayName}</p>
            </div>
            <div>
              <span className="text-xs text-slate-400 uppercase font-semibold">Experience Level</span>
              <p className="font-semibold text-slate-900 mt-0.5">{profile.experienceLevel || 'Not set'}</p>
            </div>
            <div>
              <span className="text-xs text-slate-400 uppercase font-semibold">College / University</span>
              <p className="text-slate-800 mt-0.5">{profile.college || 'Not set'}</p>
            </div>
            <div>
              <span className="text-xs text-slate-400 uppercase font-semibold">Course / Major</span>
              <p className="text-slate-800 mt-0.5">{profile.course || 'Not set'}</p>
            </div>
            <div>
              <span className="text-xs text-slate-400 uppercase font-semibold">Year of Study</span>
              <p className="text-slate-800 mt-0.5">
                {profile.yearOfStudy ? `Year ${profile.yearOfStudy}` : 'Not set'}
              </p>
            </div>
            <div className="sm:col-span-2">
              <span className="text-xs text-slate-400 uppercase font-semibold">Bio</span>
              <p className="text-slate-700 mt-0.5 whitespace-pre-line leading-relaxed">
                {profile.bio || 'No bio provided.'}
              </p>
            </div>
          </div>
        ) : (
          <div className="text-center py-6">
            <p className="text-slate-600 text-sm mb-4">You have not created your application profile yet.</p>
            <a
              href="/profile/setup"
              className="inline-block px-4 py-2 bg-indigo-600 text-white rounded-lg text-sm font-medium hover:bg-indigo-700 transition"
            >
              Set Up Profile
            </a>
          </div>
        )}
      </div>

      {/* Skills Section */}
      <div className="bg-white rounded-xl border border-slate-200 p-6 sm:p-8 shadow-sm">
        <h2 className="text-xl font-bold text-slate-900 mb-2">Technical Skills</h2>
        <p className="text-xs text-slate-500 mb-6">
          Add skills to help hackathon crew leaders discover your technical profile.
        </p>

        {/* Add Skill Form */}
        <form onSubmit={handleAddSkill} className="mb-8 p-4 bg-slate-50 rounded-xl border border-slate-200">
          <h3 className="text-xs font-bold text-slate-700 uppercase tracking-wider mb-3">
            Add New Skill
          </h3>
          {skillError && (
            <div className="mb-3 p-2.5 bg-red-50 border border-red-200 rounded-lg text-red-700 text-xs">
              {skillError}
            </div>
          )}
          <div className="flex flex-col sm:flex-row gap-3">
            <div className="flex-1">
              <input
                type="text"
                required
                value={newSkillName}
                onChange={(e) => setNewSkillName(e.target.value)}
                placeholder="e.g. React, Spring Boot, PyTorch"
                className="w-full px-3 py-2 bg-white border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div className="w-full sm:w-44">
              <select
                value={newSkillLevel}
                onChange={(e) => setNewSkillLevel(e.target.value as SkillLevel)}
                className="w-full px-3 py-2 bg-white border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
              >
                <option value="BEGINNER">Beginner</option>
                <option value="INTERMEDIATE">Intermediate</option>
                <option value="ADVANCED">Advanced</option>
                <option value="EXPERT">Expert</option>
              </select>
            </div>
            <button
              type="submit"
              disabled={skillSubmitting}
              className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 text-white rounded-lg text-xs font-semibold transition whitespace-nowrap shadow-sm"
            >
              {skillSubmitting ? 'Adding...' : 'Add Skill'}
            </button>
          </div>
        </form>

        {/* Skills List */}
        <div className="space-y-3">
          {skills.length === 0 ? (
            <div className="text-center py-8 text-slate-400 text-sm">
              No skills added to your profile yet. Add your primary programming languages and frameworks above!
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              {skills.map((skill) => (
                <div
                  key={skill.id}
                  className="p-3.5 rounded-lg border border-slate-200 bg-white flex justify-between items-center"
                >
                  <div>
                    <span className="font-semibold text-slate-900 text-sm block">{skill.skillName}</span>
                    {editingSkillId === skill.id ? (
                      <div className="flex items-center gap-2 mt-2">
                        <select
                          value={editingSkillLevel}
                          onChange={(e) => setEditingSkillLevel(e.target.value as SkillLevel)}
                          className="text-xs px-2 py-1 border border-slate-300 rounded bg-white"
                        >
                          <option value="BEGINNER">Beginner</option>
                          <option value="INTERMEDIATE">Intermediate</option>
                          <option value="ADVANCED">Advanced</option>
                          <option value="EXPERT">Expert</option>
                        </select>
                        <button
                          onClick={() => handleUpdateSkill(skill.id)}
                          className="text-xs text-indigo-600 font-semibold hover:underline"
                        >
                          Save
                        </button>
                        <button
                          onClick={() => setEditingSkillId(null)}
                          className="text-xs text-slate-500 hover:underline"
                        >
                          Cancel
                        </button>
                      </div>
                    ) : (
                      <span className="inline-block mt-1 text-[11px] font-medium px-2 py-0.5 rounded-full bg-indigo-50 text-indigo-700 border border-indigo-100">
                        {skill.skillLevel}
                      </span>
                    )}
                  </div>

                  {editingSkillId !== skill.id && (
                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => {
                          setEditingSkillId(skill.id)
                          setEditingSkillLevel(skill.skillLevel)
                        }}
                        className="text-xs text-slate-500 hover:text-slate-800 px-2 py-1 rounded hover:bg-slate-50 transition"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => handleDeleteSkill(skill.id)}
                        className="text-xs text-red-500 hover:text-red-700 px-2 py-1 rounded hover:bg-red-50 transition"
                      >
                        Delete
                      </button>
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
