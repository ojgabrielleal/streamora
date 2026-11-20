import { useQuery } from '@tanstack/react-query'

import {
  fileTreeQueryOptions,
  ftpConnectionQueryOptions,
} from '@/api/files/file-queries'
import { FileManager } from '@/components/files/file-manager'
import { FtpConnectionCard } from '@/components/files/ftp-connection-card'
import { AuthenticatedLayout } from '@/components/layout/authenticated-layout'

export function FilesPage() {
  const connectionQuery = useQuery(ftpConnectionQueryOptions())
  const fileTreeQuery = useQuery(fileTreeQueryOptions())

  return (
    <AuthenticatedLayout
      activeDestination="files"
      description="Organize a biblioteca compartilhada da sua rádio."
      title="Arquivos"
    >
      <div className="mx-auto max-w-[96rem] space-y-8 px-4 py-8 sm:px-8 sm:py-10 lg:px-12 xl:px-14 xl:py-12">
        <FtpConnectionCard
          connection={connectionQuery.data}
          isError={connectionQuery.isError}
          isPending={connectionQuery.isPending}
        />
        <FileManager
          entries={fileTreeQuery.data}
          isError={fileTreeQuery.isError}
          isPending={fileTreeQuery.isPending}
          onRetry={() => void fileTreeQuery.refetch()}
        />
      </div>
    </AuthenticatedLayout>
  )
}
