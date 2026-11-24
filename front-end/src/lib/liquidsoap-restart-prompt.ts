import { useSyncExternalStore } from 'react'

const STORAGE_KEY = 'streamora:liquidsoap-restart-needed'

function readInitialState() {
  if (typeof window === 'undefined') {
    return false
  }

  return window.localStorage.getItem(STORAGE_KEY) === 'true'
}

let restartNeeded = readInitialState()
const listeners = new Set<() => void>()

function emit() {
  for (const listener of listeners) {
    listener()
  }
}

export function notifyLiquidsoapRestartNeeded() {
  if (!restartNeeded) {
    restartNeeded = true
    if (typeof window !== 'undefined') {
      window.localStorage.setItem(STORAGE_KEY, 'true')
    }
    emit()
  }
}

export function clearLiquidsoapRestartPrompt() {
  if (restartNeeded) {
    restartNeeded = false
    if (typeof window !== 'undefined') {
      window.localStorage.removeItem(STORAGE_KEY)
    }
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
