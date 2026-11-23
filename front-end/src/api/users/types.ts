export type UserRole = 'ADMIN' | 'STANDARD'

export type UserResponse = {
  uuid: string
  fullname: string
  nickname: string
  username: string
  role: UserRole
}

export type UserRequest = {
  fullname: string
  nickname: string
  username?: string
  password?: string
  role?: UserRole
}
