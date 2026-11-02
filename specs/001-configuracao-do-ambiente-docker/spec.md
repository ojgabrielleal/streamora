# Spec 001 - Configuração do ambiente docker

**Status:** concluida
**Branch:** main

## Objetivo
Configurar o ambiente docker para executar a aplicação

## Requisitos
- Criar um Dockerfile para a aplicação
- Criar um docker-compose.yml para gerenciar os serviços
- Utilizar Java 25 
- Utilizar Liquidsoap  
- Configurar e utilizar icecast 
- Configurar e utilizar pureFtpd
- Utilizar PHPMyAdmin
- Utilizar e configurar Mysql
- Utilizar node 26

## Regras
- Dados sensíveis como senhas, usernames devem ficar em um arquivo .env, deve ter um arquivo .env.example para indicar quais chaves são necessárias
- Cada serviço dentro do docker-compose.yml deve ter o nome da tecnologia que está executando
- O pureFtpd não deve gerenciar as senhas automaticamente, ele deve ter um volume separado para gerenciamento das senhas
- Os volumes dos serviços que executarem o pureFtpd e o Liquidsoap devem estar acessiveis e visiveis pelo serviço principal
- Cada serviço deverá ter um container name seguindo essa lógica "streamora-{NOME DO SERVIÇO}" 

## Critérios de aceite
- [ ] Os volumes do pureFtpd e liquidsoap devem estar acessiveis dentro do serviço principal