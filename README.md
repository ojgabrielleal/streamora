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

O Compose não cria usuários nem gerencia senhas automaticamente. A base de
senhas é persistida separadamente no volume `pureftpd_passwords`. Para criar um
usuário manualmente, execute:

```bash
docker exec -it streamora-pureftpd pure-pw useradd NOME \
  -f /etc/pure-ftpd/passwd/pureftpd.passwd \
  -m -u ftpuser -d /home/ftpusers/NOME
```

O comando solicitará a senha sem armazená-la no histórico do shell.

## Verificação dos volumes compartilhados

```bash
docker exec streamora-application sh -c \
  'test -d /data/pureftpd && test -d /data/liquidsoap'
```

Para encerrar o ambiente sem remover os volumes persistentes:

```bash
docker compose down
```
