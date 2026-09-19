import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from './auth/AuthContext'
import { Layout } from './components/Layout'
import { ProtectedRoute } from './components/ProtectedRoute'
import { Home } from './routes/Home'
import { Login } from './routes/Login'
import { Signup } from './routes/Signup'
import { Dashboard } from './routes/Dashboard'
import { Profile } from './routes/Profile'
import { ProfileSetup } from './routes/ProfileSetup'

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Layout>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/signup" element={<Signup />} />
            <Route
              path="/profile/setup"
              element={
                <ProtectedRoute>
                  <ProfileSetup />
                </ProtectedRoute>
              }
            />
            <Route
              path="/dashboard"
              element={
                <ProtectedRoute>
                  <Dashboard />
                </ProtectedRoute>
              }
            />
            <Route
              path="/profile"
              element={
                <ProtectedRoute>
                  <ProfileSetupCheck>
                    <Profile />
                  </ProfileSetupCheck>
                </ProtectedRoute>
              }
            />
          </Routes>
        </Layout>
      </AuthProvider>
    </BrowserRouter>
  )
}

// Simple wrapper that allows direct access to Profile
function ProfileSetupCheck({ children }: { children: React.ReactNode }) {
  return <>{children}</>
}

export default App
