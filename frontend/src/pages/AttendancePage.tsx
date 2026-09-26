import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { approveAttendance, clockIn, clockOut, listAttendance } from '../api/attendanceApi'
import { listEmployees } from '../api/employeeApi'
import { useAuthStore, decodeRole, decodeEmployeeId } from '../auth/authStore'

const canDecide = (role: string | null) => role === 'ADMIN' || role === 'HR_MANAGER' || role === 'MANAGER'
const todayIso = () => new Date().toISOString().slice(0, 10)

export function AttendancePage() {
  const queryClient = useQueryClient()
  const token = useAuthStore((state) => state.token)
  const role = decodeRole(token)
  const employeeId = decodeEmployeeId(token)

  const attendanceQuery = useQuery({ queryKey: ['attendance'], queryFn: listAttendance })
  const employeesQuery = useQuery({ queryKey: ['employees'], queryFn: listEmployees })

  const clockInMutation = useMutation({
    mutationFn: clockIn,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['attendance'] }),
  })

  const clockOutMutation = useMutation({
    mutationFn: clockOut,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['attendance'] }),
  })

  const approveMutation = useMutation({
    mutationFn: approveAttendance,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['attendance'] }),
  })

  const todaysEntry = attendanceQuery.data?.find(
    (entry) => entry.employeeId === employeeId && entry.workDate === todayIso(),
  )

  const employeeName = (id: string) => {
    const employee = employeesQuery.data?.find((candidate) => candidate.id === id)
    return employee ? `${employee.firstName} ${employee.lastName}` : id
  }

  return (
    <div>
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-2xl font-semibold">Attendance</h1>
        {!todaysEntry && (
          <button
            onClick={() => clockInMutation.mutate()}
            disabled={clockInMutation.isPending}
            className="rounded bg-indigo-600 px-4 py-2 text-sm text-white hover:bg-indigo-500 disabled:opacity-50"
          >
            Clock in
          </button>
        )}
        {todaysEntry && !todaysEntry.clockOut && (
          <button
            onClick={() => clockOutMutation.mutate()}
            disabled={clockOutMutation.isPending}
            className="rounded bg-slate-800 px-4 py-2 text-sm text-white hover:bg-slate-700 disabled:opacity-50"
          >
            Clock out
          </button>
        )}
        {todaysEntry?.clockOut && <span className="text-sm text-slate-500">Clocked out for today</span>}
      </div>

      <div className="overflow-hidden rounded-lg border border-slate-200 bg-white">
        <table className="w-full text-left text-sm">
          <thead className="bg-slate-100 text-xs uppercase text-slate-500">
            <tr>
              <th className="px-4 py-3">Employee</th>
              <th className="px-4 py-3">Date</th>
              <th className="px-4 py-3">Clock in</th>
              <th className="px-4 py-3">Clock out</th>
              <th className="px-4 py-3">Minutes</th>
              <th className="px-4 py-3">Status</th>
              <th className="px-4 py-3" />
            </tr>
          </thead>
          <tbody>
            {attendanceQuery.data?.map((entry) => (
              <tr key={entry.id} className="border-t border-slate-100">
                <td className="px-4 py-3">{employeeName(entry.employeeId)}</td>
                <td className="px-4 py-3">{entry.workDate}</td>
                <td className="px-4 py-3">{new Date(entry.clockIn).toLocaleTimeString()}</td>
                <td className="px-4 py-3">{entry.clockOut ? new Date(entry.clockOut).toLocaleTimeString() : '—'}</td>
                <td className="px-4 py-3">{entry.minutesWorked ?? '—'}</td>
                <td className="px-4 py-3">{entry.status}</td>
                <td className="px-4 py-3 text-right">
                  {entry.status === 'PENDING' && canDecide(role) && (
                    <button onClick={() => approveMutation.mutate(entry.id)} className="text-sm text-green-700 hover:underline">
                      Approve
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
