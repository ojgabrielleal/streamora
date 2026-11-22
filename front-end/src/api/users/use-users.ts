import {
  queryOptions,
  useMutation,
  useQuery,
  useQueryClient,
} from '@tanstack/react-query'

import type { UserRequest } from '@/api/users/types'
import {
  createUser,
  deleteUser,
  getUsers,
  updateUser,
} from '@/api/users/users-api'

export const userKeys = {
  all: ['users'] as const,
}

export function usersQueryOptions() {
  return queryOptions({
    queryKey: userKeys.all,
    queryFn: getUsers,
  })
}

export function useUsers() {
  return useQuery(usersQueryOptions())
}

export function useCreateUser() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (payload: UserRequest) => createUser(payload),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: userKeys.all })
    },
  })
}

export function useUpdateUser() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ uuid, payload }: { uuid: string; payload: UserRequest }) =>
      updateUser(uuid, payload),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: userKeys.all })
    },
  })
}

export function useDeleteUser() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (uuid: string) => deleteUser(uuid),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: userKeys.all })
    },
  })
}
