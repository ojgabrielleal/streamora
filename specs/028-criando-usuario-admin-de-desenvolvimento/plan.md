# Plano de implementação - Spec 028

## Objetivo

Criar somente a conta `admin` / `admin` no ambiente de desenvolvimento, de forma segura, idempotente e compatível com a autenticação da aplicação e do FTP.

## Decisões de implementação

- Criar um inicializador Spring condicionado por `@ConditionalOnProperty(name = "environment", havingValue = "dev")`.
- Usar `ApplicationRunner` para executar a criação depois da inicialização do contexto e das migrations do Flyway.
- Consultar `UserRepository.findByUsername("admin")` antes de criar a conta.
- Se a conta existir, não realizar nenhuma alteração, inclusive na senha.
- Criar a conta por `UserService.store`, reutilizando o fluxo existente que codifica a senha no MySQL e cria o usuário virtual no Pure-FTPd.
- Usar `admin` como senha da aplicação e do FTP, mantendo o texto puro apenas durante a chamada de criação.
- Aproveitar a transação de `UserService` para que uma falha do comando do Pure-FTPd também desfaça a persistência no MySQL.
- Adicionar `ENVIRONMENT=dev` ao `.env.example`; o desenvolvedor manterá essa seleção em seu `.env` local.
- Repassar `${ENVIRONMENT}` ao container da aplicação pelo Docker Compose, sem fixar `dev` na configuração do container.
- Usar a propriedade Spring `environment`, alimentada pela variável `ENVIRONMENT`, para condicionar internamente o inicializador.
- Não criar propriedades para a credencial: `admin` / `admin` será uma convenção fixa e exclusiva do desenvolvimento.

## Estrutura proposta

```text
src/main/java/com/streamora/streamora/development/
└── DevelopmentAdminInitializer.java

src/test/java/com/streamora/streamora/development/
└── DevelopmentAdminInitializerTests.java
```

## Etapas

1. Criar o inicializador restrito ao profile `dev`.
2. Implementar a consulta idempotente e criar a conta por `UserService`, com senha codificada no banco e acesso no Pure-FTPd.
3. Documentar a chave `ENVIRONMENT` no `.env.example` e encaminhá-la ao serviço da aplicação no Docker Compose.
4. Criar testes cobrindo criação da aplicação e do FTP, conta existente, falha no provisionamento e condição por ambiente.
5. Atualizar `docs/project-structure.md` com o novo pacote e sua responsabilidade.
6. Executar a suíte de testes e a compilação do backend.
7. Revisar os critérios de aceite e registrar o resultado em `tasks.md` durante a implementação.

## Estratégia de testes

- Simular a ausência do usuário `admin` e verificar que `UserService.store` recebe os campos esperados e a senha `admin`.
- Reutilizar os testes de `UserService` para confirmar a codificação da senha e a chamada `createFtpdUser("admin", "admin")`.
- Simular um usuário `admin` existente e verificar que nem `UserService` nem o Pure-FTPd são acionados.
- Simular falha do provisionamento FTP e verificar sua propagação e o comportamento transacional esperado.
- Confirmar que o componente somente é registrado quando a propriedade `environment` possui o valor `dev`.
- Executar `./mvnw test` e `./mvnw verify` se o wrapper estiver disponível; caso contrário, usar os comandos Maven equivalentes disponíveis no projeto.

## Fora do escopo

- Criar outros usuários ou dados iniciais.
- Tornar as credenciais configuráveis.
- Alterar schema ou criar migrations.
- Alterar autenticação, autorização ou frontend.
