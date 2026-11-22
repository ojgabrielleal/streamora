export type UserResponse = {
  uuid: string
  fullname: string
  nickname: string
  username: string
}

export type UserRequest = {
  fullname: string
  nickname: string
  username?: string
  password?: string
}
