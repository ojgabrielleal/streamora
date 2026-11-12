# Padrões de código

Este documento registra as convenções de escrita e formatação adotadas no
Streamora. As novas implementações devem seguir estes padrões e preservar a
consistência do código existente.

## Java

### Nomes de variáveis e parâmetros

Use nomes substantivos que identifiquem o domínio e, quando relevante, o papel
do valor no fluxo. Evite nomes genéricos como `changes`, `result`, `request`,
`response`, `file`, `data` ou `item` quando houver um nome mais específico.
Para DTOs, indique também a finalidade, como criação ou atualização.

Prefira `userCreateRequest`, `playlistUpdateRequest`, `createdUser` e
`playlistFileEntity`. Evite `createUser` para uma variável, pois esse nome
descreve uma ação e sugere um método. Nomes curtos continuam aceitáveis quando
forem termos próprios e inequívocos do domínio, como `uuid`.

### Operações com Stream

Em operações com Stream, mantenha `stream()` na linha da origem e quebre a
linha antes de cada operação encadeada. Cada operação deve usar exatamente um
nível adicional de recuo em relação ao `return`. O conteúdo de uma lambda ou
os argumentos de uma chamada quebrada devem usar mais um nível de recuo, sem
adicionar tabulações extras.

Correto:

```java
return playlistRepository.findAll().stream()
	.map(playlist -> PlaylistMapper.toResponse(playlist))
	.toList();
```

Evite:

```java
return playlistRepository.findAll().stream().map(
		playlist -> PlaylistMapper.toResponse(playlist)
).toList();
```

Quando a origem do encadeamento estiver em um método auxiliar, aplique o mesmo
padrão:

```java
return indexes(quantity)
	.mapToObj(index -> new PlaylistRequestDTO("Playlist " + index))
	.toList();
```

### Operações com Optional

Ao encadear uma operação em um `Optional` retornado por outra chamada, quebre
a linha antes da operação e use um nível adicional de recuo.

Correto:

```java
return playlistRepository.findByUuid(uuid)
	.orElseThrow(() -> new ResourceNotFoundException("Playlist", uuid));
```

Evite:

```java
return playlistRepository.findByUuid(uuid)
		.orElseThrow(() -> new ResourceNotFoundException("Playlist", uuid));
```

### Parênteses em múltiplas linhas

Em chamadas ou declarações com parênteses distribuídos em várias linhas, o
parêntese de fechamento deve ficar em uma linha própria.

Correto:

```java
private static final Map<Class<?>, Class<?>> REPOSITORIES = Map.of(
		UserRepository.class, User.class,
		PlaylistRepository.class, Playlist.class,
		ScheduleRepository.class, Schedule.class
);
```

Evite:

```java
private static final Map<Class<?>, Class<?>> REPOSITORIES = Map.of(
	UserRepository.class, User.class,
	PlaylistRepository.class, Playlist.class,
	ScheduleRepository.class, Schedule.class);
```

### Chave em declarações de métodos com parâmetros em múltiplas linhas

Quando a declaração de um método tiver os parâmetros distribuídos em mais de
uma linha, coloque a chave de abertura em uma nova linha, imediatamente depois
do parêntese de fechamento. Métodos cuja assinatura permanece em uma única
linha mantêm a chave de abertura na mesma linha.

Correto:

```java
public PlaylistResponseDTO update(
	String uuid,
	PlaylistRequestDTO playlistUpdateRequest
)
{
	return playlistService.update(uuid, playlistUpdateRequest);
}
```

Evite:

```java
public PlaylistResponseDTO update(
	String uuid,
	PlaylistRequestDTO playlistUpdateRequest
) {
	return playlistService.update(uuid, playlistUpdateRequest);
}
```

Essa regra se aplica a declarações de métodos e construtores. Ela não altera
o padrão de chamadas de métodos, estruturas condicionais ou records com corpo
vazio.

### Organização interna dos métodos

Organize o corpo dos métodos em blocos de responsabilidade separados por uma
linha em branco. Separe validações iniciais, preparação de dados, condicionais
independentes, execução da operação principal e retorno ou lançamento final
quando representarem etapas distintas do fluxo.

Mantenha juntas as instruções que fazem parte da mesma operação. Não adicione
linhas vazias entre atribuições relacionadas, entre uma condição e seu corpo ou
apenas para aumentar visualmente o método.

Separe por uma linha em branco as declarações locais sem inicialização do bloco
que atribuirá seus valores, como um `try` usado para capturar falhas da
inicialização. Quando houver várias declarações relacionadas, mantenha-as juntas
e insira uma única linha em branco antes do bloco operacional.

Aplique a mesma separação entre declarações locais já inicializadas e uma
estrutura de controle que consome esses valores, como `for`, `while`, `if` ou
`try`. Mantenha as declarações relacionadas no mesmo grupo e insira uma única
linha em branco antes da estrutura de controle.

