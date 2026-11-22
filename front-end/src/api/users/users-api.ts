import type { UserRequest, UserResponse } from '@/api/users/types'
import { apiRequest } from '@/lib/api-client'

export function getUsers() {
  return apiRequest<UserResponse[]>('/user')
}

export function createUser(payload: UserRequest) {
  return apiRequest<UserResponse>('/user', {
    method: 'POST',
    json: payload,
  })
}

export function updateUser(uuid: string, payload: UserRequest) {
  return apiRequest<UserResponse>(`/user/${uuid}`, {
    method: 'PUT',
    json: payload,
  })
}

export function deleteUser(uuid: string) {
  return apiRequest<void>(`/user/${uuid}`, {
    method: 'DELETE',
  })
}
