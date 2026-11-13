package com.streamora.streamora.infrastructure.pureFtpd.storage;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.streamora.streamora.infrastructure.pureFtpd.storage.exception.FtpFileStorageException;
import com.streamora.streamora.infrastructure.pureFtpd.storage.interfaces.FtpFileStorage;
import com.streamora.streamora.infrastructure.pureFtpd.storage.records.FtpFileEntry;
import com.streamora.streamora.infrastructure.pureFtpd.storage.records.FtpFileType;
import com.streamora.streamora.infrastructure.pureFtpd.user.records.PureFtpdProperties;

@Component
public class LocalFtpFileStorage implements FtpFileStorage {

	private static final LinkOption[] NO_FOLLOW_LINKS = { LinkOption.NOFOLLOW_LINKS };
	private static final Comparator<FtpFileEntry> ENTRY_ORDER = Comparator
		.comparing((FtpFileEntry entry) -> entry.type())
		.thenComparing(entry -> entry.name());

	private final Path root;

	public LocalFtpFileStorage(PureFtpdProperties properties) {
		try {
			root = Path.of(properties.homeDirectory()).toAbsolutePath().normalize();
		} catch (InvalidPathException | NullPointerException exception) {
			throw new FtpFileStorageException("Invalid FTP root directory", exception);
		}
	}

	@Override
	public List<FtpFileEntry> list() {
		Path storageRoot = requireRoot();

		return readChildren(storageRoot);
	}

	@Override
	public void deleteFile(String path) {
		Path ftpFile = requireFile(resolve(path, false));
		delete(ftpFile, "Unable to delete FTP file");
	}

	@Override
	public void renameFile(String path, String newName) {
		Path ftpFile = requireFile(resolve(path, false));
		move(ftpFile, ftpFile.resolveSibling(validateName(newName)), "Unable to rename FTP file");
	}

	@Override
	public void moveFile(String path, String targetDirectory) {
		Path ftpFile = requireFile(resolve(path, false));
		Path directory = requireDirectory(resolve(targetDirectory, true));
		move(ftpFile, directory.resolve(ftpFile.getFileName()), "Unable to move FTP file");
	}

	@Override
	public void createDirectory(String path) {
		Path directory = resolve(path, false);
		requireDirectory(directory.getParent());

		requireAvailable(directory);

		try {
			Files.createDirectory(directory);
		} catch (IOException exception) {
			throw new FtpFileStorageException("Unable to create FTP directory", exception);
		}
	}

	@Override
	public void renameDirectory(String path, String newName) {
		Path directory = requireDirectory(resolve(path, false));
		move(directory, directory.resolveSibling(validateName(newName)), "Unable to rename FTP directory");
	}

	@Override
	public void deleteDirectory(String path) {
		Path directory = requireDirectory(resolve(path, false));

		try {
			Files.delete(directory);
		} catch (DirectoryNotEmptyException exception) {
			throw new FtpFileStorageException("FTP directory must be empty", exception);
		} catch (IOException exception) {
			throw new FtpFileStorageException("Unable to delete FTP directory", exception);
		}
	}

	private List<FtpFileEntry> readChildren(Path directory) {
		List<FtpFileEntry> entries = new ArrayList<>();

		try (DirectoryStream<Path> children = Files.newDirectoryStream(directory)) {
			for (Path child : children) {
				entries.add(readEntry(child));
			}
		} catch (IOException exception) {
			throw new FtpFileStorageException("Unable to list FTP directory", exception);
		}

		entries.sort(ENTRY_ORDER);

		return List.copyOf(entries);
	}

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

