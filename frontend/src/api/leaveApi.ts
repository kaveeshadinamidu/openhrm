import { apiClient } from './client'
import type { LeaveRequest, LeaveRequestInput } from '../types/domain'

export async function listLeaveRequests(): Promise<LeaveRequest[]> {
  const { data } = await apiClient.get<LeaveRequest[]>('/leave-requests')
  return data
}

export async function createLeaveRequest(input: LeaveRequestInput): Promise<LeaveRequest> {
  const { data } = await apiClient.post<LeaveRequest>('/leave-requests', input)
  return data
}

export async function approveLeaveRequest(id: string): Promise<LeaveRequest> {
  const { data } = await apiClient.post<LeaveRequest>(`/leave-requests/${id}/approve`)
  return data
}

export async function rejectLeaveRequest(id: string): Promise<LeaveRequest> {
  const { data } = await apiClient.post<LeaveRequest>(`/leave-requests/${id}/reject`)
  return data
}
