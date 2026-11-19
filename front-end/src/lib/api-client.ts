import {
  clearAccessToken,
  getAccessToken,
  setAccessToken,
} from '@/lib/access-token'

type AccessTokenResponse = {
  accessToken: string
}

export class ApiError extends Error {
  readonly status: number
  readonly body: unknown

  constructor(status: number, body: unknown) {
    super(`API request failed with status ${status}`)
    this.name = 'ApiError'
    this.status = status
    this.body = body
  }
}

type ApiRequestOptions = Omit<RequestInit, 'body'> & {
  authenticated?: boolean
  json?: unknown
}

let refreshPromise: Promise<void> | null = null

function getApiUrl() {
  const apiUrl = import.meta.env.VITE_API_URL

  if (!apiUrl) {
    throw new Error('VITE_API_URL is not configured')
  }

  return apiUrl
}

async function parseResponse(response: Response) {
  if (response.status === 204) {
    return undefined
  }

  const contentType = response.headers.get('content-type') ?? ''

  if (contentType.includes('application/json')) {
    return response.json()
  }

  const responseText = await response.text()

  return responseText || undefined
}

export async function apiRequest<T>(
  path: string,
  options: ApiRequestOptions = {},
): Promise<T> {
  return executeRequest<T>(path, options, true)
}

export function refreshAccessToken() {
  if (!refreshPromise) {
    refreshPromise = requestNewAccessToken().finally(() => {
      refreshPromise = null
    })
  }

  return refreshPromise
}

async function executeRequest<T>(
  path: string,
  options: ApiRequestOptions,
  allowRefresh: boolean,
): Promise<T> {
  const {
    authenticated = true,
    headers: requestHeaders,
    json,
    ...requestOptions
  } = options
  const headers = new Headers(requestHeaders)
  const accessToken = getAccessToken()

  if (json !== undefined) {
    headers.set('Content-Type', 'application/json')
  }

  if (authenticated && accessToken) {
    headers.set('Authorization', `Bearer ${accessToken}`)
  }

  const response = await fetch(new URL(path, getApiUrl()), {
    ...requestOptions,
    body: json === undefined ? undefined : JSON.stringify(json),
    headers,
  })
  const responseBody = await parseResponse(response)

  if (!response.ok) {
    if (authenticated && response.status === 401 && allowRefresh) {
      try {
        await refreshAccessToken()

        return executeRequest<T>(path, options, false)
      } catch (refreshError) {
        if (refreshError instanceof ApiError && refreshError.status === 401) {
          clearAccessToken()
        }

        throw refreshError
      }
    }

    if (authenticated && response.status === 401) {
      clearAccessToken()
    }

    throw new ApiError(response.status, responseBody)
  }

  return responseBody as T
}

async function requestNewAccessToken() {
  const response = await fetch(new URL('/auth/refresh', getApiUrl()), {
    credentials: 'include',
    method: 'POST',
  })
  const responseBody = await parseResponse(response)

  if (!response.ok) {
    if (response.status === 401) {
      clearAccessToken()
    }

    throw new ApiError(response.status, responseBody)
  }

  const tokenResponse = responseBody as AccessTokenResponse

  setAccessToken(tokenResponse.accessToken)
}
