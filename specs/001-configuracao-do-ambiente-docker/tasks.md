# Tarefas — Spec 001

## Implementação

- [x] Criar o Dockerfile da aplicação com Java 25.
- [x] Atualizar o Dockerfile para compilar e executar a aplicação Spring Boot existente.
- [x] Criar o Docker Compose com todos os serviços e nomes de contêiner exigidos.
- [x] Configurar MySQL e PHPMyAdmin.
- [x] Configurar Icecast e Liquidsoap.
- [x] Mover a configuração do Liquidsoap para `infrastructure/liquidsoap` e eliminar a pasta `docker`.
- [x] Configurar Pure-FTPd sem criação automática de usuários.
- [x] Disponibilizar Node.js 26.
- [x] Compartilhar os volumes do Pure-FTPd e Liquidsoap com a aplicação.
- [x] Criar `.env.example` e proteger o `.env` no Git e no contexto de build.
- [x] Documentar a operação do ambiente.

## Testes e validação

- [x] Executar os testes Maven da aplicação Spring Boot.
- [x] Reconstruir a imagem e confirmar a inicialização da aplicação Spring Boot.
- [x] Validar o arquivo com `docker compose config`.
- [x] Construir a imagem da aplicação.
- [x] Subir os serviços e verificar seus estados.
- [x] Verificar os nomes dos contêineres.
- [x] Verificar os volumes compartilhados dentro da aplicação.
- [x] Verificar a comunicação entre os serviços.

