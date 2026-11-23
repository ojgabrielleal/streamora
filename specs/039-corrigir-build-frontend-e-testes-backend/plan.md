# Plano de implementação - Spec 039

## Objetivo

Corrigir o erro de tipagem que bloqueia o build de produção do front-end (`npm run build`) e resolver as falhas de carregamento de contexto e mocks nas 6 classes de teste do back-end que quebram em `./mvnw test`.

## Decisões de implementação

- No front-end, manter os testes unitários existentes intactos e apenas completar o objeto mockado de `UserResponse` em `users-board.test.tsx` com a propriedade `role` exigida pelo tipo atual.
- No back-end, priorizar a correção da configuração dos testes (ex.: adicionar `@MockitoBean` para repositórios/serviços ausentes em `@WebMvcTest`, `@SpringBootTest` ou testes de configuração) sem alterar código de produção, a menos que algum teste revele um defeito real.
- Tratar primeiro `StreamoraApplicationTests` e os slices `@WebMvcTest` / `@SpringBootTest` que compartilham a mesma causa raiz (ex.: ausência de `SettingRepository` ou beans introduzidos recentemente).

## Estrutura proposta

- `front-end/src/components/users/users-board.test.tsx` (ajuste do mock de `UserResponse`)
- `src/test/java/com/streamora/streamora/application/StreamoraApplicationTests.java`
- `src/test/java/com/streamora/streamora/controller/LiquidsoapControllerTests.java`
- `src/test/java/com/streamora/streamora/controller/FtpFileControllerTests.java`
- `src/test/java/com/streamora/streamora/controller/ServiceStatusControllerTests.java`
- `src/test/java/com/streamora/streamora/config/OpenApiDocumentationTests.java`
- `src/test/java/com/streamora/streamora/config/SecurityConfigTests.java`

## Etapas

1. Atualizar o mock de `UserResponse` em `front-end/src/components/users/users-board.test.tsx` e validar `npm run build` e `npm test -- --run`.
2. Inspecionar as causas exatas de falha nas 6 classes de teste do back-end a partir dos relatórios do Surefire / logs de execução.
3. Corrigir os mocks e a configuração de contexto das classes de teste do back-end, seguindo `docs/code-standards.md`.
4. Executar `./mvnw test` e confirmar 0 falhas e 0 erros nos 238 testes.

## Estratégia de testes

- Front-end: `cd front-end && npm run build` (valida `tsc -b` + `vite build`) e `cd front-end && npm test -- --run` (valida os 17 arquivos de teste).
- Back-end: `./mvnw test` (valida toda a suíte Java/Spring Boot sem testes ignorados).

## Validação

- `cd front-end && npm run build`
- `cd front-end && npm test -- --run`
- `./mvnw test`

## Riscos e cuidados

- Evitar mascarar falhas reais removendo asserções ou desabilitando testes.
- Caso alguma falha em `FtpFileControllerTests` ou `ServiceStatusControllerTests` seja causada por mudança de contrato (ex.: novas permissões da Spec 038), verificar a spec/código de produção antes de ajustar o teste.

## Fora do escopo

- Novas funcionalidades ou alterações visuais no front-end.
- Configuração de CI/CD.
- Alterações de banco de dados ou migrations.
