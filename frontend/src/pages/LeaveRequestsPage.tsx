import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { approveLeaveRequest, createLeaveRequest, listLeaveRequests, rejectLeaveRequest } from '../api/leaveApi'
import { listEmployees } from '../api/employeeApi'
import { useAuthStore, decodeRole } from '../auth/authStore'

const schema = z.object({
  employeeId: z.string().min(1, 'Required'),
  leaveType: z.enum(['ANNUAL', 'SICK', 'UNPAID', 'OTHER']),
  startDate: z.string().min(1, 'Required'),
  endDate: z.string().min(1, 'Required'),
  reason: z.string().optional(),
})

type FormValues = z.infer<typeof schema>

const canDecide = (role: string | null) => role === 'ADMIN' || role === 'HR_MANAGER' || role === 'MANAGER'

export function LeaveRequestsPage() {
  const [showForm, setShowForm] = useState(false)
  const queryClient = useQueryClient()
  const role = decodeRole(useAuthStore((state) => state.token))

  const leaveQuery = useQuery({ queryKey: ['leave-requests'], queryFn: listLeaveRequests })
  const employeesQuery = useQuery({ queryKey: ['employees'], queryFn: listEmployees })

  const { register, handleSubmit, reset, formState } = useForm<FormValues>({ resolver: zodResolver(schema) })

  const createMutation = useMutation({
    mutationFn: createLeaveRequest,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['leave-requests'] })
      reset()
      setShowForm(false)
    },
  })

  const approveMutation = useMutation({
    mutationFn: approveLeaveRequest,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['leave-requests'] }),
  })

  const rejectMutation = useMutation({
    mutationFn: rejectLeaveRequest,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['leave-requests'] }),
  })

  const employeeName = (id: string) => {
    const employee = employeesQuery.data?.find((candidate) => candidate.id === id)
    return employee ? `${employee.firstName} ${employee.lastName}` : id
  }

  return (
    <div>
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-2xl font-semibold">Leave Requests</h1>
        <button
          onClick={() => setShowForm((value) => !value)}
          className="rounded bg-indigo-600 px-4 py-2 text-sm text-white hover:bg-indigo-500"
        >
          {showForm ? 'Cancel' : 'Request leave'}
        </button>
      </div>

      {showForm && (
        <form
          onSubmit={handleSubmit((values) => createMutation.mutate(values))}
          className="mb-8 grid grid-cols-2 gap-4 rounded-lg border border-slate-200 bg-white p-6"
        >
          <select {...register('employeeId')} className="rounded border border-slate-300 px-3 py-2">
            <option value="">Select employee</option>
            {employeesQuery.data?.map((employee) => (
              <option key={employee.id} value={employee.id}>{employee.firstName} {employee.lastName}</option>
            ))}
          </select>
          <select {...register('leaveType')} className="rounded border border-slate-300 px-3 py-2">
            <option value="ANNUAL">Annual</option>
            <option value="SICK">Sick</option>
            <option value="UNPAID">Unpaid</option>
            <option value="OTHER">Other</option>
          </select>
          <input {...register('startDate')} type="date" className="rounded border border-slate-300 px-3 py-2" />
          <input {...register('endDate')} type="date" className="rounded border border-slate-300 px-3 py-2" />
          <input {...register('reason')} placeholder="Reason (optional)" className="col-span-2 rounded border border-slate-300 px-3 py-2" />
          {formState.errors.employeeId && <p className="col-span-2 text-sm text-red-600">{formState.errors.employeeId.message}</p>}
          <button
            type="submit"
            disabled={createMutation.isPending}
            className="col-span-2 rounded bg-indigo-600 py-2 text-white hover:bg-indigo-500 disabled:opacity-50"
          >
            {createMutation.isPending ? 'Submitting...' : 'Submit request'}
          </button>
        </form>
      )}

      <div className="overflow-hidden rounded-lg border border-slate-200 bg-white">
        <table className="w-full text-left text-sm">
          <thead className="bg-slate-100 text-xs uppercase text-slate-500">
            <tr>
              <th className="px-4 py-3">Employee</th>
              <th className="px-4 py-3">Type</th>
              <th className="px-4 py-3">Dates</th>
              <th className="px-4 py-3">Status</th>
              <th className="px-4 py-3" />
            </tr>
          </thead>
          <tbody>
            {leaveQuery.data?.map((request) => (
              <tr key={request.id} className="border-t border-slate-100">
                <td className="px-4 py-3">{employeeName(request.employeeId)}</td>
                <td className="px-4 py-3">{request.leaveType}</td>
                <td className="px-4 py-3">{request.startDate} → {request.endDate}</td>
                <td className="px-4 py-3">{request.status}</td>
                <td className="px-4 py-3 text-right">
                  {request.status === 'PENDING' && canDecide(role) && (
                    <div className="flex justify-end gap-3">
                      <button onClick={() => approveMutation.mutate(request.id)} className="text-sm text-green-700 hover:underline">
                        Approve
                      </button>
                      <button onClick={() => rejectMutation.mutate(request.id)} className="text-sm text-red-600 hover:underline">
                        Reject
                      </button>
                    </div>
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
