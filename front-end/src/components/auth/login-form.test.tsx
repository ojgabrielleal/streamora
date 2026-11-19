import { renderToStaticMarkup } from 'react-dom/server'
import type { FormEvent } from 'react'
import { beforeEach, describe, expect, it, vi } from 'vitest'

import { LoginForm } from '@/components/auth/login-form'
import { submitLogin } from '@/components/auth/login-submit'
import { HomePage } from '@/routes/index'

const mutationState = vi.hoisted(() => ({
  isError: false,
  isPending: false,
  isSuccess: false,
  mutate: vi.fn(),
  reset: vi.fn(),
}))

const navigate = vi.hoisted(() => vi.fn())

vi.mock('@tanstack/react-query', async (importOriginal) => ({
  ...(await importOriginal<typeof import('@tanstack/react-query')>()),
  useMutation: () => mutationState,
}))

vi.mock('@tanstack/react-router', () => ({
  useNavigate: () => navigate,
}))

describe('LoginForm', () => {
  beforeEach(() => {
    mutationState.isError = false
    mutationState.isPending = false
    mutationState.isSuccess = false
    mutationState.mutate.mockReset()
    mutationState.reset.mockReset()
    navigate.mockReset()
  })

  it('renders the brand and accessible authentication fields', () => {
    const markup = renderToStaticMarkup(<HomePage />)

    expect(markup).toContain('/images/brand/vertical-logo.png')
    expect(markup).toContain('alt="Streamora"')
    expect(markup).toContain('for="username"')
    expect(markup).toContain('autoComplete="username"')
    expect(markup).toContain('for="password"')
    expect(markup).toContain('autoComplete="current-password"')
    expect(markup).toContain('type="password"')
    expect(markup).toContain('required=""')
    expect(markup).toContain('Entrar')
  })

  it('submits the informed credentials through the mutation', () => {
    const event = { preventDefault: vi.fn() } as unknown as FormEvent<HTMLFormElement>
    const credentials = { password: 'secret', username: 'streamora' }

    submitLogin(event, credentials, mutationState)

    expect(event.preventDefault).toHaveBeenCalledOnce()
    expect(mutationState.reset).toHaveBeenCalledOnce()
    expect(mutationState.mutate).toHaveBeenCalledWith(credentials)
  })

  it('announces loading and prevents another submission while pending', () => {
    mutationState.isPending = true
    const markup = renderToStaticMarkup(<LoginForm />)
    const event = { preventDefault: vi.fn() } as unknown as FormEvent<HTMLFormElement>

    submitLogin(event, { password: 'secret', username: 'streamora' }, mutationState)

    expect(markup).toContain('aria-busy="true"')
    expect(markup).toContain('disabled=""')
    expect(markup).toContain('Entrando...')
    expect(markup).toContain('Autenticação em andamento.')
    expect(mutationState.mutate).not.toHaveBeenCalled()
  })

  it('shows a safe and accessible error message', () => {
    mutationState.isError = true

    const markup = renderToStaticMarkup(<LoginForm />)

    expect(markup).toContain('role="alert"')
    expect(markup).toContain(
      'Não foi possível entrar. Verifique suas credenciais e tente novamente.',
    )
  })

  it('requests navigation to the dashboard after a successful login', () => {
    mutationState.mutate.mockImplementation((_credentials, options) => {
      options?.onSuccess()
    })
    const event = { preventDefault: vi.fn() } as unknown as FormEvent<HTMLFormElement>

    submitLogin(
      event,
      { password: 'secret', username: 'streamora' },
      mutationState,
      () => navigate({ to: '/dashboard' }),
    )

    expect(navigate).toHaveBeenCalledWith({ to: '/dashboard' })
  })
})
