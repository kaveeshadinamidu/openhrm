import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useNavigate, Link } from 'react-router-dom'
import { useMutation } from '@tanstack/react-query'
import { login } from '../api/authApi'
import { useAuthStore } from '../auth/authStore'

const schema = z.object({
  email: z.string().email(),
  password: z.string().min(1, 'Password is required'),
})

type FormValues = z.infer<typeof schema>

export function LoginPage() {
  const navigate = useNavigate()
  const setToken = useAuthStore((state) => state.setToken)
  const { register, handleSubmit, formState } = useForm<FormValues>({ resolver: zodResolver(schema) })

  const mutation = useMutation({
    mutationFn: login,
    onSuccess: (data) => {
      setToken(data.accessToken)
      navigate('/')
    },
  })

  return (
    <div className="mx-auto mt-16 max-w-sm rounded-lg border border-slate-200 bg-white p-8 shadow-sm">
      <h1 className="mb-6 text-xl font-semibold">Log in to OpenHRM</h1>
      <form onSubmit={handleSubmit((values) => mutation.mutate(values))} className="space-y-4">
        <div>
          <label className="mb-1 block text-sm font-medium">Email</label>
          <input {...register('email')} type="email" className="w-full rounded border border-slate-300 px-3 py-2" />
          {formState.errors.email && <p className="mt-1 text-sm text-red-600">{formState.errors.email.message}</p>}
        </div>
        <div>
          <label className="mb-1 block text-sm font-medium">Password</label>
          <input {...register('password')} type="password" className="w-full rounded border border-slate-300 px-3 py-2" />
        </div>
        {mutation.isError && <p className="text-sm text-red-600">Invalid email or password</p>}
        <button
          type="submit"
          disabled={mutation.isPending}
          className="w-full rounded bg-indigo-600 py-2 text-white hover:bg-indigo-500 disabled:opacity-50"
        >
          {mutation.isPending ? 'Logging in...' : 'Log in'}
        </button>
      </form>
      <p className="mt-4 text-sm text-slate-500">
        No account yet? <Link to="/register" className="text-indigo-600">Register your company</Link>
      </p>
    </div>
  )
}
