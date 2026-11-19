# Spec 027 - Criando tela de login do frontend

**Status:** Concluída

## Objetivo

Substituir a interface provisória da rota `/` por uma tela de login própria do
Streamora, responsiva e acessível, utilizando os arquivos de marca já
disponíveis no frontend e preservando a integração existente com o endpoint de
autenticação.

## Requisitos

### Rota e estrutura

- Renderizar a tela de login ao acessar a rota `/` do frontend.
- Substituir o conteúdo provisório usado para validar a integração com a API.
- Manter a página dentro da estrutura atual do TanStack Router.
- Não criar uma nova rota para o login nesta spec.
- Organizar o formulário em um componente próprio quando isso mantiver a rota
  simples e facilitar seus testes.

### Identidade visual

- Usar uma das logos existentes em `front-end/public/images/brand` como marca
  principal da tela.
- Referenciar a imagem pelo caminho público do Vite, sem importar ou duplicar
  o arquivo dentro de `src`.
- Preservar a proporção da logo e fornecer texto alternativo adequado.
- Usar os tokens e componentes visuais já configurados no projeto.
- Apresentar um layout limpo, centralizado e responsivo para telas móveis e
  desktop.
- Não alterar os arquivos originais da marca.

### Formulário de autenticação

- Exibir campos identificados para usuário e senha.
- Usar tipos e atributos semânticos apropriados para os campos, incluindo
  preenchimento automático pelo navegador.
- Exigir o preenchimento de usuário e senha antes do envio.
- Exibir uma ação principal clara para entrar.
- Enviar as credenciais por meio da mutation de login já existente, sem
  realizar chamadas HTTP diretamente no componente visual.
- Desabilitar o botão enquanto a autenticação estiver em andamento e informar
  visualmente o estado de carregamento.
- Impedir envios duplicados durante uma tentativa em andamento.
- Não armazenar nem preencher credenciais no código-fonte.

### Estados e acessibilidade

- Exibir uma mensagem segura quando a autenticação falhar, sem revelar
  detalhes internos da API.
- Remover ou ocultar a mensagem de erro quando uma nova tentativa for
  iniciada.
- Associar os rótulos aos respectivos campos de forma acessível.
- Permitir o envio do formulário pelo teclado.
- Manter foco visível nos controles interativos.
- Expor mensagens de erro e carregamento de modo perceptível por tecnologias
  assistivas.

### Comportamento após o login

- Preservar o armazenamento em memória do access token realizado pela mutation
  atual.
- Não consultar playlists apenas para validar a autenticação.
- Exibir uma confirmação simples após o login bem-sucedido enquanto não houver
  uma rota autenticada de destino.
- Não implementar redirecionamento para dashboard nesta spec.

### Testes e documentação

- Criar testes da página ou do formulário cobrindo renderização, submissão,
  carregamento, sucesso e falha.
- Substituir a mutation real por um mock nos testes visuais.
- Garantir que lint, testes e build do frontend terminem com sucesso.
- Atualizar `docs/project-structure.md` caso seja criado um novo componente ou
  diretório estrutural.

## Regras

- Reutilizar `loginMutationOptions` e os contratos atuais do domínio
  `api/auth`.
- Não realizar chamadas HTTP diretamente na rota ou no formulário.
- Não adicionar dependências para implementar a tela.
- Não alterar o contrato de `POST /login` nem o backend.
- Não persistir access token, refresh token, usuário ou senha em local storage,
  session storage, cookies ou arquivos.
- Manter o código simples e alinhado aos padrões atuais do frontend.

## Fora do escopo

- Dashboard ou qualquer outra tela autenticada.
- Redirecionamento após a autenticação.
- Persistência da sessão após recarregar a página.
- Renovação, rotação ou revogação de tokens.
- Recuperação ou redefinição de senha.
- Cadastro de usuário.
- Login social ou autenticação multifator.
- Alterações na API, no Spring Security ou no CORS.
- Criação ou edição dos arquivos de marca.

## Critérios de aceite

- [x] A rota `/` exibe a tela de login do Streamora.
- [x] A tela utiliza uma logo de `front-end/public/images/brand` sem distorcê-la.
- [x] O layout funciona em telas móveis e desktop.
- [x] Usuário e senha possuem rótulos e atributos semânticos adequados.
- [x] O formulário usa a mutation de login existente.
- [x] O botão informa o carregamento e impede envios duplicados.
- [x] Uma falha exibe mensagem segura e acessível.
- [x] Um login bem-sucedido mantém o access token em memória e exibe uma
  confirmação simples.
- [x] A tela não consulta playlists após o login.
- [x] Nenhuma credencial ou token é persistido no navegador ou versionado.
- [x] Os testes automatizados da tela terminam com sucesso.
- [x] Os comandos de lint e build do frontend terminam com sucesso.
- [x] `docs/project-structure.md` reflete qualquer novo componente estrutural.
