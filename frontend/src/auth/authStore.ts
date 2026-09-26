import { create } from 'zustand'

const TOKEN_STORAGE_KEY = 'openhrm.accessToken'

interface AuthState {
  token: string | null
  setToken: (token: string | null) => void
  logout: () => void
}

// Kept minimal on purpose: the token is the only real client-side auth state, everything
// else (role, employeeId) is decoded from it on demand where needed.
export const useAuthStore = create<AuthState>((set) => ({
  token: localStorage.getItem(TOKEN_STORAGE_KEY),
  setToken: (token) => {
    if (token) {
      localStorage.setItem(TOKEN_STORAGE_KEY, token)
    } else {
      localStorage.removeItem(TOKEN_STORAGE_KEY)
    }
    set({ token })
  },
  logout: () => {
    localStorage.removeItem(TOKEN_STORAGE_KEY)
    set({ token: null })
  },
}))

function decodeClaims(token: string | null): Record<string, unknown> | null {
  if (!token) return null
  try {
    return JSON.parse(atob(token.split('.')[1]))
  } catch {
    return null
  }
}

export function decodeRole(token: string | null): string | null {
  return (decodeClaims(token)?.role as string) ?? null
}

export function decodeEmployeeId(token: string | null): string | null {
  return (decodeClaims(token)?.employeeId as string) ?? null
}
