import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useNavigate, Link } from 'react-router-dom'
import { useMutation } from '@tanstack/react-query'
import { registerOrganization } from '../api/authApi'
import { useAuthStore } from '../auth/authStore'

const schema = z.object({
  organizationName: z.string().min(1, 'Required'),
  adminFirstName: z.string().min(1, 'Required'),
  adminLastName: z.string().min(1, 'Required'),
  adminEmail: z.string().email(),
  adminPassword: z.string().min(8, 'At least 8 characters'),
})

type FormValues = z.infer<typeof schema>

export function RegisterOrganizationPage() {
  const navigate = useNavigate()
  const setToken = useAuthStore((state) => state.setToken)
  const { register, handleSubmit, formState } = useForm<FormValues>({ resolver: zodResolver(schema) })

  const mutation = useMutation({
    mutationFn: registerOrganization,
    onSuccess: (data) => {
      setToken(data.accessToken)
      navigate('/')
    },
  })

  return (
    <div className="mx-auto mt-16 max-w-md rounded-lg border border-slate-200 bg-white p-8 shadow-sm">
      <h1 className="mb-6 text-xl font-semibold">Set up your company</h1>
      <form onSubmit={handleSubmit((values) => mutation.mutate(values))} className="space-y-4">
        <Field label="Company name" error={formState.errors.organizationName?.message}>
          <input {...register('organizationName')} className="w-full rounded border border-slate-300 px-3 py-2" />
        </Field>
        <div className="grid grid-cols-2 gap-3">
          <Field label="Your first name" error={formState.errors.adminFirstName?.message}>
            <input {...register('adminFirstName')} className="w-full rounded border border-slate-300 px-3 py-2" />
          </Field>
          <Field label="Your last name" error={formState.errors.adminLastName?.message}>
            <input {...register('adminLastName')} className="w-full rounded border border-slate-300 px-3 py-2" />
          </Field>
        </div>
        <Field label="Your email" error={formState.errors.adminEmail?.message}>
          <input {...register('adminEmail')} type="email" className="w-full rounded border border-slate-300 px-3 py-2" />
        </Field>
        <Field label="Password" error={formState.errors.adminPassword?.message}>
          <input {...register('adminPassword')} type="password" className="w-full rounded border border-slate-300 px-3 py-2" />
        </Field>
        {mutation.isError && <p className="text-sm text-red-600">Could not create your account. Try a different email.</p>}
        <button
          type="submit"
          disabled={mutation.isPending}
          className="w-full rounded bg-indigo-600 py-2 text-white hover:bg-indigo-500 disabled:opacity-50"
        >
          {mutation.isPending ? 'Creating...' : 'Create company'}
        </button>
      </form>
      <p className="mt-4 text-sm text-slate-500">
        Already have an account? <Link to="/login" className="text-indigo-600">Log in</Link>
      </p>
    </div>
  )
}

function Field({ label, error, children }: { label: string; error?: string; children: React.ReactNode }) {
  return (
    <div>
      <label className="mb-1 block text-sm font-medium">{label}</label>
      {children}
      {error && <p className="mt-1 text-sm text-red-600">{error}</p>}
    </div>
  )
}
