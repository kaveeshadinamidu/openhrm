import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuthStore } from '../auth/authStore'

const navItems = [
  { to: '/', label: 'Dashboard' },
  { to: '/employees', label: 'Employees' },
  { to: '/leave-requests', label: 'Leave Requests' },
  { to: '/attendance', label: 'Attendance' },
]

export function AppLayout() {
  const logout = useAuthStore((state) => state.logout)
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <div className="min-h-screen">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-5xl items-center justify-between px-4 py-3">
          <span className="text-lg font-semibold">OpenHRM</span>
          <nav className="flex gap-4">
            {navItems.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.to === '/'}
                className={({ isActive }) =>
                  `text-sm font-medium ${isActive ? 'text-indigo-600' : 'text-slate-600 hover:text-slate-900'}`
                }
              >
                {item.label}
              </NavLink>
            ))}
          </nav>
          <button onClick={handleLogout} className="text-sm text-slate-500 hover:text-slate-900">
            Log out
          </button>
        </div>
      </header>
      <main className="mx-auto max-w-5xl px-4 py-8">
        <Outlet />
      </main>
    </div>
  )
}
