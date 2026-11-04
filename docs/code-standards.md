# Padrões de código

Este documento registra as convenções de escrita e formatação adotadas no
Streamora. As novas implementações devem seguir estes padrões e preservar a
consistência do código existente.

## Java

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
