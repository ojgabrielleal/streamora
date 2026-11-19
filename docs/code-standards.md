# Padrões de código

Consulte este documento ao alterar código. Preserve o padrão do arquivo e
aplique estas regras somente ao trecho modificado, sem refatorações paralelas.

## Princípios gerais

- Mantenha o código simples, legível e sem duplicação.
- Use nomes que expressem domínio e finalidade. Evite nomes genéricos como
  `data`, `item`, `result`, `request` ou `response` quando houver opção precisa.
- Use Lombok quando reduzir repetição sem ocultar regras de negócio.

## Frontend

- Estados de `hover` e foco devem ser discretos, coerentes com a identidade
  visual e menos proeminentes que a ação principal.
- Nunca remova a indicação de foco. Garanta contraste para navegação por teclado
  e reserve maior destaque para ações primárias e erros.
- Mantenha testes próximos dos componentes, rotas ou módulos testados.

## Java

### Formatação

- Use tabulação para recuo, conforme o código existente.
- Em chamadas, declarações e anotações com múltiplas linhas, coloque cada
  argumento ou atributo em sua linha e o parêntese final em linha própria.
- Em métodos ou construtores com parâmetros em múltiplas linhas, coloque `{` na
  linha seguinte ao parêntese final. Em assinaturas de uma linha, mantenha `{`
  na mesma linha.
- Em cadeias de `Stream` e `Optional`, mantenha a origem na primeira linha e
  inicie cada operação encadeada na linha seguinte, com um nível de recuo.
- Separe por uma linha em branco os blocos de responsabilidade de um método:
  preparação, validação, controle de fluxo, execução e retorno. Não separe
  instruções da mesma operação.

### Contratos e controllers

- Declare DTOs como `record`, com sufixo `DTO` e um componente por linha. Para
  corpo vazio, use `) {}`.
- Em mappers, coloque cada argumento do DTO retornado em sua própria linha.
- Nos parâmetros de controllers, coloque anotações Swagger antes das anotações
  funcionais do Spring e imediatamente antes do parâmetro correspondente.
- Se um controller tiver exatamente dois parâmetros e nenhuma anotação de
  documentação ocupar linha própria, mantenha a assinatura em uma linha.
- Anotações OpenAPI curtas ficam em uma linha; nas longas, use um atributo por
  linha e recue anotações aninhadas por nível.

## Testes Java

- Crie testes independentes para cada método público e cenário. Cada teste deve
  executar somente um método principal da classe testada.
- Organize cada teste em Arrange, Act e Assert, separados por uma linha em
  branco e sem comentários para nomear as etapas.
- Agrupe, nesta ordem: dados do cenário, configurações de mocks, execução,
  asserções e verificações `verify`; não separe instruções do mesmo grupo.
- Armazene o retorno da execução em uma variável específica antes de fazer
  asserções. `assertThatThrownBy` é a exceção.
- Em asserções encadeadas com `isSameAs` ou `containsExactly`, deixe
  `assertThat(...)` na primeira linha e a verificação na seguinte.
- Com `MockitoExtension`, use `@Mock` nas dependências e `@InjectMocks` na classe
  testada; não faça essa montagem em `@BeforeEach` nem use
  `@RequiredArgsConstructor` na classe de teste.
- Em mocks, deixe `when(...)` na primeira linha e `thenReturn`, `thenAnswer` ou
  equivalente na seguinte. Mantenha todas as configurações no mesmo bloco.
- Não deixe espaços ou tabulações em linhas vazias.
