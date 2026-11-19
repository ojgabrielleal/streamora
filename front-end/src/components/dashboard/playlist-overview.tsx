import { ListMusic } from 'lucide-react'

import type { Playlist } from '@/api/playlists/playlist-types'

type PlaylistDataProps = {
  playlists: Playlist[]
}

const weekDays: Record<string, string> = {
  MONDAY: 'Segunda-feira',
  TUESDAY: 'Terça-feira',
  WEDNESDAY: 'Quarta-feira',
  THURSDAY: 'Quinta-feira',
  FRIDAY: 'Sexta-feira',
  SATURDAY: 'Sábado',
  SUNDAY: 'Domingo',
}

const weekDayOrder = Object.keys(weekDays)

function formatTime(time: string) {
  return time.slice(0, 5)
}

export function ScheduleOverview({ playlists }: PlaylistDataProps) {
  const scheduledPlaylists = playlists
    .flatMap((playlist) =>
      playlist.schedules.map((schedule) => ({
        ...schedule,
        playlistName: playlist.name,
      })),
    )
    .sort((first, second) => {
      const dayDifference =
        weekDayOrder.indexOf(first.dayOfWeek) -
        weekDayOrder.indexOf(second.dayOfWeek)

      return dayDifference || first.startTime.localeCompare(second.startTime)
    })

  return (
      <section
        aria-labelledby="schedule-overview-title"
        className="overflow-hidden rounded-2xl border border-violet-200 bg-violet-950 shadow-lg shadow-violet-950/10"
      >
        <div className="bg-linear-to-r from-violet-700 to-fuchsia-600 px-5 py-4 sm:px-6">
          <h2 className="text-lg font-semibold text-white" id="schedule-overview-title">
            Agendado
          </h2>
          <p className="mt-1 text-sm text-violet-100">
            Próximos horários da programação semanal.
          </p>
        </div>

        {scheduledPlaylists.length === 0 ? (
          <p className="px-5 py-10 text-center text-sm text-violet-300" role="status">
            Nenhum horário programado até o momento.
          </p>
        ) : (
          <ul className="divide-y divide-white/8">
            {scheduledPlaylists.slice(0, 5).map((schedule) => (
              <li
                className="flex flex-col gap-3 px-5 py-4 text-white sm:flex-row sm:items-center sm:justify-between sm:px-6"
                key={schedule.uuid}
              >
                <div className="flex min-w-0 items-center gap-3">
                  <span className="flex size-10 shrink-0 items-center justify-center rounded-xl bg-white/8 text-fuchsia-300">
                    <ListMusic aria-hidden="true" className="size-5" />
                  </span>
                  <div className="min-w-0">
                    <p className="text-xs font-semibold uppercase tracking-wide text-violet-300">
                      Playlist
                    </p>
                    <p className="truncate font-semibold text-white">
                      {schedule.playlistName}
                    </p>
                  </div>
                </div>

                <div className="pl-13 text-left sm:pl-0 sm:text-right">
                  <p className="text-sm font-medium text-white">
                    {formatTime(schedule.startTime)} – {formatTime(schedule.endTime)}
                  </p>
                  <p className="mt-1 text-xs text-violet-300">
                    {weekDays[schedule.dayOfWeek] ?? schedule.dayOfWeek}
                  </p>
                </div>
              </li>
            ))}
          </ul>
        )}
      </section>
  )
}
