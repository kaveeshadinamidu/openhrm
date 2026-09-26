import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { createEmployee, listEmployees, offboardEmployee } from '../api/employeeApi'

const schema = z.object({
  firstName: z.string().min(1, 'Required'),
  lastName: z.string().min(1, 'Required'),
  email: z.string().email(),
  jobTitle: z.string().optional(),
  department: z.string().optional(),
})

type FormValues = z.infer<typeof schema>

export function EmployeesPage() {
  const [showForm, setShowForm] = useState(false)
  const queryClient = useQueryClient()
  const employeesQuery = useQuery({ queryKey: ['employees'], queryFn: listEmployees })

  const { register, handleSubmit, reset, formState } = useForm<FormValues>({ resolver: zodResolver(schema) })

  const createMutation = useMutation({
    mutationFn: createEmployee,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['employees'] })
      reset()
      setShowForm(false)
    },
  })

  const offboardMutation = useMutation({
    mutationFn: offboardEmployee,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['employees'] }),
  })

  return (
    <div>
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-2xl font-semibold">Employees</h1>
        <button
          onClick={() => setShowForm((value) => !value)}
          className="rounded bg-indigo-600 px-4 py-2 text-sm text-white hover:bg-indigo-500"
        >
          {showForm ? 'Cancel' : 'Add employee'}
        </button>
      </div>

      {showForm && (
        <form
          onSubmit={handleSubmit((values) => createMutation.mutate(values))}
          className="mb-8 grid grid-cols-2 gap-4 rounded-lg border border-slate-200 bg-white p-6"
        >
          <input {...register('firstName')} placeholder="First name" className="rounded border border-slate-300 px-3 py-2" />
          <input {...register('lastName')} placeholder="Last name" className="rounded border border-slate-300 px-3 py-2" />
          <input {...register('email')} placeholder="Email" className="col-span-2 rounded border border-slate-300 px-3 py-2" />
          <input {...register('jobTitle')} placeholder="Job title" className="rounded border border-slate-300 px-3 py-2" />
          <input {...register('department')} placeholder="Department" className="rounded border border-slate-300 px-3 py-2" />
          {formState.errors.email && <p className="col-span-2 text-sm text-red-600">{formState.errors.email.message}</p>}
          <button
            type="submit"
            disabled={createMutation.isPending}
            className="col-span-2 rounded bg-indigo-600 py-2 text-white hover:bg-indigo-500 disabled:opacity-50"
          >
            {createMutation.isPending ? 'Saving...' : 'Save employee'}
          </button>
        </form>
      )}

      <div className="overflow-hidden rounded-lg border border-slate-200 bg-white">
        <table className="w-full text-left text-sm">
          <thead className="bg-slate-100 text-xs uppercase text-slate-500">
            <tr>
              <th className="px-4 py-3">Name</th>
              <th className="px-4 py-3">Email</th>
              <th className="px-4 py-3">Title</th>
              <th className="px-4 py-3">Status</th>
              <th className="px-4 py-3" />
            </tr>
          </thead>
          <tbody>
            {employeesQuery.data?.map((employee) => (
              <tr key={employee.id} className="border-t border-slate-100">
                <td className="px-4 py-3">{employee.firstName} {employee.lastName}</td>
                <td className="px-4 py-3">{employee.email}</td>
                <td className="px-4 py-3">{employee.jobTitle ?? '—'}</td>
                <td className="px-4 py-3">
                  <StatusBadge status={employee.employmentStatus} />
                </td>
                <td className="px-4 py-3 text-right">
                  {employee.employmentStatus !== 'OFFBOARDED' && (
                    <button
                      onClick={() => offboardMutation.mutate(employee.id)}
                      className="text-sm text-red-600 hover:underline"
                    >
                      Offboard
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

function StatusBadge({ status }: { status: string }) {
  const colors: Record<string, string> = {
    ACTIVE: 'bg-green-100 text-green-700',
    ONBOARDING: 'bg-amber-100 text-amber-700',
    OFFBOARDED: 'bg-slate-200 text-slate-600',
  }
  return <span className={`rounded-full px-2 py-1 text-xs font-medium ${colors[status]}`}>{status}</span>
}
