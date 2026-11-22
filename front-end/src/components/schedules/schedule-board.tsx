import { useMutation, useQueryClient } from '@tanstack/react-query'
import { CalendarDays, Clock, Pencil, Plus, Trash2 } from 'lucide-react'
import { useState } from 'react'

import { deleteSchedule } from '@/api/schedules/schedule-api'
import { scheduleKeys } from '@/api/schedules/schedule-queries'
import type { Schedule, WeekDay } from '@/api/schedules/schedule-types'
import {
  ScheduleFormModal,
  WEEK_DAYS,
} from '@/components/schedules/schedule-form-modal'

type ScheduleBoardProps = {
  isError: boolean
  isPending: boolean
  onRetry: () => void
  schedules: Schedule[] | undefined
}

function formatTime(value: string) {
  return value.slice(0, 5)
}

export function ScheduleBoard({
  isError,
  isPending,
  onRetry,
  schedules,
}: ScheduleBoardProps) {
  const queryClient = useQueryClient()
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [editingSchedule, setEditingSchedule] = useState<Schedule | null>(null)
  const [confirmingDeleteUuid, setConfirmingDeleteUuid] = useState<string | null>(
    null,
  )

  const deleteMutation = useMutation({
    mutationFn: (uuid: string) => deleteSchedule(uuid),
    onSuccess: async () => {
      setConfirmingDeleteUuid(null)
      await queryClient.invalidateQueries({ queryKey: scheduleKeys.all })
    },
  })

  function openCreateModal() {
    setEditingSchedule(null)
    setIsModalOpen(true)
  }

  function openEditModal(schedule: Schedule) {
    setEditingSchedule(schedule)
    setIsModalOpen(true)
  }

  function closeModal() {
    setIsModalOpen(false)
    setEditingSchedule(null)
  }

  const groupedByDay = WEEK_DAYS.map((day) => {
    const items = (schedules ?? [])
      .filter((item) => item.dayOfWeek === (day.value as WeekDay))
      .sort((a, b) => a.startTime.localeCompare(b.startTime))
    return { ...day, items }
  })

  return (
    <div className="mx-auto w-full max-w-[96rem] px-4 py-8 sm:px-8 lg:px-12 xl:px-14">
      <div className="mb-6 flex flex-wrap items-center justify-between gap-4">
        <div>
          <p className="text-sm text-muted-foreground">
            {schedules
              ? `${schedules.length} ${schedules.length === 1 ? 'horário cadastrado' : 'horários cadastrados'}`
              : 'Gerencie os blocos semanais de transmissão'}
          </p>
        </div>
        <button
          className="inline-flex items-center gap-2 rounded-xl bg-linear-to-r from-violet-600 to-fuchsia-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:opacity-95"
          onClick={openCreateModal}
          type="button"
        >
          <Plus aria-hidden="true" className="size-4" />
          Novo horário
        </button>
      </div>

      {isPending && (
        <div className="rounded-2xl border border-violet-100 bg-white p-8 text-center text-sm text-muted-foreground shadow-sm">
          Carregando grade de programação...
        </div>
      )}

      {isError && (
        <div className="rounded-2xl border border-rose-200 bg-rose-50 p-6 text-center shadow-sm">
          <p className="text-sm font-medium text-rose-800">
            Não foi possível carregar a grade de programação.
          </p>
          <button
            className="mt-3 rounded-xl border border-rose-300 bg-white px-4 py-2 text-xs font-semibold text-rose-700 transition hover:bg-rose-100"
            onClick={onRetry}
            type="button"
          >
            Tentar novamente
          </button>
        </div>
      )}

      {!isPending && !isError && (schedules?.length ?? 0) === 0 && (
        <div className="rounded-2xl border border-dashed border-violet-200 bg-white p-10 text-center shadow-sm">
          <CalendarDays
            aria-hidden="true"
            className="mx-auto size-10 text-violet-400"
          />
          <h2 className="mt-3 text-base font-semibold text-violet-950">
            Nenhum horário cadastrado
          </h2>
          <p className="mt-1 text-sm text-muted-foreground">
            Adicione o primeiro horário para organizar as playlists da semana.
          </p>
        </div>
      )}

      {!isPending && !isError && (schedules?.length ?? 0) > 0 && (
        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
          {groupedByDay.map((day) => (
            <section
              className="rounded-2xl border border-violet-100 bg-white p-5 shadow-sm"
              key={day.value}
            >
              <div className="flex items-center justify-between border-b border-violet-100 pb-3">
                <h2 className="text-sm font-semibold text-violet-950">
                  {day.label}
                </h2>
                <span className="rounded-full bg-violet-50 px-2.5 py-0.5 text-xs font-medium text-violet-700">
                  {day.items.length}
                </span>
              </div>

              {day.items.length === 0 ? (
                <p className="py-6 text-center text-xs text-muted-foreground">
                  Sem horários definidos
                </p>
              ) : (
                <ul className="mt-3 space-y-2.5">
                  {day.items.map((item) => (
                    <li
                      className="rounded-xl border border-violet-100 bg-violet-50/40 p-3"
                      key={item.uuid}
                    >
                      <div className="flex items-start justify-between gap-2">
                        <div className="min-w-0">
                          <p className="truncate text-sm font-semibold text-violet-950">
                            {item.playlist.name}
                          </p>
                          <p className="mt-1 inline-flex items-center gap-1.5 text-xs text-violet-700">
                            <Clock aria-hidden="true" className="size-3.5" />
                            {formatTime(item.startTime)} -{' '}
                            {formatTime(item.endTime)}
                          </p>
                        </div>

                        <div className="flex items-center gap-1">
                          <button
                            aria-label={`Editar horário de ${item.playlist.name}`}
                            className="rounded-lg p-1.5 text-violet-600 transition hover:bg-violet-100"
                            onClick={() => openEditModal(item)}
                            type="button"
                          >
                            <Pencil aria-hidden="true" className="size-4" />
                          </button>
                          <button
                            aria-label={`Excluir horário de ${item.playlist.name}`}
                            className="rounded-lg p-1.5 text-rose-600 transition hover:bg-rose-100"
                            onClick={() => setConfirmingDeleteUuid(item.uuid)}
                            type="button"
                          >
                            <Trash2 aria-hidden="true" className="size-4" />
                          </button>
                        </div>
                      </div>

                      {confirmingDeleteUuid === item.uuid && (
                        <div className="mt-2.5 flex items-center justify-between gap-2 border-t border-violet-100 pt-2 text-xs">
                          <span className="font-medium text-rose-700">
                            Confirmar exclusão?
                          </span>
                          <div className="flex gap-1.5">
                            <button
                              className="rounded-lg border border-violet-200 bg-white px-2.5 py-1 font-medium text-violet-800 hover:bg-violet-50"
                              onClick={() => setConfirmingDeleteUuid(null)}
                              type="button"
                            >
                              Cancelar
                            </button>
                            <button
                              className="rounded-lg bg-rose-600 px-2.5 py-1 font-semibold text-white hover:bg-rose-700 disabled:opacity-60"
                              disabled={deleteMutation.isPending}
                              onClick={() => deleteMutation.mutate(item.uuid)}
                              type="button"
                            >
                              Excluir
                            </button>
                          </div>
                        </div>
                      )}
                    </li>
                  ))}
                </ul>
              )}
            </section>
          ))}
        </div>
      )}

      {isModalOpen && (
        <ScheduleFormModal
          initialSchedule={editingSchedule}
          onClose={closeModal}
        />
      )}
    </div>
  )
}
