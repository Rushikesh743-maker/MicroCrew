import React from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

export const Home: React.FC = () => {
  const { user } = useAuth()

  return (
    <div className="py-12 sm:py-16">
      <div className="text-center max-w-3xl mx-auto">
        <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-indigo-50 text-indigo-700 border border-indigo-200 mb-6">
          Student Collaboration Platform
        </span>
        <h1 className="text-4xl sm:text-5xl font-extrabold tracking-tight text-slate-900 leading-tight">
          Find Your Crew. Build Projects.{' '}
          <span className="text-indigo-600">Win Hackathons.</span>
        </h1>
        <p className="mt-6 text-lg text-slate-600 leading-relaxed max-w-2xl mx-auto">
          A collaboration platform for students to discover hackathons, build teams, collaborate,
          and showcase verified contributions.
        </p>

        <div className="mt-8 flex justify-center gap-4">
          {user ? (
            <Link
              to="/dashboard"
              className="inline-flex items-center justify-center px-6 py-3 rounded-lg bg-indigo-600 text-white font-medium hover:bg-indigo-700 shadow transition"
            >
              Go to Dashboard
            </Link>
          ) : (
            <>
              <Link
                to="/signup"
                className="inline-flex items-center justify-center px-6 py-3 rounded-lg bg-indigo-600 text-white font-medium hover:bg-indigo-700 shadow transition"
              >
                Get Started
              </Link>
              <Link
                to="/login"
                className="inline-flex items-center justify-center px-6 py-3 rounded-lg bg-white border border-slate-300 text-slate-700 font-medium hover:bg-slate-50 transition"
              >
                Log In
              </Link>
            </>
          )}
        </div>
      </div>

      {/* Feature Pillars */}
      <div className="mt-20 grid grid-cols-1 md:grid-cols-3 gap-8 max-w-6xl mx-auto">
        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
          <div className="w-10 h-10 rounded-lg bg-indigo-100 flex items-center justify-center text-indigo-700 font-bold mb-4">
            🎯
          </div>
          <h3 className="text-lg font-bold text-slate-900 mb-2">Hackathon Hub</h3>
          <p className="text-sm text-slate-600 leading-relaxed">
            Discover student hackathons, track deadlines, and organize participation with your
            classmates and teammates.
          </p>
        </div>

        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
          <div className="w-10 h-10 rounded-lg bg-indigo-100 flex items-center justify-center text-indigo-700 font-bold mb-4">
            👥
          </div>
          <h3 className="text-lg font-bold text-slate-900 mb-2">Skill-Based Team Matching</h3>
          <p className="text-sm text-slate-600 leading-relaxed">
            Form balanced crews based on verified skills, availability, and specific project
            requirements without endless Discord searches.
          </p>
        </div>

        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
          <div className="w-10 h-10 rounded-lg bg-indigo-100 flex items-center justify-center text-indigo-700 font-bold mb-4">
            📜
          </div>
          <h3 className="text-lg font-bold text-slate-900 mb-2">Verified Contributions</h3>
          <p className="text-sm text-slate-600 leading-relaxed">
            Track milestones and tasks directly into evidence-based portfolios proving real
            hands-on software development work.
          </p>
        </div>
      </div>
    </div>
  )
}
