import { useState, type FormEvent } from 'react'
import { Pencil, Plus, RefreshCw, Trash2, Users } from 'lucide-react'

import type { UserRequest, UserResponse } from '@/api/users/types'
import {
  useCreateUser,
  useDeleteUser,
  useUpdateUser,
} from '@/api/users/use-users'

type UsersBoardProps = {
  isError: boolean
  isPending: boolean
  onRetry: () => void
  users?: UserResponse[]
}

type FormState = {
  fullname: string
  nickname: string
  username: string
  password: string
}

const emptyForm: FormState = {
  fullname: '',
  nickname: '',
  username: '',
  password: '',
}

export function UsersBoard({
  isError,
  isPending,
  onRetry,
  users = [],
}: UsersBoardProps) {
  const createUserMutation = useCreateUser()
  const updateUserMutation = useUpdateUser()
  const deleteUserMutation = useDeleteUser()

  const [isFormOpen, setIsFormOpen] = useState(false)
  const [editingUser, setEditingUser] = useState<UserResponse | null>(null)
  const [deletingUser, setDeletingUser] = useState<UserResponse | null>(null)
  const [form, setForm] = useState<FormState>(emptyForm)
  const [feedback, setFeedback] = useState<{
    type: 'success' | 'error'
    message: string
  } | null>(null)

  const isSubmitting =
    createUserMutation.isPending || updateUserMutation.isPending

  function openCreateModal() {
    setEditingUser(null)
    setForm(emptyForm)
    setFeedback(null)
    setIsFormOpen(true)
  }

  function openEditModal(user: UserResponse) {
    setEditingUser(user)
    setForm({
      fullname: user.fullname,
      nickname: user.nickname,
      username: user.username,
      password: '',
    })
    setFeedback(null)
    setIsFormOpen(true)
  }

  function closeFormModal() {
    setIsFormOpen(false)
    setEditingUser(null)
    setForm(emptyForm)
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setFeedback(null)

    const fullname = form.fullname.trim()
    const nickname = form.nickname.trim()
    const username = form.username.trim()
    const password = form.password.trim()

    if (!fullname || !nickname) {
      setFeedback({
        type: 'error',
        message: 'Preencha nome completo e nome de exibição.',
      })
      return
    }

    if (!editingUser && (!username || !password)) {
      setFeedback({
        type: 'error',
        message: 'Preencha usuário e senha para criar um novo usuário.',
      })
      return
    }

    try {
      if (editingUser) {
        const payload: UserRequest = {
          fullname,
          nickname,
          ...(password ? { password } : {}),
        }
        await updateUserMutation.mutateAsync({
          uuid: editingUser.uuid,
          payload,
        })
        setFeedback({
          type: 'success',
          message: 'Usuário atualizado com sucesso.',
        })
      } else {
        await createUserMutation.mutateAsync({
          fullname,
          nickname,
          username,
          password,
        })
        setFeedback({
          type: 'success',
          message: 'Usuário criado com sucesso.',
        })
      }
      closeFormModal()
    } catch (error) {
      setFeedback({
        type: 'error',
        message:
          error instanceof Error
            ? error.message
            : 'Não foi possível salvar o usuário.',
      })
    }
  }

  async function handleConfirmDelete() {
    if (!deletingUser) {
      return
    }

    setFeedback(null)
    try {
      await deleteUserMutation.mutateAsync(deletingUser.uuid)
      setFeedback({
        type: 'success',
        message: 'Usuário excluído com sucesso.',
      })
      setDeletingUser(null)
    } catch (error) {
      setFeedback({
        type: 'error',
        message:
          error instanceof Error
            ? error.message
            : 'Não foi possível excluir o usuário.',
      })
    }
  }

  return (
    <div className="mx-auto w-full max-w-[96rem] space-y-6 px-4 py-8 sm:px-8 lg:px-12 xl:px-14">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <div className="flex size-10 items-center justify-center rounded-xl bg-violet-100 text-violet-700">
            <Users aria-hidden="true" className="size-5" />
          </div>
          <div>
            <h2 className="text-lg font-semibold text-slate-900">
              Usuários cadastrados
            </h2>
            <p className="text-sm text-slate-500">
              Gerencie os acessos ao painel do Streamora.
            </p>
          </div>
        </div>

        <button
          className="inline-flex items-center gap-2 rounded-xl bg-violet-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-violet-700 focus-visible:ring-2 focus-visible:ring-violet-500"
          onClick={openCreateModal}
          type="button"
        >
          <Plus aria-hidden="true" className="size-4" />
          Novo usuário
        </button>
      </div>

      {feedback ? (
        <div
          className={`rounded-xl border px-4 py-3 text-sm font-medium ${
            feedback.type === 'success'
              ? 'border-emerald-200 bg-emerald-50 text-emerald-800'
              : 'border-rose-200 bg-rose-50 text-rose-800'
          }`}
          role="status"
        >
          {feedback.message}
        </div>
      ) : null}

      {isPending ? (
        <div className="rounded-2xl border border-slate-200 bg-white p-8 text-center text-sm text-slate-500">
          Carregando usuários...
        </div>
      ) : isError ? (
        <div className="rounded-2xl border border-rose-200 bg-rose-50 p-6 text-center">
          <p className="text-sm font-medium text-rose-800">
            Não foi possível carregar os usuários.
          </p>
          <button
            className="mt-4 inline-flex items-center gap-2 rounded-xl bg-rose-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-rose-700"
            onClick={onRetry}
            type="button"
          >
            <RefreshCw aria-hidden="true" className="size-4" />
            Tentar novamente
          </button>
        </div>
      ) : users.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-slate-300 bg-white p-8 text-center">
          <p className="text-sm font-medium text-slate-700">
            Nenhum usuário cadastrado.
          </p>
          <p className="mt-1 text-sm text-slate-500">
            Clique em &quot;Novo usuário&quot; para adicionar o primeiro acesso.
          </p>
        </div>
      ) : (
        <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
          <table className="min-w-full divide-y divide-slate-200 text-left text-sm">
            <thead className="bg-slate-50 text-xs font-semibold uppercase tracking-wide text-slate-500">
              <tr>
                <th className="px-4 py-3">Nome completo</th>
                <th className="px-4 py-3">Exibição</th>
                <th className="px-4 py-3">Usuário</th>
                <th className="px-4 py-3 text-right">Ações</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {users.map((user) => (
                <tr key={user.uuid} className="hover:bg-slate-50/70">
                  <td className="px-4 py-4 font-medium text-slate-900">
                    {user.fullname}
                  </td>
                  <td className="px-4 py-4 text-slate-600">{user.nickname}</td>
                  <td className="px-4 py-4 font-mono text-xs text-slate-600">
                    {user.username}
                  </td>
                  <td className="px-4 py-4 text-right">
                    <div className="inline-flex items-center gap-2">
                      <button
                        aria-label={`Editar ${user.fullname}`}
                        className="inline-flex items-center gap-1 rounded-lg border border-slate-200 px-2.5 py-1.5 text-xs font-medium text-slate-700 transition hover:bg-slate-100"
                        onClick={() => openEditModal(user)}
                        type="button"
                      >
                        <Pencil aria-hidden="true" className="size-3.5" />
                        Editar
                      </button>
                      <button
                        aria-label={`Excluir ${user.fullname}`}
                        className="inline-flex items-center gap-1 rounded-lg border border-rose-200 px-2.5 py-1.5 text-xs font-medium text-rose-700 transition hover:bg-rose-50"
                        onClick={() => setDeletingUser(user)}
                        type="button"
                      >
                        <Trash2 aria-hidden="true" className="size-3.5" />
                        Excluir
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {isFormOpen ? (
        <div
          aria-modal="true"
          className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/50 p-4"
          role="dialog"
        >
          <div className="w-full max-w-md rounded-2xl bg-white p-6 shadow-xl">
            <h3 className="text-lg font-semibold text-slate-900">
              {editingUser ? 'Editar usuário' : 'Novo usuário'}
            </h3>
            <form className="mt-4 space-y-4" onSubmit={handleSubmit}>
              <div>
                <label
                  className="block text-sm font-medium text-slate-700"
                  htmlFor="user-fullname"
                >
                  Nome completo
                </label>
                <input
                  className="mt-1 w-full rounded-xl border border-slate-300 px-3 py-2 text-sm focus:border-violet-500 focus:outline-none"
                  id="user-fullname"
                  onChange={(event) =>
                    setForm((prev) => ({
                      ...prev,
                      fullname: event.target.value,
                    }))
                  }
                  required
                  type="text"
                  value={form.fullname}
                />
              </div>

              <div>
                <label
                  className="block text-sm font-medium text-slate-700"
                  htmlFor="user-nickname"
                >
                  Nome de exibição
                </label>
                <input
                  className="mt-1 w-full rounded-xl border border-slate-300 px-3 py-2 text-sm focus:border-violet-500 focus:outline-none"
                  id="user-nickname"
                  onChange={(event) =>
                    setForm((prev) => ({
                      ...prev,
                      nickname: event.target.value,
                    }))
                  }
                  required
                  type="text"
                  value={form.nickname}
                />
              </div>

              <hr className="border-slate-200" />

              <div>
                <label
                  className="block text-sm font-medium text-slate-700"
                  htmlFor="user-username"
                >
                  Usuário
                </label>
                <input
                  className="mt-1 w-full rounded-xl border border-slate-300 px-3 py-2 text-sm disabled:bg-slate-100 disabled:text-slate-500 focus:border-violet-500 focus:outline-none"
                  disabled={Boolean(editingUser)}
                  id="user-username"
                  onChange={(event) =>
                    setForm((prev) => ({
                      ...prev,
                      username: event.target.value,
                    }))
                  }
                  required={!editingUser}
                  type="text"
                  value={form.username}
                />
              </div>

              <div>
                <label
                  className="block text-sm font-medium text-slate-700"
                  htmlFor="user-password"
                >
                  {editingUser
                    ? 'Nova senha (opcional)'
                    : 'Senha'}
                </label>
                <input
                  className="mt-1 w-full rounded-xl border border-slate-300 px-3 py-2 text-sm focus:border-violet-500 focus:outline-none"
                  id="user-password"
                  onChange={(event) =>
                    setForm((prev) => ({
                      ...prev,
                      password: event.target.value,
                    }))
                  }
                  required={!editingUser}
                  type="password"
                  value={form.password}
                />
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <button
                  className="rounded-xl border border-slate-200 px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50"
                  onClick={closeFormModal}
                  type="button"
                >
                  Cancelar
                </button>
                <button
                  className="rounded-xl bg-violet-600 px-4 py-2 text-sm font-semibold text-white hover:bg-violet-700 disabled:opacity-50"
                  disabled={isSubmitting}
                  type="submit"
                >
                  {isSubmitting ? 'Salvando...' : 'Salvar'}
                </button>
              </div>
            </form>
          </div>
        </div>
      ) : null}

      {deletingUser ? (
        <div
          aria-modal="true"
          className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/50 p-4"
          role="dialog"
        >
          <div className="w-full max-w-sm rounded-2xl bg-white p-6 shadow-xl">
            <h3 className="text-lg font-semibold text-slate-900">
              Excluir usuário
            </h3>
            <p className="mt-2 text-sm text-slate-600">
              Tem certeza que deseja excluir{' '}
              <span className="font-semibold text-slate-900">
                {deletingUser.fullname}
              </span>
              ?
            </p>
            <div className="mt-6 flex justify-end gap-2">
              <button
                className="rounded-xl border border-slate-200 px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50"
                onClick={() => setDeletingUser(null)}
                type="button"
              >
                Cancelar
              </button>
              <button
                className="rounded-xl bg-rose-600 px-4 py-2 text-sm font-semibold text-white hover:bg-rose-700 disabled:opacity-50"
                disabled={deleteUserMutation.isPending}
                onClick={() => void handleConfirmDelete()}
                type="button"
              >
                {deleteUserMutation.isPending ? 'Excluindo...' : 'Excluir'}
              </button>
            </div>
          </div>
        </div>
      ) : null}
    </div>
  )
}
