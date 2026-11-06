# Plano de implementação — Spec 013

## Objetivo

Disponibilizar a especificação OpenAPI e o Swagger UI da API Streamora,
documentando integralmente os recursos de usuários, playlists e arquivos de
playlist sem alterar os contratos ou o comportamento dos endpoints existentes.

## Etapas

1. Configurar o OpenAPI em `config` com título, descrição e versão da API.
2. Ajustar a configuração criada pela Spec 012 para permitir acesso anônimo aos
   caminhos do JSON OpenAPI e do Swagger UI, preservando as regras definidas
   para os demais caminhos.
3. Documentar os controllers com tags por recurso e descrever cada operação,
   parâmetro, corpo de requisição e resposta, sem modificar seus mappings.
4. Documentar os DTOs de entrada e saída com schemas, descrições e exemplos
   coerentes com os contratos atuais.
5. Criar testes para validar os metadados globais, os caminhos e operações da
   especificação, os schemas dos DTOs e o acesso anônimo aos endpoints de
   documentação.
6. Atualizar `docs/project-structure.md` para registrar o novo pacote `config`
   e os testes de configuração/documentação.
7. Executar todos os testes e a compilação Maven, corrigindo apenas problemas
   relacionados ao escopo da spec.

## Decisões de implementação

- Usar a dependência `springdoc-openapi-starter-webmvc-ui` já declarada no
  projeto, sem alterar versões.
- Manter os caminhos padrão do Springdoc: `/v3/api-docs` para o JSON e
  `/swagger-ui.html`/`/swagger-ui/**` para a interface.
- Usar anotações OpenAPI somente como metadados de documentação; assinaturas,
  verbos HTTP, caminhos e delegação dos controllers permanecerão inalterados.
- Representar as respostas conforme os tipos atualmente retornados pelos
  controllers, incluindo listas, respostas unitárias e exclusões sem corpo.

## Validação esperada

- O JSON OpenAPI responde sem autenticação e contém os 17 endpoints/operações
  existentes nos três controllers.
- O Swagger UI responde sem autenticação e referencia a especificação.
- Título, descrição, versão, tags, parâmetros, corpos, respostas e schemas
  aparecem corretamente no documento OpenAPI.
- Os testes existentes continuam passando e o projeto compila com sucesso.
