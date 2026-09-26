import { apiClient } from './client'

export interface AuthResponse {
  accessToken: string
  tokenType: string
}

export interface LoginPayload {
  email: string
  password: string
}

export interface RegisterOrganizationPayload {
  organizationName: string
  adminFirstName: string
  adminLastName: string
  adminEmail: string
  adminPassword: string
}

export async function login(payload: LoginPayload): Promise<AuthResponse> {
  const { data } = await apiClient.post<AuthResponse>('/auth/login', payload)
  return data
}

export async function registerOrganization(payload: RegisterOrganizationPayload): Promise<AuthResponse> {
  const { data } = await apiClient.post<AuthResponse>('/auth/register-organization', payload)
  return data
}
