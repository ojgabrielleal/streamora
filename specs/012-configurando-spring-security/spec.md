# Spec 012 - Configurando Spring Security

**Status:** Concluída

## Objetivo

Criar a configuração inicial do Spring Security para que a aplicação tenha
regras de segurança explícitas e não exiba o formulário de login gerado
automaticamente pelo framework.

## Requisitos

- Criar a configuração do Spring Security na pasta
  `src/main/java/com/streamora/streamora/config`.
- Declarar um bean do tipo `SecurityFilterChain`.
- Configurar as regras de autorização para permitir todas as requisições nesta
  etapa inicial, deixando a cadeia preparada para regras futuras por rota e
  método HTTP.
- Desabilitar o formulário de login automático do Spring Security.
- Manter inalterados os caminhos, os verbos HTTP e o comportamento dos
  endpoints existentes.

## Critérios de aceite

- [x] Existe uma classe de configuração do Spring Security no pacote `config`.
- [x] A classe declara um bean do tipo `SecurityFilterChain`.
- [x] As regras de autorização permitem todas as requisições sem autenticação.
- [x] A configuração permite adicionar regras futuras por rota e método HTTP.
- [x] O formulário de login automático do Spring Security não é exibido.
- [x] Os contratos e o comportamento dos endpoints existentes permanecem
  inalterados.
- [x] Os testes existentes continuam passando e o projeto compila com sucesso.

## Fora do escopo

- Implementar autenticação de usuários.
- Implementar autorização, perfis, papéis ou permissões.
- Criar endpoints de login, logout ou renovação de credenciais.
- Implementar autenticação por sessão, HTTP Basic, token ou JWT.
- Alterar a proteção CSRF.
- Alterar controllers, services, repositories, entities, DTOs ou migrations.
- Adicionar dependências ou alterar as versões existentes.
