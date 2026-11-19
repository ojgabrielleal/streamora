import { LoginForm } from '@/components/auth/login-form'

export function HomePage() {
  return (
    <main className="relative flex min-h-svh items-center justify-center overflow-hidden bg-linear-to-br from-violet-50 via-background to-fuchsia-50 px-4 py-10 sm:px-6">
      <div
        aria-hidden="true"
        className="absolute -left-24 -top-24 size-72 rounded-full bg-violet-400/15 blur-3xl"
      />
      <div
        aria-hidden="true"
        className="absolute -bottom-28 -right-20 size-80 rounded-full bg-fuchsia-400/15 blur-3xl"
      />

      <section
        aria-labelledby="login-title"
        className="relative w-full max-w-md rounded-2xl border border-violet-200/80 bg-card/95 px-6 py-8 shadow-xl shadow-violet-950/8 backdrop-blur-sm sm:px-10 sm:py-10"
      >
        <img
          alt="Streamora"
          className="mx-auto h-auto w-40 max-w-full sm:w-48"
          src="/images/brand/vertical-logo.png"
        />

        <div className="mb-8 mt-7 text-center">
          <h1
            className="bg-linear-to-r from-violet-700 to-fuchsia-600 bg-clip-text text-2xl font-semibold tracking-tight text-transparent"
            id="login-title"
          >
            Acesse sua conta
          </h1>
          <p className="mt-2 text-sm text-muted-foreground">
            Entre para gerenciar sua web-rádio.
          </p>
        </div>

        <LoginForm />
      </section>
    </main>
  )
}
