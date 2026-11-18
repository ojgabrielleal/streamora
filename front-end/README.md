# Frontend do Streamora

Painel web do Streamora construído com React, TypeScript e Vite.

## Desenvolvimento local

Copie `.env.example` para `.env`, instale as dependências e inicie o Vite:

```bash
npm install
npm run dev
```

Também é possível iniciar o frontend pelo serviço `node` na raiz do projeto:

```bash
docker compose up node
```

## Scripts

- `npm run dev`: inicia o servidor de desenvolvimento.
- `npm run build`: verifica os tipos e gera o bundle de produção.
- `npm run lint`: executa a análise estática.
- `npm test`: executa os testes automatizados uma vez.

## Integração com a API

O endereço público da API é configurado por `VITE_API_URL`. A API também deve
permitir a origem do Vite por meio de `CORS_ALLOWED_ORIGINS`, configurada no
ambiente do backend.
