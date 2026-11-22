import { queryOptions, useMutation, useQueryClient } from '@tanstack/react-query'

import {
  getLiveBroadcastStatus,
  updateLiveBroadcastStatus,
} from '@/api/settings/live-broadcast-api'

export const liveBroadcastKeys = {
  all: ['live-broadcast'] as const,
}

export function liveBroadcastQueryOptions() {
  return queryOptions({
    queryKey: liveBroadcastKeys.all,
    queryFn: getLiveBroadcastStatus,
  })
}

export function useUpdateLiveBroadcastMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: updateLiveBroadcastStatus,
    onSuccess: (data) => {
      queryClient.setQueryData(liveBroadcastKeys.all, data)
    },
  })
}
