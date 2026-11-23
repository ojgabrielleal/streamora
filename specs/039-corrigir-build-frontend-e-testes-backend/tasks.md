# Tarefas - Spec 039

- [x] Adicionar a propriedade `role` no mock de `UserResponse` em `front-end/src/components/users/users-board.test.tsx`.
- [x] Executar `cd front-end && npm run build` e `cd front-end && npm test -- --run` para confirmar que o front-end compila e passa nos testes.
- [x] Diagnosticar e corrigir `StreamoraApplicationTests` e `LiquidsoapControllerTests`.
- [x] Diagnosticar e corrigir `FtpFileControllerTests` e `ServiceStatusControllerTests`.
- [x] Diagnosticar e corrigir `OpenApiDocumentationTests` e `SecurityConfigTests`.
- [x] Executar `./mvnw test` e confirmar 0 falhas e 0 erros.
- [x] Revisar os critérios de aceite da Spec 039.

## Validação

- `cd front-end && npm run build`: passou com código de saída 0 (`tsc -b && vite build`).
- `cd front-end && npm test -- --run`: passou com 17 arquivos de teste e 56 testes aprovados.
- `./mvnw test -q`: passou com código de saída 0 (0 falhas e 0 erros).
