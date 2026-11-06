# Spec 002 - Configuração inicial do Spring Boot

**Status:** concluida

## Objetivo
Realizar configuração inicial no application.properties

## Requisitos
- Configuração do driver MYSQL
- Configuração do Flyway

## Regras
- Todas as informações sensíveis como dados de acesso ao banco devem ser colocadas no .env e serem chamadas no application.properties.
- As chaves usadas no .env devem ser replicadas no .env.example sem os valores.

## Critérios de aceite
- [ ] Arquivo .env criado com dados sensiveis configurados

## Observações
Os dados de acesso podem ser aleatórios, essa spec irá definir apenas um ambiente de desenvolvimento, não de produção
