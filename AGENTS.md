# AGENTS.md

## Sobre o projeto
O streammora é idealizado para gerenciamento de web-rádio. O projeto foi idealizado para ser um auto-dj que manterá um fluxo de áudio continuo mesmo sem nenhuma transmissão em tempo real.

## Tecnologias
- Linguagem: Java 25
- Framework: Spring Boot
- Banco de dados: Mysql
- Outras tecnologias: Liquidsoap, Icecast    

## Estrutura do projeto

```text
streamora/
├── src/                         # Código e recursos da aplicação Spring Boot
│   ├── main/
│   │   ├── java/com/streamora/streamora/
│   │   └── resources/
│   └── test/                    # Testes da aplicação
├── infrastructure/              # Configurações de aplicações e serviços externos
│   ├── liquidsoap/
│   │   └── radio.liq
│   ├── icecast/                 # Configurações do Icecast, quando necessárias
│   └── pureftpd/                # Configurações do Pure-FTPd, quando necessárias
├── specs/                        # Especificações, planos, tarefas e revisões
├── Dockerfile                    # Build da aplicação Spring Boot
├── docker-compose.yml            # Orquestração dos serviços
├── .env.example                  # Modelo das variáveis de ambiente
└── pom.xml                       # Configuração Maven da aplicação
```

- O código da aplicação principal deve permanecer em `src/`.
- Pacotes `service` dentro de `src/main/java` representam serviços e regras da aplicação Spring.
- Aplicações externas e seus arquivos de configuração devem ficar em `infrastructure/{nome-do-serviço}/`.
- Arquivos de build e orquestração Docker compartilhados pelo projeto devem permanecer na raiz.
- Dados persistentes e informações sensíveis não devem ser versionados; devem utilizar volumes Docker e variáveis do `.env`.

## Fluxo de desenvolvimento
1. Leia a especificação antes de iniciar.
2. Crie um plano de implementação.
3. Aguarde aprovação do plano.
4. Crie as tarefas e testes necessárias.
5. Valide a implementação antes de finalizar.

## Regras
- Siga os padrões existentes no projeto.
- Não adicione novas dependências sem necessidade.
- Não altere versões das dependências ou linguagens sem autorização
- Não faça alterações fora do escopo de trabalho solicitado.
- Não remova funcionalidades existentes sem autorização.
- Caso de ambiguidade relevante, pergunte antes de decidir.
- Todos os dados sensíveis como tokens e senhas devem ficar no arquivo .env
- O arquivo .env deve ter um modelo exemplo chamado de .env.example

## Código
- Mantenha o código simples e legível.
- Evite duplicação, use o que já existe.
- Utilize nomes claros para classes, métodos, variáveis e arquivos.
- Siga as convenções da linguagem e do framework.
- Não faça refatorações desnecessárias fora do escopo de trabalho solicitado.

## Testes e validação
Antes de finalizar:
- Execute os testes existentes.
- Verifique se o projeto compila.
- Verifique os critérios de aceite.
- Informe erros ou limitações encontradas.

## Specs
As especificações ficam em:

`specs/XXX-nome-da-spec/`

Cada spec pode conter:

- `spec.md` — requisitos
- `plan.md` — plano de implementação
- `tasks.md` — tarefas
- `review.md` — revisão, quando necessário

## Finalização
Ao concluir uma implementação:
- Informe o que foi feito.
- Informe quais arquivos foram alterados.
- Informe os testes executados.
- Informe qualquer pendência encontrada.
- Altere o status e informe a branch que a tarefa foi realizada nos campos da spec
- Crie um commit
