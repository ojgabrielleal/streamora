# Spec 032 - Criando tela de arquivos

**Status:** Concluída

## Objetivo

Criar uma tela autenticada de Arquivos que apresente os dados de conexão FTP do usuário e permita gerenciar, pelo navegador, os arquivos e diretórios do storage compartilhado da rádio.

## Contexto

O dashboard já apresenta Arquivos na navegação lateral, mas o item está desabilitado e marcado como futuro. O backend possui operações para listar, criar diretório, renomear, mover e excluir itens do storage FTP compartilhado, porém o frontend ainda não oferece acesso a elas. Também faltam os dados públicos de conexão e metadados úteis para a interface. As transferências de arquivos continuarão sendo realizadas por um cliente FTP.

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

- Exibir um cartão de conexão FTP com host público e porta.
- Informar que o FTP utiliza o mesmo usuário e senha de acesso ao painel, sem retornar, persistir no frontend ou tentar revelar a senha atual.
- Disponibilizar ações acessíveis para copiar host e porta, com confirmação visual e anunciada após a cópia.
- Carregar os dados de conexão por uma API autenticada, sem fixar host, porta ou usuário no código da interface.
- Exibir estados próprios de carregamento e erro sem impedir o uso do gerenciador quando apenas os dados de conexão falharem.

### Gerenciador de arquivos

- Exibir o conteúdo do storage FTP compartilhado entre os usuários do painel.
- Apresentar o diretório atual em uma trilha de navegação, permitindo voltar à raiz ou a qualquer diretório ancestral.
- Listar diretórios antes de arquivos e ordenar cada grupo alfabeticamente.
- Exibir nome, tipo, formato, tamanho e data da última alteração de cada item.
- Permitir abrir diretórios sem recarregar a página.
- Permitir filtrar os itens do diretório atual pelo nome.
- Permitir criar diretórios dentro do diretório atual.
- Permitir renomear arquivos e diretórios.
- Permitir mover arquivos e diretórios para outro diretório do storage, impedindo destinos inválidos, como mover um diretório para dentro dele mesmo.
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

- Manter o storage compartilhado e preservar a raiz FTP única utilizada pelas operações existentes.
- Identificar o usuário por meio da autenticação vigente somente para preencher o username retornado nos dados de conexão.
- Disponibilizar host público, porta e username FTP em um endpoint autenticado específico, sem retornar senha ou hash de senha.
- Usar no username FTP o mesmo username empregado pelo usuário para acessar o painel.
- Preservar a listagem recursiva em árvore e ampliar o contrato dos itens do storage com formato, tamanho em bytes e data da última alteração.
- Representar o formato pela extensão do arquivo sem o ponto; diretórios e arquivos sem extensão devem retornar formato nulo.
- Retornar tamanho nulo para diretórios e tamanho em bytes para arquivos.
- Retornar a última alteração em formato de data e hora não ambíguo, com fuso ou offset explícito.
- Resolver todas as operações dentro da raiz compartilhada e rejeitar travessia de diretórios, caminhos absolutos e links simbólicos.
- Preservar as validações atuais de nomes, destinos existentes e exclusão apenas de diretórios vazios.
- Não incluir senha ou hash de senha em respostas, logs ou mensagens de erro.

## Regras

- A raiz compartilhada apresentada na tela não pode ser renomeada, movida ou excluída.
- Caminhos enviados nas operações de gerenciamento são sempre relativos à raiz compartilhada do storage.
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

- [x] Um usuário autenticado acessa `/arquivos` pelo item Arquivos da sidebar em desktop e mobile.
- [x] A sidebar destaca corretamente Dashboard ou Arquivos conforme a rota ativa e mantém os demais itens futuros inalterados.
- [x] A página apresenta host e porta FTP obtidos da API e informa que usuário e senha são os mesmos do painel, sem expor segredo.
- [x] Host e porta podem ser copiados com retorno visual e acessível.
- [x] A listagem apresenta o storage compartilhado, com navegação por diretórios, filtro, tipo, formato, tamanho e última alteração.
- [x] O usuário consegue criar diretórios e renomear, mover e excluir itens respeitando as validações definidas.
- [x] Operações concluídas atualizam a listagem e operações com falha exibem mensagem segura com possibilidade de nova tentativa.
- [x] Carregamento, diretório vazio, filtro sem resultado, erro e ações em andamento possuem estados distintos e acessíveis.
- [x] Tentativas de acessar caminhos absolutos, travessias ou links simbólicos são rejeitadas pelo backend.
- [x] A página é utilizável por teclado e se adapta a desktop e dispositivos móveis.
- [x] Testes automatizados cobrem contratos, limites da raiz compartilhada, operações principais, navegação e estados relevantes da interface.
