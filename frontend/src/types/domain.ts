export type Role = 'ADMIN' | 'HR_MANAGER' | 'MANAGER' | 'EMPLOYEE'

export type EmploymentStatus = 'ONBOARDING' | 'ACTIVE' | 'OFFBOARDED'

export interface Employee {
  id: string
  firstName: string
  lastName: string
  email: string
  jobTitle: string | null
  department: string | null
  managerId: string | null
  employmentStatus: EmploymentStatus
  hireDate: string | null
}

export interface EmployeeInput {
  firstName: string
  lastName: string
  email: string
  jobTitle?: string
  department?: string
  managerId?: string | null
  hireDate?: string | null
}

export type LeaveType = 'ANNUAL' | 'SICK' | 'UNPAID' | 'OTHER'
export type LeaveStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED'

export interface LeaveRequest {
  id: string
  employeeId: string
  leaveType: LeaveType
  startDate: string
  endDate: string
  status: LeaveStatus
  reason: string | null
  approverId: string | null
}

export interface LeaveRequestInput {
  employeeId: string
  leaveType: LeaveType
  startDate: string
  endDate: string
  reason?: string
}
