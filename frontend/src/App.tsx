import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { ProtectedRoute } from './components/ProtectedRoute'
import { AppLayout } from './components/AppLayout'
import { LoginPage } from './pages/LoginPage'
import { RegisterOrganizationPage } from './pages/RegisterOrganizationPage'
import { DashboardPage } from './pages/DashboardPage'
import { EmployeesPage } from './pages/EmployeesPage'
import { LeaveRequestsPage } from './pages/LeaveRequestsPage'
import { AttendancePage } from './pages/AttendancePage'

export function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterOrganizationPage />} />
        <Route element={<ProtectedRoute />}>
          <Route element={<AppLayout />}>
            <Route path="/" element={<DashboardPage />} />
            <Route path="/employees" element={<EmployeesPage />} />
            <Route path="/leave-requests" element={<LeaveRequestsPage />} />
            <Route path="/attendance" element={<AttendancePage />} />
          </Route>
        </Route>
      </Routes>
    </BrowserRouter>
  )
}
