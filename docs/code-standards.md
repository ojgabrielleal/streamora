# Padrões de código

Este documento registra as convenções de escrita e formatação adotadas no
Streamora. As novas implementações devem seguir estes padrões e preservar a
consistência do código existente.

## Java

### Operações com Stream

Em operações com Stream, mantenha `stream()` na linha da origem e quebre a
linha antes de cada operação encadeada.

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
		PlaylistFileRepository.class, PlaylistFile.class
);
```

Evite:

```java
private static final Map<Class<?>, Class<?>> REPOSITORIES = Map.of(
		UserRepository.class, User.class,
		PlaylistRepository.class, Playlist.class,
		PlaylistFileRepository.class, PlaylistFile.class);
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
