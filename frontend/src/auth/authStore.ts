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

export function decodeRole(token: string | null): string | null {
  if (!token) return null
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    return payload.role ?? null
  } catch {
    return null
  }
}
