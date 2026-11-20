# Spec 032 - Criando tela de arquivos

## Objetivo

Criar uma tela autenticada de Arquivos que apresente os dados de conexão FTP do usuário e permita gerenciar, pelo navegador, os arquivos e diretórios da sua própria área de armazenamento.

## Contexto

O dashboard já apresenta Arquivos na navegação lateral, mas o item está desabilitado e marcado como futuro. O backend possui operações para listar, criar diretório, renomear, mover e excluir itens do storage FTP, porém o frontend ainda não oferece acesso a elas. Também faltam metadados úteis para a interface e o isolamento explícito das operações pelo usuário autenticado. As transferências de arquivos continuarão sendo realizadas por um cliente FTP.

## Requisitos

### Navegação e estrutura da página

- Disponibilizar a rota autenticada `/arquivos`.
- Transformar o item Arquivos da sidebar em um link funcional para a nova rota e remover dele a indicação `Em breve`.
- Manter Dashboard, Playlists e Programação com seus comportamentos atuais.
- Indicar visualmente Arquivos como a opção ativa somente na rota correspondente.
- Reutilizar a sidebar, o cabeçalho responsivo, a identidade violeta e fúcsia, os cartões e os padrões de foco presentes no dashboard.
- Exibir no cabeçalho o título `Arquivos` e uma descrição curta sobre o gerenciamento da biblioteca da rádio.
- Manter o menu móvel com abertura, fechamento, navegação e logout funcionais.

### Dados de acesso FTP

- Exibir um cartão de conexão FTP com host público, porta e nome de usuário do usuário autenticado.
- Exibir o texto `Mesmo acesso do painel` no campo de senha, sem retornar, persistir no frontend ou tentar revelar a senha atual.
- Disponibilizar ações acessíveis para copiar host, porta e usuário, com confirmação visual e anunciada após a cópia.
- Carregar os dados de conexão por uma API autenticada, sem fixar host, porta ou usuário no código da interface.
- Exibir estados próprios de carregamento e erro sem impedir o uso do gerenciador quando apenas os dados de conexão falharem.

### Gerenciador de arquivos

- Exibir somente o conteúdo da área FTP pertencente ao usuário autenticado.
- Apresentar o diretório atual em uma trilha de navegação, permitindo voltar à raiz ou a qualquer diretório ancestral.
- Listar diretórios antes de arquivos e ordenar cada grupo alfabeticamente.
- Exibir nome, tipo, tamanho e data da última alteração de cada item.
- Permitir abrir diretórios sem recarregar a página.
- Permitir filtrar os itens do diretório atual pelo nome.
- Permitir criar diretórios dentro do diretório atual.
- Permitir renomear arquivos e diretórios.
- Permitir mover arquivos e diretórios para outro diretório da área do usuário, impedindo destinos inválidos, como mover um diretório para dentro dele mesmo.
- Permitir excluir arquivos e diretórios vazios somente após confirmação que identifique claramente o item afetado.
- Atualizar os dados exibidos após cada mutação concluída sem exigir recarregamento manual da página.
- Desabilitar ações em andamento para evitar envios duplicados.
- Apresentar menus, diálogos e mensagens de sucesso ou erro acessíveis por teclado e tecnologias assistivas.

### Estados da interface

- Exibir um estado de carregamento enquanto o diretório é consultado.
- Exibir um estado vazio com orientação para conectar por FTP ou criar uma pasta.
- Distinguir ausência de resultados do filtro de um diretório vazio.
- Exibir falhas de consulta e de mutação em linguagem segura, preservando a possibilidade de tentar novamente.
- Adaptar cartões, tabela ou lista, barra de ações e diálogos para desktop e dispositivos móveis sem rolagem horizontal obrigatória da página.

### Backend e segurança

- Identificar o usuário por meio da autenticação vigente, sem aceitar nome de usuário arbitrário enviado pelo frontend para definir a área de armazenamento.
- Resolver todas as operações dentro da pasta FTP do usuário autenticado e rejeitar travessia de diretórios, caminhos absolutos e links simbólicos.
- Impedir que listagem, criação, renomeação, movimentação ou exclusão alcance arquivos de outro usuário.
- Disponibilizar host público, porta e usuário em um contrato autenticado específico para os dados de conexão FTP.
- Ampliar o contrato dos itens do storage com caminho relativo, tamanho em bytes e data da última alteração; tamanho poderá ser nulo para diretórios.
- Preservar as validações atuais de nomes, destinos existentes e exclusão apenas de diretórios vazios.
- Não incluir senha ou hash de senha em respostas, logs ou mensagens de erro.

## Regras

- A raiz apresentada na tela corresponde à pasta FTP do usuário autenticado e não pode ser renomeada, movida ou excluída.
- Caminhos enviados à API são sempre relativos à raiz do usuário autenticado.
- Nomes vazios, `.` e `..`, separadores de caminho e destinos fora da raiz são inválidos.
- A interface deve preservar autenticação, renovação de sessão e logout já existentes.
- A página deve utilizar as dependências já instaladas no projeto.

## Fora do escopo

- Alterar a senha FTP pela tela de Arquivos.
- Recuperar ou revelar a senha atual do usuário.
- Enviar ou baixar arquivos pelo painel; transferências continuam sendo realizadas por um cliente FTP.
- Editar o conteúdo de arquivos no navegador.
- Reproduzir ou visualizar mídia no navegador.
- Compactar, descompactar ou compartilhar arquivos por link público.
- Exibir cota, espaço livre ou consumo total do disco.
- Excluir diretórios não vazios de forma recursiva.
- Implementar as telas de Playlists ou Programação.

## Critérios de aceite

- [ ] Um usuário autenticado acessa `/arquivos` pelo item Arquivos da sidebar em desktop e mobile.
- [ ] A sidebar destaca corretamente Dashboard ou Arquivos conforme a rota ativa e mantém os demais itens futuros inalterados.
- [ ] A página apresenta host, porta e usuário FTP obtidos da API e exibe `Mesmo acesso do painel` como senha, sem expor segredo.
- [ ] Host, porta e usuário podem ser copiados com retorno visual e acessível.
- [ ] A listagem apresenta somente a área do usuário autenticado, com navegação por diretórios, filtro, tipo, tamanho e última alteração.
- [ ] O usuário consegue criar diretórios e renomear, mover e excluir itens respeitando as validações definidas.
- [ ] Operações concluídas atualizam a listagem e operações com falha exibem mensagem segura com possibilidade de nova tentativa.
- [ ] Carregamento, diretório vazio, filtro sem resultado, erro e ações em andamento possuem estados distintos e acessíveis.
- [ ] Tentativas de acessar caminhos absolutos, travessias, links simbólicos ou áreas de outro usuário são rejeitadas pelo backend.
- [ ] A página é utilizável por teclado e se adapta a desktop e dispositivos móveis.
- [ ] Testes automatizados cobrem contratos, isolamento do storage, operações principais, navegação e estados relevantes da interface.
