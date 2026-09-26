import { apiClient } from './client'
import type { Employee, EmployeeInput } from '../types/domain'

export async function listEmployees(): Promise<Employee[]> {
  const { data } = await apiClient.get<Employee[]>('/employees')
  return data
}

export async function getEmployee(id: string): Promise<Employee> {
  const { data } = await apiClient.get<Employee>(`/employees/${id}`)
  return data
}

export async function createEmployee(input: EmployeeInput): Promise<Employee> {
  const { data } = await apiClient.post<Employee>('/employees', input)
  return data
}

export async function offboardEmployee(id: string): Promise<void> {
  await apiClient.delete(`/employees/${id}`)
}
