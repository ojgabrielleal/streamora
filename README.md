# Streamora

Ambiente para gerenciamento de web-rádio com Spring Boot, MySQL, Icecast,
Liquidsoap e Pure-FTPd.

## Requisitos

- Docker com o plugin Docker Compose.

## Configuração

Crie o arquivo local de variáveis e substitua todos os valores `CHANGE_ME`:

```bash
cp .env.example .env
```

O arquivo `.env` não deve ser versionado. O Dockerfile compila a aplicação com
o Maven Wrapper e JDK 25 em um estágio de build, copia somente o JAR gerado para
uma imagem com JRE 25 e inicia o Spring Boot automaticamente.

## Execução

```bash
docker compose config
docker compose build application
docker compose up -d
docker compose ps
```

Serviços expostos por padrão:

| Serviço | Endereço/porta |
| --- | --- |
| Aplicação | `http://localhost:8080` |
| PHPMyAdmin | `http://localhost:8081` |
| Icecast | `http://localhost:8000` |
| MySQL | `localhost:3306` |
| Pure-FTPd | `localhost:21` e portas passivas `30000-30009` |

Os arquivos enviados por FTP ficam no volume `pureftpd_files`. As mídias do
auto-DJ ficam no volume `liquidsoap_media`. Ambos são montados no serviço
`application`, respectivamente em `/data/pureftpd` e `/data/liquidsoap`.

## Usuários do Pure-FTPd

A aplicação sincroniza automaticamente a base virtual do Pure-FTPd quando um
usuário é criado, tem suas credenciais alteradas ou é excluído. A senha é
enviada diretamente à entrada padrão do `pure-pw` e não é incluída nos
argumentos do processo ou nos logs.

A base de credenciais é persistida no volume `pureftpd_passwords`, compartilhado
entre os serviços `application` e `pureftpd`. Os valores `PUREFTPD_UID` e
`PUREFTPD_GID` devem ser iguais nos dois serviços para que os diretórios dos
usuários mantenham as permissões corretas; o padrão do Compose é `1000` para
ambos.

O username é definido no cadastro e não pode ser alterado. Ele pode ser exibido
ao próprio usuário para configuração do cliente FTP. Nome completo e apelido
continuam editáveis, e alterações nesses campos não modificam o acesso FTP.

## Verificação dos volumes compartilhados

```bash
docker exec streamora-application sh -c \
  'test -d /data/pureftpd && test -d /data/liquidsoap'
```

Para encerrar o ambiente sem remover os volumes persistentes:

```bash
docker compose down
```