```java
Path current = root;
Path relativePath = root.relativize(path);

for (Path segment : relativePath) {
	current = current.resolve(segment);
}
```

Correto:

```java
Path relativePath;

try {
	relativePath = Path.of(path);
} catch (InvalidPathException exception) {
	throw new FtpFileStorageException("Invalid FTP path", exception);
}
```

Evite:

```java
Path relativePath;
try {
	relativePath = Path.of(path);
} catch (InvalidPathException exception) {
	throw new FtpFileStorageException("Invalid FTP path", exception);
}
```

Quando condicionais independentes encerrarem o fluxo com `return` ou `throw`,
separe cada condicional da próxima etapa com uma linha em branco. Antes de um
`return` ou `throw` final, use uma linha em branco quando ele representar o
resultado residual depois das condições anteriores.

Correto:

```java
private FtpFileEntry readEntry(Path path) {
	rejectSymbolicLink(path);

	if (Files.isDirectory(path, NO_FOLLOW_LINKS)) {
		return new FtpFileEntry(
			path.getFileName().toString(),
			FtpFileType.DIRECTORY,
			readChildren(path)
		);
	}

	if (Files.isRegularFile(path, NO_FOLLOW_LINKS)) {
		return new FtpFileEntry(
			path.getFileName().toString(),
			FtpFileType.FILE,
			List.of()
		);
	}

	throw new FtpFileStorageException(
		"Unsupported item in FTP storage: " + path.getFileName()
	);
}
```

### Anotações OpenAPI em múltiplas linhas

Quando uma anotação `@ApiResponse` ficar longa, ela pode ser distribuída em
várias linhas. Mantenha cada atributo em sua própria linha e aumente o recuo a
cada anotação aninhada. O parêntese de fechamento de cada nível deve ficar em
uma linha própria, alinhado com o elemento que abriu aquele nível.

Correto:

```java
@ApiResponse(
	responseCode = "200",
	description = "Horários encontrados",
	content = @Content(
		array = @ArraySchema(
			schema = @Schema(implementation = ScheduleResponseDTO.class)
		)
	)
)
```

Quando a anotação couber com legibilidade em uma linha, mantenha a forma
compacta:

```java
@ApiResponse(responseCode = "200", description = "Horário encontrado")
```

### Parâmetros em métodos de controllers

Quando um parâmetro de controller possuir anotações do Swagger e do Spring,
declare primeiro a anotação de documentação do Swagger e depois a anotação
funcional do Spring. Mantenha todas as anotações imediatamente antes do
parâmetro ao qual pertencem.

Correto:

```java
public PlaylistResponseDTO update(
	@Parameter(description = "UUID da playlist")
	@PathVariable String uuid,
	@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Novos dados da playlist")
	@RequestBody PlaylistRequestDTO playlistRequest
)
{
	return playlistService.update(uuid, playlistRequest);
}
```

Evite:

```java
public PlaylistResponseDTO update(
	@PathVariable String uuid,
	@Parameter(description = "UUID da playlist")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Novos dados da playlist")
	@RequestBody PlaylistRequestDTO playlistRequest
)
{
	return playlistService.update(uuid, playlistRequest);
}
```

Quando um método de controller tiver exatamente dois parâmetros sem anotações de
documentação em linhas próprias, mantenha toda a assinatura na mesma linha.

Correto:

```java
public ScheduleResponseDTO update(@PathVariable String uuid, @RequestBody ScheduleRequestDTO scheduleRequest) {
	return scheduleService.update(uuid, scheduleRequest);
}
```

Evite:

```java
public ScheduleResponseDTO update(
	@PathVariable String uuid,
	@RequestBody ScheduleRequestDTO scheduleRequest
) {
	return scheduleService.update(uuid, scheduleRequest);
}
```

### DTOs com records

Os DTOs devem ser declarados como records, e o nome da classe e do arquivo deve
terminar com o sufixo `DTO`. Cada componente deve ficar em sua própria linha,
com uma tabulação de recuo, inclusive quando houver apenas um componente. Como
o corpo do DTO é vazio, as chaves devem permanecer na mesma linha do parêntese
de fechamento.

Correto:

```java
public record UserRequestDTO(
	String fullname,
	String nickname,
	String username,
	String password
) {}
```

Evite:

```java
public record UserRequestDTO(
		String fullname,
		String nickname,
		String username,
		String password
) {
}
```

### Argumentos nos mappers

Ao criar um DTO dentro de um mapper, cada argumento deve ficar em sua própria
linha e usar uma tabulação de recuo em relação ao `return`.

Correto:

