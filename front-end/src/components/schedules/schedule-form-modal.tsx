import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { X } from 'lucide-react'
import { type FormEvent, useState } from 'react'

import { playlistsQueryOptions } from '@/api/playlists/playlist-queries'
import {
  createSchedule,
  updateSchedule,
} from '@/api/schedules/schedule-api'
import { scheduleKeys } from '@/api/schedules/schedule-queries'
import { notifyLiquidsoapRestartNeeded } from '@/lib/liquidsoap-restart-prompt'
import type { Schedule, WeekDay } from '@/api/schedules/schedule-types'

export const WEEK_DAYS: Array<{ value: WeekDay; label: string }> = [
  { value: 'MONDAY', label: 'Segunda-feira' },
  { value: 'TUESDAY', label: 'Terça-feira' },
  { value: 'WEDNESDAY', label: 'Quarta-feira' },
  { value: 'THURSDAY', label: 'Quinta-feira' },
  { value: 'FRIDAY', label: 'Sexta-feira' },
  { value: 'SATURDAY', label: 'Sábado' },
  { value: 'SUNDAY', label: 'Domingo' },
]

function normalizeTimeForInput(value: string | undefined, fallback: string) {
  if (!value) {
    return fallback
  }
  return value.slice(0, 5)
}

function normalizeTimeForApi(value: string) {
  return value.length === 5 ? `${value}:00` : value
}

type ScheduleFormModalProps = {
  initialSchedule?: Schedule | null
  onClose: () => void
}

export function ScheduleFormModal({
  initialSchedule,
  onClose,
}: ScheduleFormModalProps) {
  const queryClient = useQueryClient()
  const playlistsQuery = useQuery(playlistsQueryOptions())
  const playlists = playlistsQuery.data ?? []

  const [playlistUuid, setPlaylistUuid] = useState(
    initialSchedule?.playlist.uuid ?? '',
  )
  const [dayOfWeek, setDayOfWeek] = useState<WeekDay>(
    initialSchedule?.dayOfWeek ?? 'MONDAY',
  )
  const [startTime, setStartTime] = useState(
    normalizeTimeForInput(initialSchedule?.startTime, '08:00'),
  )
  const [endTime, setEndTime] = useState(
    normalizeTimeForInput(initialSchedule?.endTime, '12:00'),
  )
  const [formError, setFormError] = useState<string | null>(null)

  const saveMutation = useMutation({
    mutationFn: async () => {
      const payload = {
        playlistUuid,
        dayOfWeek,
        startTime: normalizeTimeForApi(startTime),
        endTime: normalizeTimeForApi(endTime),
      }
      if (initialSchedule) {
        return updateSchedule(initialSchedule.uuid, payload)
      }
      return createSchedule(payload)
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: scheduleKeys.all })
      notifyLiquidsoapRestartNeeded()
      onClose()
    },
    onError: (error) => {
      setFormError(
        error instanceof Error
          ? error.message
          : 'Não foi possível salvar o horário.',
      )
    },
  })

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setFormError(null)

    if (!playlistUuid) {
      setFormError('Selecione uma playlist.')
      return
    }
    if (!startTime || !endTime) {
      setFormError('Informe o horário de início e de término.')
      return
    }
    if (endTime <= startTime) {
      setFormError('O horário de término deve ser posterior ao horário de início.')
      return
    }

    saveMutation.mutate()
  }

  return (
    <div
      aria-modal="true"
      className="fixed inset-0 z-50 flex items-center justify-center bg-violet-950/55 p-4 backdrop-blur-sm"
      role="dialog"
    >
      <div className="w-full max-w-md rounded-2xl border border-violet-100 bg-white p-6 shadow-2xl">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-violet-950">
            {initialSchedule ? 'Editar horário' : 'Novo horário'}
          </h2>
          <button
            aria-label="Fechar modal"
            className="rounded-lg p-1.5 text-violet-500 transition hover:bg-violet-50 hover:text-violet-900"
            onClick={onClose}
            type="button"
          >
            <X aria-hidden="true" className="size-5" />
          </button>
        </div>

        <form className="mt-5 space-y-4" onSubmit={handleSubmit}>
          <div>
            <label
              className="block text-sm font-medium text-violet-950"
              htmlFor="schedule-playlist"
            >
              Playlist
            </label>
            <select
              className="mt-1.5 w-full rounded-xl border border-violet-200 bg-white px-3 py-2.5 text-sm text-violet-950 focus:border-violet-500 focus:outline-none focus:ring-2 focus:ring-violet-200"
              disabled={playlistsQuery.isPending}
              id="schedule-playlist"
              onChange={(event) => setPlaylistUuid(event.target.value)}
              value={playlistUuid}
            >
              <option value="">
                {playlistsQuery.isPending
                  ? 'Carregando playlists...'
                  : 'Selecione uma playlist'}
              </option>
              {playlists.map((playlist) => (
                <option key={playlist.uuid} value={playlist.uuid}>
                  {playlist.name}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label
              className="block text-sm font-medium text-violet-950"
              htmlFor="schedule-day"
            >
              Dia da semana
            </label>
            <select
              className="mt-1.5 w-full rounded-xl border border-violet-200 bg-white px-3 py-2.5 text-sm text-violet-950 focus:border-violet-500 focus:outline-none focus:ring-2 focus:ring-violet-200"
              id="schedule-day"
              onChange={(event) => setDayOfWeek(event.target.value as WeekDay)}
              value={dayOfWeek}
            >
              {WEEK_DAYS.map((day) => (
                <option key={day.value} value={day.value}>
                  {day.label}
                </option>
              ))}
            </select>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label
                className="block text-sm font-medium text-violet-950"
                htmlFor="schedule-start"
              >
                Início
              </label>
              <input
                className="mt-1.5 w-full rounded-xl border border-violet-200 bg-white px-3 py-2.5 text-sm text-violet-950 focus:border-violet-500 focus:outline-none focus:ring-2 focus:ring-violet-200"
                id="schedule-start"
                onChange={(event) => setStartTime(event.target.value)}
                required
                type="time"
                value={startTime}
              />
            </div>

            <div>
              <label
                className="block text-sm font-medium text-violet-950"
                htmlFor="schedule-end"
              >
                Término
              </label>
              <input
                className="mt-1.5 w-full rounded-xl border border-violet-200 bg-white px-3 py-2.5 text-sm text-violet-950 focus:border-violet-500 focus:outline-none focus:ring-2 focus:ring-violet-200"
                id="schedule-end"
                onChange={(event) => setEndTime(event.target.value)}
                required
                type="time"
                value={endTime}
              />
            </div>
          </div>

          {formError && (
            <p
              className="rounded-xl border border-rose-200 bg-rose-50 px-3 py-2 text-xs font-medium text-rose-700"
              role="alert"
            >
              {formError}
            </p>
          )}

          <div className="flex justify-end gap-2 pt-2">
            <button
              className="rounded-xl border border-violet-200 px-4 py-2 text-sm font-medium text-violet-800 transition hover:bg-violet-50"
              onClick={onClose}
              type="button"
            >
              Cancelar
            </button>
            <button
              className="rounded-xl bg-linear-to-r from-violet-600 to-fuchsia-600 px-4 py-2 text-sm font-semibold text-white shadow-sm transition hover:opacity-95 disabled:opacity-60"
              disabled={saveMutation.isPending}
              type="submit"
            >
              {saveMutation.isPending ? 'Salvando...' : 'Salvar'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
