# AGENTS.md

## Sobre o projeto
O streammora é idealizado para gerenciamento de web-rádio. O projeto foi idealizado para ser um auto-dj que manterá um fluxo de áudio continuo mesmo sem nenhuma transmissão em tempo real.

## Tecnologias
- Linguagem: Java 25
- Framework: Spring Boot
- Banco de dados: Mysql
- Outras tecnologias: Liquidsoap, Icecast    

## Estrutura do projeto
Consulte `docs/project-structure.md` para conhecer a estrutura atual, as
responsabilidades dos diretórios e a organização dos testes.

Ao criar, mover ou remover componentes estruturais, atualize também esse
documento.

## Fluxo de desenvolvimento
1. Leia a especificação antes de iniciar.
2. Crie um plano de implementação.
3. Aguarde aprovação do plano.
4. Crie as tarefas e testes necessárias.
5. Valide a implementação
7. Pergunte se a implementação precisa de correções
6. Após as correções, peça autorização para finalizar a spec

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

## Migrations
- Crie as migrations em arquivos `.sql` dentro de `src/main/resources/db/migration`.
- Siga o padrão de nomes do Flyway: `V{versão}__{descrição}.sql`.
- Cada migration deve alterar somente uma tabela.
- Separe obrigatoriamente em migrations diferentes as alterações em tabelas diferentes.
- Toda coluna `uuid` deve possuir restrição de unicidade.
- Atualize `docs/database-schema.md` sempre que uma migration modificar a estrutura do banco.

## Testes de migrations
- Mantenha os testes de migrations globais, verificando somente as convenções comuns às tabelas principais.
- Verifique apenas a presença dos campos `id` e `uuid` nas migrations de criação de tabelas principais.
- Não inclua tabelas associativas nessa verificação.
- Não crie testes para detalhes específicos de cada migration.

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
