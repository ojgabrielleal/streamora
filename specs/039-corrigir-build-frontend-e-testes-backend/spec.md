# Spec 039 - Corrigir build do front-end e suíte de testes do back-end

## Objetivo

Restaurar a compilação de produção do front-end (`npm run build`) e a execução completa da suíte de testes do back-end (`./mvnw test`) sem falhas ou erros.

## Contexto

Após a conclusão das últimas alterações do projeto (incluindo a Spec 038), a validação completa identificou dois bloqueios:

- No front-end, `npm test -- --run` passa (17 arquivos, 56 testes), mas `npm run build` (`tsc -b && vite build`) falha em `front-end/src/components/users/users-board.test.tsx` porque o objeto mockado de `UserResponse` não informa a propriedade obrigatória `role`.
- No back-end, `./mvnw test` executa 238 testes com **10 falhas e 24 erros** distribuídos entre `StreamoraApplicationTests`, `LiquidsoapControllerTests`, `FtpFileControllerTests`, `ServiceStatusControllerTests`, `OpenApiDocumentationTests` e `SecurityConfigTests` (incluindo falhas de carregamento do `ApplicationContext` por ausência de beans como `SettingRepository` em slices de teste).

## Requisitos

### Front-end

- Atualizar o(s) mock(s) de `UserResponse` em `front-end/src/components/users/users-board.test.tsx` para incluir a propriedade `role` com um `UserRole` válido.
- Garantir que `npm run build` e `npm test -- --run` executem com sucesso em `front-end/`.

### Back-end

- Corrigir o carregamento de contexto e os mocks/configurações de teste nas classes que falham em `./mvnw test`:
  - `StreamoraApplicationTests`
  - `LiquidsoapControllerTests`
  - `FtpFileControllerTests`
  - `ServiceStatusControllerTests`
  - `OpenApiDocumentationTests`
  - `SecurityConfigTests`
- Garantir que `./mvnw test` execute todos os testes sem falhas, erros ou testes ignorados para mascarar problemas.

## Regras

- Não remover testes existentes nem relaxar asserções apenas para forçar aprovação.
- Não alterar regras de negócio em produção além do estritamente necessário caso algum teste exponha um defeito real.
- Manter a conformidade com `docs/code-standards.md` ao editar código Java.

## Fora do escopo

- Criação de novas funcionalidades ou alterações de UI.
- Configuração de pipelines de CI/CD.
- Alterações de banco de dados ou migrations.

## Critérios de aceite

- [ ] `cd front-end && npm run build` conclui com código de saída 0.
- [ ] `cd front-end && npm test -- --run` conclui com todos os testes passando.
- [ ] `./mvnw test` conclui com código de saída 0, 0 falhas e 0 erros.
