# Plano de implementação — Spec 001

## Objetivo

Criar um ambiente Docker Compose para executar a aplicação Streamora e os serviços MySQL, PHPMyAdmin, Icecast, Liquidsoap, Pure-FTPd e Node.js, mantendo credenciais no arquivo `.env` e compartilhando com a aplicação os dados necessários de áudio e FTP.

## Premissas

- O serviço principal será chamado `application`.
- Os demais serviços serão chamados de acordo com a tecnologia executada: `mysql`, `phpmyadmin`, `icecast`, `liquidsoap`, `pureftpd` e `node`.
- Cada contêiner terá um nome explícito no padrão `streamora-{nome-do-serviço}`: `streamora-application`, `streamora-mysql`, `streamora-phpmyadmin`, `streamora-icecast`, `streamora-liquidsoap`, `streamora-pureftpd` e `streamora-node`.
- A aplicação Spring Boot existente será compilada com o Maven Wrapper fornecido pelo projeto.
- O Dockerfile utilizará Java 25 tanto na compilação quanto na execução, em conformidade com a versão declarada no `pom.xml`.
- A imagem final conterá o artefato Spring Boot produzido no estágio de build e iniciará a aplicação diretamente, sem o modo de espera provisório.
- O Node.js 26 será disponibilizado em um serviço próprio.
- Liquidsoap publicará o fluxo no Icecast e utilizará uma configuração versionada no repositório.
- Configurações de componentes externos serão organizadas sob `infrastructure/`; a configuração do Liquidsoap ficará em `infrastructure/liquidsoap/radio.liq`.
- Pure-FTPd utilizará um volume persistente separado para sua base de usuários/senhas; o gerenciamento das senhas não será automatizado pelo Compose.
- Credenciais e demais valores sensíveis serão lidos exclusivamente do `.env`, com chaves documentadas em `.env.example` sem valores reais.

## Etapas

1. Atualizar o `Dockerfile` para um build multiestágio: compilar a aplicação com Maven Wrapper e JDK 25, copiar somente o JAR executável para uma imagem com JRE 25 e iniciar a aplicação com `java -jar`.
2. Criar o `docker-compose.yml` com os serviços `application`, `mysql`, `phpmyadmin`, `icecast`, `liquidsoap`, `pureftpd` e `node`, atribuindo a cada um o `container_name` no padrão `streamora-{nome-do-serviço}` e incluindo redes, dependências, portas, verificações de saúde aplicáveis e políticas de reinício.
3. Configurar volumes persistentes para MySQL, dados/configuração do Pure-FTPd e conteúdo/configuração do Liquidsoap.
4. Montar os volumes de conteúdo do Pure-FTPd e do Liquidsoap também no serviço `application`, atendendo ao critério de aceite; manter a base de senhas do Pure-FTPd em volume exclusivo do próprio serviço.
5. Adicionar configurações mínimas versionadas do Icecast e Liquidsoap, parametrizadas por variáveis de ambiente quando contiverem dados sensíveis.
6. Mover a configuração do Liquidsoap de `docker/liquidsoap/` para `infrastructure/liquidsoap/`, atualizar a referência no Compose e remover a pasta `docker/` vazia.
7. Criar `.env.example` com todas as variáveis necessárias e garantir que `.env` esteja ignorado pelo Git.
8. Documentar no `README.md` como preparar o `.env`, construir/subir os serviços, criar manualmente usuários do Pure-FTPd e verificar os volumes compartilhados.
9. Criar `tasks.md` com as tarefas e os testes derivados deste plano.

## Validação

- Validar a sintaxe e a interpolação com `docker compose config` usando valores locais não sensíveis.
- Verificar no resultado do Compose que todos os serviços possuem o `container_name` exigido pela especificação.
- Construir a imagem da aplicação com `docker compose build application`.
- Executar os testes Maven existentes fora do build da imagem e verificar a geração do JAR Spring Boot.
- Subir os serviços e verificar o estado/saúde com `docker compose up -d` e `docker compose ps`.
- Confirmar nos logs que o Spring Boot iniciou e está escutando na porta configurada.
- Confirmar a conectividade entre Liquidsoap e Icecast.
- Confirmar que o Compose monta `infrastructure/liquidsoap/radio.liq` e que não restaram referências à antiga pasta `docker/`.
- Confirmar o acesso do PHPMyAdmin ao MySQL.
- Confirmar que o volume de usuários/senhas do Pure-FTPd é separado do volume de arquivos enviados.
- Confirmar, dentro de `application`, que os pontos de montagem compartilhados do Pure-FTPd e Liquidsoap estão visíveis e acessíveis.
- Executar os testes existentes e a compilação da aplicação, caso passem a existir durante a implementação.

## Critérios de conclusão

- Todos os requisitos da `spec.md` estão representados no ambiente Docker.
- Todos os contêineres seguem o padrão de nome `streamora-{nome-do-serviço}`.
- Nenhuma credencial real está versionada.
- Os volumes de arquivos do Pure-FTPd e de conteúdo do Liquidsoap estão montados no serviço `application`.
- Os comandos de validação aplicáveis terminam com sucesso, ou suas limitações são registradas na revisão final.
- A compilação e a execução utilizam Java 25, conforme a Spec e o `pom.xml`.
