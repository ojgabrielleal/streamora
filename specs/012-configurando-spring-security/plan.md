# Plano de implementação — Spec 012

## Objetivo

Adicionar uma configuração inicial e explícita do Spring Security que mantenha
a API pública e remova o formulário de login automático, sem alterar os
contratos ou o comportamento dos endpoints existentes.

## Etapas

1. Criar uma classe de configuração no pacote `config` com um bean
   `SecurityFilterChain`.
2. Organizar a autorização com `authorizeHttpRequests`, deixando a configuração
   preparada para receber regras por rota e método HTTP em specs futuras.
3. Manter todas as requisições permitidas nesta configuração inicial.
4. Desabilitar o formulário de login automático do Spring Security.
5. Criar testes da configuração para confirmar o registro do bean, o acesso
   anônimo e a ausência do formulário de login.
6. Atualizar `docs/project-structure.md` para incluir o novo pacote `config` e
   seu teste correspondente.
7. Executar todos os testes e a compilação Maven.

## Decisões de implementação

- Usar somente a dependência do Spring Security que já existe no projeto.
- Usar a API de matchers do Spring Security, que permite associar caminhos e
  métodos HTTP às regras que forem adicionadas posteriormente.
- Nesta spec, finalizar a cadeia com `anyRequest().permitAll()` e desabilitar
  somente o `formLogin`.
- Não modificar controllers, services, repositories, entities, DTOs,
  migrations ou caminhos HTTP.

## Validação esperada

- O contexto da aplicação expõe exatamente uma cadeia de filtros definida
  pela aplicação.
- Requisições anônimas alcançam os endpoints sem resposta `401` ou
  redirecionamento de login causado pela segurança.
- O endpoint automático `/login` não apresenta o formulário do Spring Security.
- Todos os testes existentes passam e o projeto compila com sucesso.
