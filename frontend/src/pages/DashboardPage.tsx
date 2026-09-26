import { useQuery } from '@tanstack/react-query'
import { listEmployees } from '../api/employeeApi'
import { listLeaveRequests } from '../api/leaveApi'

export function DashboardPage() {
  const employeesQuery = useQuery({ queryKey: ['employees'], queryFn: listEmployees })
  const leaveQuery = useQuery({ queryKey: ['leave-requests'], queryFn: listLeaveRequests })

  const pendingCount = leaveQuery.data?.filter((request) => request.status === 'PENDING').length ?? 0
  const activeCount = employeesQuery.data?.filter((employee) => employee.employmentStatus === 'ACTIVE').length ?? 0

  return (
    <div>
      <h1 className="mb-6 text-2xl font-semibold">Dashboard</h1>
      <div className="grid grid-cols-2 gap-4 sm:grid-cols-3">
        <StatCard label="Total employees" value={employeesQuery.data?.length} />
        <StatCard label="Active employees" value={activeCount} />
        <StatCard label="Pending leave requests" value={pendingCount} />
      </div>
    </div>
  )
}

function StatCard({ label, value }: { label: string; value: number | undefined }) {
  return (
    <div className="rounded-lg border border-slate-200 bg-white p-5">
      <p className="text-sm text-slate-500">{label}</p>
      <p className="mt-1 text-3xl font-semibold">{value ?? '—'}</p>
    </div>
  )
}
