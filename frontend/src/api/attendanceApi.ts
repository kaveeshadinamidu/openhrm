import { apiClient } from './client'
import type { AttendanceEntry } from '../types/domain'

export async function listAttendance(): Promise<AttendanceEntry[]> {
  const { data } = await apiClient.get<AttendanceEntry[]>('/attendance')
  return data
}

export async function clockIn(): Promise<AttendanceEntry> {
  const { data } = await apiClient.post<AttendanceEntry>('/attendance/clock-in')
  return data
}

export async function clockOut(): Promise<AttendanceEntry> {
  const { data } = await apiClient.post<AttendanceEntry>('/attendance/clock-out')
  return data
}

export async function approveAttendance(id: string): Promise<AttendanceEntry> {
  const { data } = await apiClient.post<AttendanceEntry>(`/attendance/${id}/approve`)
  return data
}
