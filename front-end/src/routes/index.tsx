import { Radio } from 'lucide-react'

import { Button } from '@/components/ui/button'

export function HomePage() {
  return (
    <main className="flex min-h-svh items-center justify-center bg-muted/40 px-6 py-12">
      <section className="w-full max-w-xl rounded-2xl border bg-card p-8 text-center shadow-sm">
        <div className="mx-auto mb-6 flex size-14 items-center justify-center rounded-full bg-primary text-primary-foreground">
          <Radio aria-hidden="true" className="size-7" />
        </div>

        <p className="mb-2 text-sm font-medium tracking-widest text-muted-foreground uppercase">
          Painel web
        </p>
        <h1 className="text-3xl font-semibold tracking-tight sm:text-4xl">
          Streamora
        </h1>
        <p className="mx-auto mt-4 max-w-md text-balance text-muted-foreground">
          A base do painel está pronta para receber o gerenciamento da sua
          web-rádio.
        </p>

        <Button className="mt-8" type="button">
          Setup concluído
        </Button>
      </section>
    </main>
  )
}