	private Path resolve(String path, boolean allowRoot) {
		requireRoot();

		if (path == null || path.isBlank()) {
			if (allowRoot) {
				return root;
			}

			throw new FtpFileStorageException("FTP path must not be blank");
		}

		Path relativePath;

		try {
			relativePath = Path.of(path);
		} catch (InvalidPathException exception) {
			throw new FtpFileStorageException("Invalid FTP path", exception);
		}

		if (relativePath.isAbsolute()) {
			throw new FtpFileStorageException("FTP path must be relative to the configured root");
		}

		for (Path segment : relativePath) {
			if (segment.toString().equals("..")) {
				throw new FtpFileStorageException("FTP path must not contain parent traversal");
			}
		}

		Path resolved = root.resolve(relativePath).normalize();

		if (!resolved.startsWith(root)) {
			throw new FtpFileStorageException("FTP path resolves outside the configured root");
		}

		if (resolved.equals(root) && !allowRoot) {
			throw new FtpFileStorageException("The FTP root cannot be modified");
		}

		validateExistingAncestors(resolved);

		return resolved;
	}

	private Path requireRoot() {
		if (!Files.exists(root, NO_FOLLOW_LINKS) || !Files.isDirectory(root)) {
			throw new FtpFileStorageException("FTP root directory does not exist");
		}

		try {
			root.toRealPath();
		} catch (IOException exception) {
			throw new FtpFileStorageException("Unable to access FTP root directory", exception);
		}

		return root;
	}

	private void validateExistingAncestors(Path path) {
		Path current = root;
		Path relativePath = root.relativize(path);

		for (Path segment : relativePath) {
			current = current.resolve(segment);

			if (!Files.exists(current, NO_FOLLOW_LINKS)) {
				return;
			}

			rejectSymbolicLink(current);
		}
	}

	private void rejectSymbolicLink(Path path) {
		if (Files.isSymbolicLink(path)) {
			throw new FtpFileStorageException("Symbolic links are not allowed in FTP storage paths");
		}
	}

	private Path requireFile(Path path) {
		if (!Files.exists(path, NO_FOLLOW_LINKS)) {
			throw new FtpFileStorageException("FTP file does not exist");
		}

		rejectSymbolicLink(path);

		if (!Files.isRegularFile(path, NO_FOLLOW_LINKS)) {
			throw new FtpFileStorageException("FTP path is not a file");
		}

		return path;
	}

	private Path requireDirectory(Path path) {
		if (path == null || !path.startsWith(root) || !Files.exists(path, NO_FOLLOW_LINKS)) {
			throw new FtpFileStorageException("FTP directory does not exist");
		}

		validateExistingAncestors(path);
		rejectSymbolicLink(path);

		if (!Files.isDirectory(path, NO_FOLLOW_LINKS)) {
			throw new FtpFileStorageException("FTP path is not a directory");
		}

		return path;
	}

	private String validateName(String name) {
		if (name == null || name.isBlank()) {
			throw new FtpFileStorageException("FTP item name must not be blank");
		}

		Path namePath;

		try {
			namePath = Path.of(name);
		} catch (InvalidPathException exception) {
			throw new FtpFileStorageException("Invalid FTP item name", exception);
		}

		if (namePath.isAbsolute()
			|| namePath.getNameCount() != 1
			|| name.equals(".")
			|| name.equals("..")) {
			throw new FtpFileStorageException("FTP item name must not contain a path");
		}

		return name;
	}

	private void requireAvailable(Path path) {
		if (!path.startsWith(root)) {
			throw new FtpFileStorageException("FTP destination resolves outside the configured root");
		}

		if (Files.exists(path, NO_FOLLOW_LINKS)) {
			throw new FtpFileStorageException("FTP destination already exists");
		}
	}

	private void move(Path source, Path target, String failureMessage) {
		requireAvailable(target);

		try {
			Files.move(source, target);
		} catch (IOException exception) {
			throw new FtpFileStorageException(failureMessage, exception);
		}
	}

	private void delete(Path path, String failureMessage) {
		try {
			Files.delete(path);
		} catch (IOException exception) {
			throw new FtpFileStorageException(failureMessage, exception);
		}
	}
}
