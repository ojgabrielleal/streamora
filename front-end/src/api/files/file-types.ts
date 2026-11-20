export type FtpFileType = 'DIRECTORY' | 'FILE'

export type FtpFileEntry = {
  name: string
  type: FtpFileType
  format: string | null
  size: number | null
  lastModified: string
  children: FtpFileEntry[]
}

export type FtpConnection = {
  host: string
  port: number
  username: string
}
