const apiUrl = import.meta.env.VITE_API_URL

export function apiFetch(path: string, init?: RequestInit) {
  if (!apiUrl) {
    throw new Error('VITE_API_URL is not configured')
  }

  return fetch(new URL(path, apiUrl), init)
}
