import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import { api, UserProfile, UserSkill, ApiError } from '../services/api'

export const Dashboard: React.FC = () => {
  const { user } = useAuth()
  const [profile, setProfile] = useState<UserProfile | null>(null)
  const [skills, setSkills] = useState<UserSkill[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const loadDashboardData = async () => {
      try {
        const [profileData, skillsData] = await Promise.all([
          api.getProfile().catch((err) => {
            if (err instanceof ApiError && err.status === 404) {
              return null
            }
            throw err
          }),
          api.getSkills().catch(() => []),
        ])

        setProfile(profileData)
        setSkills(skillsData)
      } catch (error) {
        console.error('Error loading dashboard data:', error)
      } finally {
        setLoading(false)
      }
    }

    loadDashboardData()
  }, [])

  const displayName =
    profile?.displayName || user?.user_metadata?.display_name || user?.email?.split('@')[0] || 'Student'

  if (loading) {
    return (
      <div className="flex min-h-[50vh] items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
      </div>
    )
  }

  return (
    <div className="space-y-8">
      {/* Welcome Header */}
      <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">
            Welcome back, <span className="text-indigo-600">{displayName}</span> 👋
          </h1>
          <p className="text-sm text-slate-500 mt-1">
            {profile?.college
              ? `${profile.course || 'Student'} at ${profile.college}`
              : 'Collaborate with teammates on student hackathons'}
          </p>
        </div>
        <Link
          to="/profile"
          className="px-4 py-2 bg-indigo-50 border border-indigo-200 text-indigo-700 hover:bg-indigo-100 rounded-lg text-sm font-medium transition"
        >
          Manage Profile & Skills
        </Link>
      </div>

      {/* Profile Overview Card */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm md:col-span-1">
          <h2 className="text-base font-bold text-slate-900 mb-3">Your Profile Summary</h2>
          {profile ? (
            <div className="space-y-3 text-sm">
              <div>
                <span className="text-xs text-slate-400 uppercase font-semibold">Experience</span>
                <p className="font-medium text-slate-700">
                  {profile.experienceLevel || 'Not specified'}
                </p>
              </div>
              <div>
                <span className="text-xs text-slate-400 uppercase font-semibold">Year of Study</span>
                <p className="font-medium text-slate-700">
                  {profile.yearOfStudy ? `Year ${profile.yearOfStudy}` : 'Not specified'}
                </p>
              </div>
              <div>
                <span className="text-xs text-slate-400 uppercase font-semibold">Skills ({skills.length})</span>
                <div className="flex flex-wrap gap-1.5 mt-1.5">
                  {skills.length > 0 ? (
                    skills.map((s) => (
                      <span
                        key={s.id}
                        className="px-2 py-0.5 bg-slate-100 text-slate-700 rounded text-xs font-medium"
                      >
                        {s.skillName}
                      </span>
                    ))
                  ) : (
                    <p className="text-xs text-slate-400">No skills added yet.</p>
                  )}
                </div>
              </div>
            </div>
          ) : (
            <div className="text-center py-4">
              <p className="text-sm text-slate-500 mb-3">You haven't completed your profile yet.</p>
              <Link
                to="/profile/setup"
                className="inline-block px-3 py-1.5 bg-indigo-600 text-white rounded-lg text-xs font-medium"
              >
                Complete Setup
              </Link>
            </div>
          )}
        </div>

        {/* Platform Status & Upcoming Phases */}
        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm md:col-span-2">
          <h2 className="text-base font-bold text-slate-900 mb-1">Collaboration Hub</h2>
          <p className="text-xs text-slate-500 mb-4">
            The foundational user & skill systems are live. Upcoming features will enable hackathon team formation:
          </p>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-sm">
            <div className="p-3.5 bg-slate-50 border border-slate-200 rounded-lg flex items-start gap-3">
              <span className="text-xl">🏆</span>
              <div>
                <div className="flex items-center gap-2">
                  <h4 className="font-semibold text-slate-800 text-sm">Hackathon Hub</h4>
                  <span className="text-[10px] uppercase font-bold bg-amber-100 text-amber-800 px-1.5 py-0.2 rounded">
                    Upcoming
                  </span>
                </div>
                <p className="text-xs text-slate-500 mt-0.5">
                  Browse, filter, and track verified hackathons.
                </p>
              </div>
            </div>

            <div className="p-3.5 bg-slate-50 border border-slate-200 rounded-lg flex items-start gap-3">
              <span className="text-xl">🚀</span>
              <div>
                <div className="flex items-center gap-2">
                  <h4 className="font-semibold text-slate-800 text-sm">Projects & Crews</h4>
                  <span className="text-[10px] uppercase font-bold bg-amber-100 text-amber-800 px-1.5 py-0.2 rounded">
                    Upcoming
                  </span>
                </div>
                <p className="text-xs text-slate-500 mt-0.5">
                  Create hackathon projects and invite teammates.
                </p>
              </div>
            </div>

            <div className="p-3.5 bg-slate-50 border border-slate-200 rounded-lg flex items-start gap-3">
              <span className="text-xl">📨</span>
              <div>
                <div className="flex items-center gap-2">
                  <h4 className="font-semibold text-slate-800 text-sm">Applications</h4>
                  <span className="text-[10px] uppercase font-bold bg-amber-100 text-amber-800 px-1.5 py-0.2 rounded">
                    Upcoming
                  </span>
                </div>
                <p className="text-xs text-slate-500 mt-0.5">
                  Apply for open roles on teams matching your skills.
                </p>
              </div>
            </div>

            <div className="p-3.5 bg-slate-50 border border-slate-200 rounded-lg flex items-start gap-3">
              <span className="text-xl">🛡️</span>
              <div>
                <div className="flex items-center gap-2">
                  <h4 className="font-semibold text-slate-800 text-sm">Contributions</h4>
                  <span className="text-[10px] uppercase font-bold bg-amber-100 text-amber-800 px-1.5 py-0.2 rounded">
                    Upcoming
                  </span>
                </div>
                <p className="text-xs text-slate-500 mt-0.5">
                  Milestone ledger generating verifiable project portfolios.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