```java
public static UserResponseDTO toResponse(User user) {
	return new UserResponseDTO(
		user.getUuid(),
		user.getFullname(),
		user.getNickname(),
		user.getUsername()
	);
}
```

### Organização dos testes

Organize o corpo de cada teste em blocos de Arrange, Act e Assert separados por
uma linha em branco, sem a necessidade de comentários para nomear as etapas.
Quando o Arrange tiver responsabilidades diferentes, separe também os
dados-base, a preparação específica do cenário e a configuração de mocks. Não
use linhas em branco entre instruções que pertençam ao mesmo bloco e não deixe
espaços ou tabulações em linhas vazias.

Dentro de cada teste, agrupe as instruções por finalidade e separe cada grupo
com uma linha em branco: primeiro os dados do cenário, depois todas as
configurações `when`, em seguida a execução, depois as asserções `assertThat` e,
por último, todas as verificações `verify`. Não insira linhas em branco entre
instruções consecutivas do mesmo grupo. Não execute o método principal sob
teste diretamente dentro de uma asserção; armazene seu retorno em uma variável
específica no bloco de execução. Testes de exceção com `assertThatThrownBy`
constituem a exceção, pois a chamada precisa permanecer dentro da função
executável avaliada pela asserção.

Nas asserções com `isSameAs(...)` ou `containsExactly(...)`, mantenha
`assertThat(...)` em uma linha e inicie a operação de verificação na linha
seguinte, com um nível adicional de recuo.

```java
ScheduleResponseDTO returnedSchedule = scheduleController.show(uuid);

assertThat(returnedSchedule)
	.isSameAs(expectedSchedule);
```

```java
List<ScheduleResponseDTO> returnedSchedules = scheduleController.index();

assertThat(returnedSchedules)
	.containsExactly(expectedSchedule);
```

Nos testes de controllers, mantenha os DTOs individuais juntos e separe por
uma linha em branco a coleção derivada deles. Separe também uma chamada sem
retorno, como `delete`, das asserções de retorno executadas antes dela. As
verificações com `verify` permanecem em um bloco próprio ao final.

Crie testes independentes por método público da classe sob teste, seja ela um
controller, service, mapper, factory ou outro componente. Não agrupe métodos
CRUD, associações ou operações diferentes no mesmo teste. Cada cenário deve
configurar, executar e verificar somente um método principal. Sobrecargas podem
ter cenários separados quando possuírem contratos ou comportamentos distintos.

Um teste pode conter várias asserções e chamar métodos auxiliares de factories
ou dos objetos retornados, desde que todas as verificações pertençam ao
comportamento do único método principal sob teste.

Correto:

```java
@Test
void updatesNameAndM3uFileNameAndPreservesIdentity() {
	Playlist storedPlaylist = PlaylistFactory.toEntity();

	PlaylistRequestDTO playlistUpdateRequest = PlaylistFactory.toCreate(2).getLast();
	Long originalId = storedPlaylist.getId();

	when(playlistRepository.findByUuid(storedPlaylist.getUuid()))
		.thenReturn(Optional.of(storedPlaylist));
	when(playlistRepository.save(storedPlaylist))
		.thenReturn(storedPlaylist);

	PlaylistResponseDTO updatedPlaylist = playlistService.update(
		storedPlaylist.getUuid(),
		playlistUpdateRequest
	);

	assertThat(updatedPlaylist.uuid()).isEqualTo(storedPlaylist.getUuid());
	assertThat(updatedPlaylist.name()).isEqualTo(playlistUpdateRequest.name());
	assertThat(updatedPlaylist.m3uFileName()).isEqualTo(playlistUpdateRequest.m3uFileName());
}
```

### Configuração de mocks com Mockito

Em testes unitários com `MockitoExtension`, declare as dependências da classe
sob teste com `@Mock` e a própria classe sob teste com `@InjectMocks`. Prefira
essa injeção pelo Mockito em vez de instanciar manualmente a classe em um
método `@BeforeEach`. Não use `@RequiredArgsConstructor` na classe de teste para
essa finalidade.

```java
@Mock
private PlaylistRepository playlistRepository;

@InjectMocks
private PlaylistService playlistService;
```

Nas configurações de mocks, mantenha `when(...)` em uma linha e inicie
`thenReturn(...)`, `thenAnswer(...)` ou outra operação equivalente na linha
seguinte, com um nível adicional de recuo. Quando houver mais de uma
configuração, mantenha todos os conjuntos `when`/`then` no mesmo bloco, sem
linhas em branco entre eles.

Correto:

```java
when(playlistRepository.findByUuid(playlist.getUuid()))
	.thenReturn(Optional.of(playlist));
when(playlistRepository.save(playlist))
	.thenReturn(playlist);
```

Evite:

```java
when(playlistRepository.findByUuid(playlist.getUuid())).thenReturn(Optional.of(playlist));
```
