# Spec 013 - Documentando a API com Swagger e OpenAPI

**Status:** Pendente

## Objetivo

Disponibilizar uma documentação interativa e atualizada da API utilizando
OpenAPI e Swagger UI.

## Requisitos

- Utilizar o Springdoc OpenAPI para gerar a especificação OpenAPI da aplicação.
- Disponibilizar a especificação OpenAPI no formato JSON.
- Disponibilizar a interface Swagger UI para consulta e execução dos endpoints.
- Configurar na documentação o título, a descrição e a versão da API na pasta `src/main/java/streamora/streamora/config`
- Documentar os endpoints dos controllers de usuários, playlists e arquivos de
  playlist.
- Organizar os endpoints documentados por recurso.
- Documentar as operações, os parâmetros, os corpos das requisições e as
  respostas de cada endpoint.
- Documentar os DTOs utilizados como contratos de entrada e saída da API.
- Permitir o acesso aos endpoints da especificação OpenAPI e do Swagger UI sem
  autenticação.
- Manter inalterados os caminhos, os verbos HTTP e o comportamento dos
  endpoints existentes.

## Critérios de aceite

- [ ] A especificação OpenAPI pode ser consultada em formato JSON.
- [ ] O Swagger UI pode ser acessado e carrega a especificação da API sem
  erros.
- [ ] Os endpoints de documentação podem ser acessados sem autenticação.
- [ ] A documentação apresenta o título, a descrição e a versão da API
  Streamora.
- [ ] Todos os endpoints de usuários, playlists e arquivos de playlist estão
  documentados e organizados por recurso.
- [ ] Os parâmetros, os corpos das requisições, as respostas e os DTOs estão
  representados corretamente na especificação OpenAPI.
- [ ] É possível executar os endpoints documentados por meio do Swagger UI.
- [ ] Os contratos e o comportamento dos endpoints existentes permanecem
  inalterados.
- [ ] Os testes existentes continuam passando e o projeto compila com sucesso.

## Fora do escopo

- Criar ou alterar endpoints da API.
- Alterar regras de negócio, services, repositories, entities ou migrations.
- Implementar autenticação ou autorização para os endpoints da aplicação.
- Alterar versões de Java, Spring Boot ou de dependências existentes.
