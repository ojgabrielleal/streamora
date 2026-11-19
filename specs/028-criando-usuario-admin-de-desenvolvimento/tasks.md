# Tarefas - Spec 028

- [x] Criar o inicializador da conta administrativa de desenvolvimento.
- [x] Condicionar a inicialização a `ENVIRONMENT=dev`.
- [x] Reutilizar `UserService` para criar a conta na aplicação e no Pure-FTPd.
- [x] Impedir a duplicação ou alteração de uma conta `admin` existente.
- [x] Documentar `ENVIRONMENT` no `.env.example`.
- [x] Encaminhar `ENVIRONMENT` ao container da aplicação no Docker Compose.
- [x] Criar testes de criação, idempotência e condição por ambiente.
- [x] Atualizar a documentação da estrutura do projeto.
- [x] Executar os testes e verificar a compilação do backend.
- [x] Revisar os critérios de aceite com o resultado da validação.

## Validação

- `./mvnw verify`: 202 testes aprovados e pacote da aplicação gerado.
- `./mvnw -q -Dtest=DevelopmentAdminInitializerTests test`: 4 testes aprovados.
- `docker compose config --quiet`: configuração válida.
- `git diff --check`: nenhuma inconsistência de whitespace encontrada.
- Todos os critérios de aceite da spec foram atendidos.
