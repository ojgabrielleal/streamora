<a id="readme-top"></a>

<div align="center">

  <img src="front-end/public/images/brand/logo.png" alt="Streamora" width="360" />

Plataforma completa para gerenciamento e transmissão de web-rádio com **Spring Boot**, **React**, **MySQL**, **Icecast**, **Liquidsoap** e **Pure-FTPd**.

<p>
  <a href="docs/README.md"><strong>Explorar a documentação »</strong></a>
</p>

![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.2-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![TypeScript](https://img.shields.io/badge/TypeScript-7-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.4-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Docker Compose](https://img.shields.io/badge/Docker%20Compose-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)

</div>

---

<details>
  <summary>Índice</summary>
  <ol>
    <li>
      <a href="#sobre-o-projeto">Sobre o Projeto</a>
      <ul>
        <li><a href="#tecnologias-utilizadas">Tecnologias Utilizadas</a></li>
      </ul>
    </li>
    <li>
      <a href="#começando">Começando</a>
      <ul>
        <li><a href="#pré-requisitos">Pré-requisitos</a></li>
        <li><a href="#instalação-e-execução">Instalação e Execução</a></li>
      </ul>
    </li>
    <li>
      <a href="#uso">Uso</a>
      <ul>
        <li><a href="#serviços-expostos">Serviços Expostos</a></li>
        <li><a href="#volumes-e-pure-ftpd">Volumes e Pure-FTPd</a></li>
        <li><a href="#testes">Testes</a></li>
      </ul>
    </li>
    <li><a href="#documentação-interna">Documentação Interna</a></li>
    <li><a href="#contribuição">Contribuição</a></li>
  </ol>
</details>

---

## Sobre o Projeto

O **Streamora** centraliza a operação de uma web-rádio em um único ambiente containerizado:

- **Painel Web**: interface para gerenciamento da rádio, player integrado e acompanhamento do status da transmissão em tempo real.
- **Playlists e Programação**: geração e controle de arquivos `.m3u` consumidos automaticamente pelo Liquidsoap.
- **Transmissão ao Vivo e AutoDJ**: entrada ao vivo via Harbor do Liquidsoap (porta `8005`, mount `/live`) com fallback automático para a programação.
- **Upload de Áudios via FTP**: integração com Pure-FTPd com sincronização automática de usuários virtuais, criação/atualização de credenciais via entrada padrão (`pure-pw`) e isolamento de diretórios.
- **API Documentada**: controle de acesso com JWT e documentação interativa via OpenAPI / Swagger UI.

### Tecnologias Utilizadas

| Camada | Tecnologias |
| --- | --- |
| **Backend** | Java 25, Spring Boot 4.1.2, Spring Security, Spring Data JPA, Flyway, JWT, Springdoc OpenAPI |
| **Frontend** | React 19, TypeScript, Vite, Tailwind CSS, TanStack Router, TanStack Query |
| **Streaming** | Liquidsoap 2.3.3, Icecast |
| **Banco & Arquivos** | MySQL 8.4, PHPMyAdmin 5.2, Pure-FTPd |
| **Infraestrutura** | Docker & Docker Compose |

<p align="right">(<a href="#readme-top">voltar ao topo</a>)</p>

---

## Começando

Siga os passos abaixo para subir o ambiente localmente com Docker.

### Pré-requisitos

- [Docker](https://docs.docker.com/get-docker/) com o plugin **Docker Compose**.

### Instalação e Execução

1. Crie o arquivo local de variáveis de ambiente a partir do modelo e preencha os valores necessários:

   ```bash
   cp .env.example .env
   ```

2. Valide a configuração, compile a aplicação e inicie os containers:

   ```bash
   docker compose config
   docker compose build application
   docker compose up -d
   docker compose ps
   ```

   O `Dockerfile` compila o backend com Maven Wrapper e JDK 25 em um estágio de build e executa o JAR gerado em uma imagem com JRE 25.

<p align="right">(<a href="#readme-top">voltar ao topo</a>)</p>

---

## Uso

### Serviços Expostos

| Serviço | Porta padrão |
| --- | --- |
| Frontend (Vite) | `5173` |
| Backend (API / Swagger) | `8080` |
| Icecast (Stream) | `8000` |
| Liquidsoap Harbor (Ao vivo) | `8005` (`/live`) |
| PHPMyAdmin | `8081` |
| MySQL | `3306` |
| Pure-FTPd | `21` (passivas `30000-30009`) |

### Volumes e Pure-FTPd

- **Áudios (`pureftpd_uploads`)**: montado em `/pure-ftpd/uploads` na aplicação, no Pure-FTPd e no Liquidsoap (somente leitura no Liquidsoap).
- **Playlists (`liquidsoap_m3u`)**: montado em `/liquidsoap/m3u` na aplicação e no Liquidsoap (somente leitura no Liquidsoap).
- **Configuração (`./liquidsoap`)**: disponível para escrita na aplicação em `/liquidsoap/config` e para leitura no Liquidsoap em `/etc/liquidsoap`.
- **Credenciais FTP (`pureftpd_passwords`)**: compartilhado entre `application` e `pureftpd`. Os valores `PUREFTPD_UID` e `PUREFTPD_GID` devem ser iguais nos dois serviços (padrão `1000`) para manter as permissões corretas dos diretórios.
- **Usuários FTP**: o `username` é definido no cadastro e não pode ser alterado; nome completo e apelido podem ser editados sem afetar o acesso FTP.

Verificação rápida dos volumes compartilhados:

```bash
docker exec streamora-application sh -c \
  'test -d /liquidsoap/config && test -d /liquidsoap/m3u && test -d /pure-ftpd/uploads'
```

Para encerrar o ambiente preservando os volumes persistentes:

```bash
docker compose down
```

### Testes

- **Backend**:
  ```bash
  ./mvnw test
  ```
- **Frontend**:
  ```bash
  cd front-end && npm test
  ```

<p align="right">(<a href="#readme-top">voltar ao topo</a>)</p>

---

## Documentação Interna

Consulte a pasta [`docs/`](docs/README.md) para padrões de código, estrutura do projeto, banco de dados e especificações.

<p align="right">(<a href="#readme-top">voltar ao topo</a>)</p>

---

## Contribuição

Contribuições são bem-vindas:

1. Faça um **Fork** do projeto
2. Crie uma branch para sua feature (`git checkout -b feat/minha-feature`)
3. Faça o commit das alterações (`git commit -m 'feat: adiciona minha feature'`)
4. Envie para a branch (`git push origin feat/minha-feature`)
5. Abra um **Pull Request**

<p align="right">(<a href="#readme-top">voltar ao topo</a>)</p>
