# Spec 028 - Criando usuário administrativo de desenvolvimento

**Status:** Concluída

## Objetivo

Criar automaticamente uma conta administrativa de desenvolvimento com usuário
`admin` e senha `admin`, permitindo autenticar pela tela de login e acessar o
FTP em um banco novo sem realizar um cadastro manual.

## Requisitos

### Ativação

- Declarar `ENVIRONMENT=dev` no arquivo `.env` do ambiente de desenvolvimento.
- Encaminhar `ENVIRONMENT` ao serviço `application` pelo Docker Compose, sem fixar o ambiente dentro do arquivo Compose.
- Executar a criação do usuário somente quando a propriedade `ENVIRONMENT` possuir o valor `dev`.
- Não executar a criação com outros valores de ambiente, inclusive produção e testes.
- Executar a inicialização somente depois que as migrations do Flyway tiverem sido aplicadas.

### Conta de desenvolvimento

- Criar um usuário com `username` igual a `admin` e senha inicial igual a `admin`.
- Definir nome completo e apelido coerentes para preencher os campos obrigatórios da entity `User`.
- Codificar a senha com o `PasswordEncoder` da aplicação antes de persistir o usuário.
- Permitir que a conta autentique pelo endpoint `POST /login` e pela tela de login do frontend.
- Provisionar o usuário virtual `admin` no Pure-FTPd com a mesma senha `admin`.
- Usar o diretório FTP próprio já definido pelo fluxo normal de criação de usuários.
- Não criar outro registro, sobrescrever campos nem redefinir a senha quando o usuário `admin` já existir.
- Não repetir o provisionamento FTP quando a conta de desenvolvimento já existir.
- Propagar falhas do provisionamento FTP e não manter a criação no banco como concluída quando o acesso FTP falhar.
- Não criar playlists, horários ou quaisquer outros dados de desenvolvimento.

### Segurança

- Manter a conta restrita à configuração `ENVIRONMENT=dev`.
- Não registrar a senha, seu hash ou tokens nos logs.
- Deixar explícito que `admin` / `admin` é uma credencial conhecida e exclusiva do desenvolvimento.
- Não definir `dev` como valor padrão no código ou no Docker Compose.
- Documentar `ENVIRONMENT=dev` no `.env.example`, sem adicionar credenciais reais ao arquivo.

### Testes e documentação

- Criar testes cobrindo a criação, codificação da senha, idempotência e preservação de um usuário `admin` existente.
- Verificar por teste que o componente está condicionado a `ENVIRONMENT=dev`.
- Atualizar `docs/project-structure.md` para documentar o componente e sua responsabilidade.
- Não atualizar `docs/database-schema.md`, pois esta spec não altera a estrutura do banco.

## Regras

- Implementar a criação pela aplicação Spring, e não por migration Flyway, para usar o `PasswordEncoder` configurado.
- Consultar a existência da conta por `UserRepository` e usar `UserService` para sua criação, reutilizando a codificação da senha e o provisionamento do Pure-FTPd.
- Reutilizar a entity, repository e configurações existentes.
- Não adicionar dependências.
- Não alterar o contrato de autenticação, a tela de login ou as regras do Spring Security.
- Não transformar a conta de desenvolvimento em mecanismo de bootstrap de produção.

## Fora do escopo

- Criação automática do primeiro administrador em produção.
- Papéis, permissões ou autorização administrativa.
- Cadastro de usuários pelo frontend.
- Criação de playlists, horários, arquivos ou qualquer outro dado inicial.
- Alterações na estrutura das tabelas ou novas migrations.

## Critérios de aceite

- [x] Com `ENVIRONMENT=dev` e sem um usuário `admin`, a aplicação cria a conta `admin` / `admin`.
- [x] A conta criada consegue autenticar em `POST /login` e pela tela de login.
- [x] As credenciais `admin` / `admin` permitem autenticar no Pure-FTPd em desenvolvimento.
- [x] A senha persistida está codificada e não corresponde ao texto `admin`.
- [x] Reiniciar a aplicação não duplica nem sobrescreve a conta.
- [x] Um usuário `admin` existente, inclusive sua senha, é preservado.
- [x] Sem `ENVIRONMENT=dev`, a conta não é criada.
- [x] O ambiente é selecionado pela chave `ENVIRONMENT` no `.env` e repassado pelo Docker Compose.
- [x] Nenhum outro dado de desenvolvimento é criado.
- [x] O acesso FTP é provisionado uma única vez junto com a criação da conta.
- [x] Uma falha no provisionamento FTP impede que a criação seja concluída no banco.
- [x] A senha e seu hash não são exibidos em logs.
- [x] Os testes automatizados e a compilação do backend terminam com sucesso.
- [x] `docs/project-structure.md` documenta a estrutura adicionada.
