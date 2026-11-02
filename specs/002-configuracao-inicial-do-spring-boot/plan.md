# Plano de implementação — Spec 002

## Objetivo

Configurar a aplicação Spring Boot para acessar o MySQL e executar migrações com Flyway, mantendo as credenciais de desenvolvimento no arquivo local `.env` e documentando as mesmas chaves, sem dados sensíveis, no `.env.example`.

## Premissas

- As dependências necessárias para JPA, MySQL e Flyway já estão declaradas no `pom.xml`; nenhuma nova dependência ou alteração de versão será feita.
- A aplicação usará as variáveis `MYSQL_DATABASE`, `MYSQL_USER` e `MYSQL_PASSWORD`, já adotadas pelo ambiente Docker da Spec 001.
- A URL JDBC será parametrizada para funcionar tanto no Docker quanto na execução local, por meio de variáveis específicas de host e porta com valores adequados a cada ambiente.
- O `.env` conterá somente valores de desenvolvimento e permanecerá fora do versionamento.
- O `.env.example` continuará documentando todas as variáveis do ambiente, mas credenciais serão apresentadas sem valores reais.
- O Flyway usará o diretório convencional `classpath:db/migration`; não será criada uma migração de domínio porque a Spec 002 solicita apenas a configuração inicial.

## Etapas

1. Criar `tasks.md` com as tarefas de implementação e validação derivadas deste plano.
2. Atualizar `src/main/resources/application.properties` com a configuração do datasource MySQL, incluindo URL, usuário, senha e classe do driver, usando placeholders de variáveis de ambiente.
3. Configurar o Flyway no `application.properties`, habilitando-o e definindo o local convencional das migrações.
4. Criar o `.env` com valores locais de desenvolvimento para as variáveis necessárias ao banco de dados.
5. Atualizar o `.env.example` para refletir as novas chaves sem expor credenciais.
6. Atualizar o `.gitignore` para impedir o versionamento do `.env`, preservando o `.env.example`.
7. Adicionar testes de configuração que validem a resolução das propriedades do datasource e do Flyway sem exigir conexão real com o MySQL.

## Validação

- Executar os testes existentes e os novos testes com `./mvnw test`.
- Compilar e empacotar a aplicação com `./mvnw package`.
- Verificar que as propriedades de datasource apontam para o driver MySQL e resolvem os valores fornecidos por variáveis de ambiente/propriedades de teste.
- Verificar que o Flyway está habilitado e usa `classpath:db/migration`.
- Confirmar que `.env` está ignorado pelo Git e que nenhuma credencial aparece nos arquivos versionados.
- Conferir todos os critérios de aceite da `spec.md` e registrar qualquer limitação encontrada.

## Critérios de conclusão

- A configuração do MySQL está presente no `application.properties` e usa variáveis externas para dados de acesso.
- O Flyway está configurado e habilitado.
- O `.env` existe com valores locais de desenvolvimento e não é rastreável pelo Git.
- O `.env.example` contém as chaves necessárias sem credenciais reais.
- Os testes e a compilação terminam com sucesso, ou eventuais limitações ficam documentadas.
- A implementação é registrada em um commit ao final da validação.
