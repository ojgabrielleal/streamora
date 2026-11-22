import { useQuery } from '@tanstack/react-query'

import { schedulesQueryOptions } from '@/api/schedules/schedule-queries'
import { AuthenticatedLayout } from '@/components/layout/authenticated-layout'
import { ScheduleBoard } from '@/components/schedules/schedule-board'

export function SchedulesPage() {
  const schedulesQuery = useQuery(schedulesQueryOptions())

  return (
    <AuthenticatedLayout
      activeDestination="schedules"
      description="Organize os horários semanais das playlists no Auto DJ."
      title="Programação"
    >
      <ScheduleBoard
        isError={schedulesQuery.isError}
        isPending={schedulesQuery.isPending}
        onRetry={() => void schedulesQuery.refetch()}
        schedules={schedulesQuery.data}
      />
    </AuthenticatedLayout>
  )
}
