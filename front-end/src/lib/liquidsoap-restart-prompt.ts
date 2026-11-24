import { useSyncExternalStore } from 'react'

let restartNeeded = false
const listeners = new Set<() => void>()

function emit() {
  for (const listener of listeners) {
    listener()
  }
}

export function notifyLiquidsoapRestartNeeded() {
  if (!restartNeeded) {
    restartNeeded = true
    emit()
  }
}

export function clearLiquidsoapRestartPrompt() {
  if (restartNeeded) {
    restartNeeded = false
    emit()
  }
}

export function useLiquidsoapRestartPrompt() {
  return useSyncExternalStore(
    (listener) => {
      listeners.add(listener)
      return () => listeners.delete(listener)
    },
    () => restartNeeded,
    () => false,
  )
}
