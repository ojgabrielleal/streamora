import { useUsers } from '@/api/users/use-users'
import { AuthenticatedLayout } from '@/components/layout/authenticated-layout'
import { UsersBoard } from '@/components/users/users-board'

export function UsersPage() {
  const usersQuery = useUsers()

  return (
    <AuthenticatedLayout
      activeDestination="users"
      description="Cadastre, edite e remova usuários com acesso ao painel."
      title="Usuários"
    >
      <UsersBoard
        isError={usersQuery.isError}
        isPending={usersQuery.isPending}
        onRetry={() => void usersQuery.refetch()}
        users={usersQuery.data}
      />
    </AuthenticatedLayout>
  )
}
