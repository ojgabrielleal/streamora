# Plano de implementação — Spec 013

## Objetivo

Disponibilizar a especificação OpenAPI e o Swagger UI da API Streamora,
documentando integralmente os recursos de usuários, playlists e arquivos de
playlist sem alterar os contratos ou o comportamento dos endpoints existentes.

## Etapas

1. Configurar o OpenAPI em `config` com título, descrição e versão da API.
2. Ajustar a configuração criada pela Spec 012 para permitir acesso anônimo
   somente com o método `GET` nos caminhos do JSON OpenAPI, do Swagger UI e de
   seus recursos.
3. Configurar qualquer outra requisição com `authenticated()`, sem implementar
   nesta spec o mecanismo de autenticação.
4. Documentar os controllers com tags por recurso e descrever cada operação,
   parâmetro, corpo de requisição e resposta, sem modificar seus mappings.
5. Documentar os DTOs de entrada e saída com schemas, descrições e exemplos
   coerentes com os contratos atuais.
6. Criar testes para validar os metadados globais, os caminhos e operações da
   especificação, os schemas dos DTOs, o acesso anônimo aos endpoints de
   documentação e o bloqueio anônimo das demais rotas.
7. Atualizar `docs/project-structure.md` para registrar os novos componentes de
   configuração e os testes de documentação.
8. Executar todos os testes e a compilação Maven, corrigindo apenas problemas
   relacionados ao escopo da spec.

## Decisões de implementação

- Usar a dependência `springdoc-openapi-starter-webmvc-ui` já declarada no
  projeto, sem alterar versões.
- Manter os caminhos padrão do Springdoc: `/v3/api-docs` para o JSON e
  `/swagger-ui.html`/`/swagger-ui/**` para a interface.
- Declarar explicitamente como público, por meio de um matcher com
  `HttpMethod.GET`, o acesso a `/v3/api-docs`, `/v3/api-docs/**`,
  `/swagger-ui.html` e `/swagger-ui/**`; finalizar a cadeia com
  `anyRequest().authenticated()`, protegendo todos os demais métodos e
  caminhos.
- Não adicionar login, HTTP Basic, JWT, usuários, papéis ou permissões nesta
  spec.
- Usar anotações OpenAPI somente como metadados de documentação; assinaturas,
  verbos HTTP, caminhos e delegação dos controllers permanecerão inalterados.
- Representar as respostas conforme os tipos atualmente retornados pelos
  controllers, incluindo listas, respostas unitárias e exclusões sem corpo.

## Validação esperada

- O JSON OpenAPI responde sem autenticação e contém os 17 endpoints/operações
  existentes nos três controllers.
- O Swagger UI responde sem autenticação e referencia a especificação.
- Os recursos usados pela interface do Swagger carregam sem autenticação.
- Requisições com métodos diferentes de `GET` nos caminhos da documentação
  são bloqueadas quando não há autenticação.
- Uma requisição anônima para qualquer endpoint da API é bloqueada pelo
  Spring Security.
- Título, descrição, versão, tags, parâmetros, corpos, respostas e schemas
  aparecem corretamente no documento OpenAPI.
- Os testes existentes continuam passando e o projeto compila com sucesso.
