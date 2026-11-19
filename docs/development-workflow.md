# Fluxo de desenvolvimento

## Projeto
Streamora gerencia web-rádios e funciona como auto-DJ, mantendo o áudio contínuo
mesmo sem transmissão ao vivo. Usa Java 25, Spring Boot, MySQL, Liquidsoap e
Icecast.

## Processo
1. Leia a spec e crie o plano.
2. Aguarde a aprovação do plano.
3. Crie as tarefas e os testes; implemente e valide.
4. Pergunte se são necessárias correções.
5. Após corrigi-las, peça autorização para finalizar a spec.

## Desenvolvimento
- Siga os padrões existentes e limite as mudanças ao escopo solicitado.
- Não remova funcionalidades nem altere versões sem autorização.
- Não adicione dependências desnecessárias; pergunte diante de ambiguidade relevante.
- Mantenha o código simples, legível, sem duplicação e com nomes claros.
- Siga `code-standards.md` em todo Java novo ou alterado. Corrija apenas as
  inconsistências presentes no trecho modificado, sem refatorações fora do escopo.
- Use Lombok quando reduzir repetição sem ocultar regras de negócio, conforme o
  padrão do projeto.
- Guarde tokens, senhas e demais dados sensíveis em `.env`, com modelo em
  `.env.example`.

## Validação e entrega
- Execute os testes existentes, compile o projeto e confira os critérios de aceite.
- Informe o que foi feito, arquivos alterados, testes executados, erros,
  limitações e pendências.
- Crie um commit.
