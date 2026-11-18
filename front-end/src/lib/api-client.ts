import { clearAccessToken, getAccessToken } from '@/lib/access-token'

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
    if (authenticated && response.status === 401) {
      clearAccessToken()
    }

    throw new ApiError(response.status, responseBody)
  }

  return responseBody as T
}
