import { queryOptions } from '@tanstack/react-query'

import { getFileTree, getFtpConnection } from '@/api/files/file-api'

export const fileKeys = {
  all: ['files'] as const,
  connection: ['files', 'connection'] as const,
  tree: ['files', 'tree'] as const,
}

export function fileTreeQueryOptions() {
  return queryOptions({
    queryKey: fileKeys.tree,
    queryFn: getFileTree,
  })
}

export function ftpConnectionQueryOptions() {
  return queryOptions({
    queryKey: fileKeys.connection,
    queryFn: getFtpConnection,
  })
}
