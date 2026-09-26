import { beforeEach, describe, expect, it } from 'vitest'
import { useAuthStore, decodeRole, decodeEmployeeId } from './authStore'

function fakeJwt(payload: object): string {
  const header = btoa(JSON.stringify({ alg: 'none' }))
  const body = btoa(JSON.stringify(payload))
  return `${header}.${body}.signature`
}

describe('authStore', () => {
  beforeEach(() => {
    localStorage.clear()
    useAuthStore.getState().logout()
  })

  it('persists the token to localStorage on setToken', () => {
    useAuthStore.getState().setToken('abc.def.ghi')
    expect(localStorage.getItem('openhrm.accessToken')).toBe('abc.def.ghi')
  })

  it('clears the token from localStorage on logout', () => {
    useAuthStore.getState().setToken('abc.def.ghi')
    useAuthStore.getState().logout()
    expect(localStorage.getItem('openhrm.accessToken')).toBeNull()
  })

  it('decodes the role claim out of a JWT payload', () => {
    const token = fakeJwt({ role: 'HR_MANAGER' })
    expect(decodeRole(token)).toBe('HR_MANAGER')
  })

  it('returns null for a malformed token instead of throwing', () => {
    expect(decodeRole('not-a-jwt')).toBeNull()
  })

  it('decodes the employeeId claim out of a JWT payload', () => {
    const token = fakeJwt({ employeeId: 'e1234' })
    expect(decodeEmployeeId(token)).toBe('e1234')
  })
})
