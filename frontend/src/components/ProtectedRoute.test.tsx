import { describe, expect, it, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { ProtectedRoute } from './ProtectedRoute'
import { useAuthStore } from '../auth/authStore'

function renderWithGuard() {
  return render(
    <MemoryRouter initialEntries={['/employees']}>
      <Routes>
        <Route path="/login" element={<div>Login page</div>} />
        <Route element={<ProtectedRoute />}>
          <Route path="/employees" element={<div>Employees page</div>} />
        </Route>
      </Routes>
    </MemoryRouter>,
  )
}

describe('ProtectedRoute', () => {
  beforeEach(() => {
    useAuthStore.getState().logout()
  })

  it('redirects to /login when there is no token', () => {
    renderWithGuard()
    expect(screen.getByText('Login page')).toBeInTheDocument()
  })

  it('renders the protected content when a token is present', () => {
    useAuthStore.getState().setToken('some.jwt.token')
    renderWithGuard()
    expect(screen.getByText('Employees page')).toBeInTheDocument()
  })
})
