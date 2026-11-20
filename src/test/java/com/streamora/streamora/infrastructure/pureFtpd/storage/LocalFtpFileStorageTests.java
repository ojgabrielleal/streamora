package com.streamora.streamora.infrastructure.pureFtpd.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.streamora.streamora.infrastructure.pureFtpd.storage.exception.FtpFileStorageException;
import com.streamora.streamora.infrastructure.pureFtpd.storage.records.FtpFileEntry;
import com.streamora.streamora.infrastructure.pureFtpd.storage.records.FtpFileType;
import com.streamora.streamora.infrastructure.pureFtpd.user.records.PureFtpdProperties;

class LocalFtpFileStorageTests {

	@TempDir
	Path root;

	private LocalFtpFileStorage storage;

	@BeforeEach
	void setUp() {
		storage = new LocalFtpFileStorage(properties(root));
	}

	@Test
	void listsRecursiveTreeWithDirectoriesBeforeFilesAndAlphabeticalNames() throws IOException {
		Files.writeString(root.resolve("zeta.mp3"), "zeta");
		Files.writeString(root.resolve("alpha.mp3"), "alpha");
		Path zetaDirectory = Files.createDirectory(root.resolve("zeta"));
		Path alphaDirectory = Files.createDirectory(root.resolve("alpha"));
		Files.writeString(alphaDirectory.resolve("track.mp3"), "track");
		Files.createDirectory(zetaDirectory.resolve("nested"));

		List<FtpFileEntry> fileTree = storage.list();

		assertThat(fileTree)
			.extracting(FtpFileEntry::name)
			.containsExactly("alpha", "zeta", "alpha.mp3", "zeta.mp3");
		assertThat(fileTree.getFirst().children())
			.extracting(FtpFileEntry::name)
			.containsExactly("track.mp3");
		assertThat(fileTree.get(1).children())
			.extracting(FtpFileEntry::name)
			.containsExactly("nested");
	}

	@Test
	void includesFormatSizeAndLastModification() throws IOException {
		Files.writeString(root.resolve("audio.MP3"), "audio");
		Files.writeString(root.resolve("README"), "readme");
		Files.createDirectory(root.resolve("programas"));

		List<FtpFileEntry> fileTree = storage.list();

		assertThat(fileTree)
			.allSatisfy(entry -> assertThat(entry.lastModified()).isNotNull());
		assertThat(fileTree)
			.filteredOn(entry -> entry.name().equals("audio.MP3"))
			.singleElement()
			.satisfies(entry -> {
				assertThat(entry.format()).isEqualTo("mp3");
				assertThat(entry.size()).isEqualTo(5L);
			});
		assertThat(fileTree)
			.filteredOn(entry -> entry.name().equals("README"))
			.singleElement()
			.satisfies(entry -> assertThat(entry.format()).isNull());
		assertThat(fileTree)
			.filteredOn(entry -> entry.type() == FtpFileType.DIRECTORY)
			.singleElement()
			.satisfies(entry -> {
				assertThat(entry.format()).isNull();
				assertThat(entry.size()).isNull();
			});
	}

	@Test
	void deletesFile() throws IOException {
		Path deletedFile = Files.writeString(root.resolve("audio.mp3"), "content");

		storage.deleteFile("audio.mp3");

		assertThat(deletedFile).doesNotExist();
	}

	@Test
	void renamesFileAndPreservesContent() throws IOException {
		Files.writeString(root.resolve("audio.mp3"), "content");

		storage.renameFile("audio.mp3", "renamed.mp3");

		assertThat(root.resolve("audio.mp3")).doesNotExist();
		assertThat(root.resolve("renamed.mp3")).hasContent("content");
	}

	@Test
	void movesFileToExistingDirectory() throws IOException {
		Path directory = Files.createDirectory(root.resolve("archive"));
		Files.writeString(root.resolve("audio.mp3"), "content");

		storage.moveFile("audio.mp3", "archive");

		assertThat(directory.resolve("audio.mp3")).hasContent("content");
	}

	@Test
	void movesFileBackToRoot() throws IOException {
		Path directory = Files.createDirectory(root.resolve("archive"));
		Files.writeString(directory.resolve("audio.mp3"), "content");

		storage.moveFile("archive/audio.mp3", "");

		assertThat(root.resolve("audio.mp3")).hasContent("content");
	}

	@Test
	void createsDirectory() {
		storage.createDirectory("new-directory");

		assertThat(root.resolve("new-directory")).isDirectory();
	}

	@Test
	void renamesDirectory() throws IOException {
		Files.createDirectory(root.resolve("new-directory"));

		storage.renameDirectory("new-directory", "renamed-directory");

		assertThat(root.resolve("new-directory")).doesNotExist();
		assertThat(root.resolve("renamed-directory")).isDirectory();
	}

	@Test
	void movesDirectoryToExistingDirectory() throws IOException {
		Path target = Files.createDirectory(root.resolve("target"));
		Path directory = Files.createDirectory(root.resolve("directory"));
		Files.writeString(directory.resolve("audio.mp3"), "content");

		storage.moveDirectory("directory", "target");

		assertThat(root.resolve("directory")).doesNotExist();
		assertThat(target.resolve("directory/audio.mp3")).hasContent("content");
	}

	@Test
	void rejectsMovingDirectoryIntoItself() throws IOException {
		Files.createDirectories(root.resolve("directory/nested"));

		assertThatThrownBy(() -> storage.moveDirectory("directory", "directory/nested"))
			.isInstanceOf(FtpFileStorageException.class)
			.hasMessage("FTP directory cannot be moved into itself");
	}

	@Test
	void deletesEmptyDirectory() throws IOException {
		Files.createDirectory(root.resolve("empty-directory"));

		storage.deleteDirectory("empty-directory");

		assertThat(root.resolve("empty-directory")).doesNotExist();
	}

	@Test
	void rejectsDeletingNonEmptyDirectoryAndPreservesItsContent() throws IOException {
		Path directory = Files.createDirectory(root.resolve("archive"));
		Path preservedFile = Files.writeString(directory.resolve("audio.mp3"), "content");

		assertThatThrownBy(() -> storage.deleteDirectory("archive"))
			.isInstanceOf(FtpFileStorageException.class)
			.hasMessage("FTP directory must be empty");
		assertThat(preservedFile).hasContent("content");
	}

	@Test
	void rejectsDeletingMissingFile() {
		assertThatThrownBy(() -> storage.deleteFile("missing.mp3"))
			.isInstanceOf(FtpFileStorageException.class)
			.hasMessage("FTP file does not exist");
	}

	@Test
	void rejectsDeletingDirectoryAsFile() throws IOException {
		Files.createDirectory(root.resolve("directory"));

		assertThatThrownBy(() -> storage.deleteFile("directory"))
			.isInstanceOf(FtpFileStorageException.class)
			.hasMessage("FTP path is not a file");
	}

	@Test
	void rejectsDeletingFileAsDirectory() throws IOException {
		Files.writeString(root.resolve("audio.mp3"), "content");

		assertThatThrownBy(() -> storage.deleteDirectory("audio.mp3"))
			.isInstanceOf(FtpFileStorageException.class)
			.hasMessage("FTP path is not a directory");
	}

	@Test
	void rejectsBlankFilePath() {
		assertThatThrownBy(() -> storage.deleteFile(" "))
			.isInstanceOf(FtpFileStorageException.class)
			.hasMessage("FTP path must not be blank");
	}

	@Test
	void rejectsBlankFileName() throws IOException {
		Files.writeString(root.resolve("audio.mp3"), "content");

		assertThatThrownBy(() -> storage.renameFile("audio.mp3", " "))
			.isInstanceOf(FtpFileStorageException.class)
			.hasMessage("FTP item name must not be blank");
	}

	@Test
	void rejectsInvalidDestinationDirectory() throws IOException {
		Files.writeString(root.resolve("audio.mp3"), "content");

		assertThatThrownBy(() -> storage.moveFile("audio.mp3", "missing"))
			.isInstanceOf(FtpFileStorageException.class)
			.hasMessage("FTP directory does not exist");
	}

	@Test
	void rejectsNameCollisionsWithoutOverwritingDestination() throws IOException {
		Files.writeString(root.resolve("source.mp3"), "source");
		Path target = Files.writeString(root.resolve("target.mp3"), "target");

		assertThatThrownBy(() -> storage.renameFile("source.mp3", "target.mp3"))
			.isInstanceOf(FtpFileStorageException.class)
			.hasMessage("FTP destination already exists");
		assertThat(root.resolve("source.mp3")).hasContent("source");
		assertThat(target).hasContent("target");
	}

	@Test
	void rejectsAbsolutePath() throws IOException {
		Files.writeString(root.resolve("audio.mp3"), "content");

		assertThatThrownBy(() -> storage.deleteFile(root.resolve("audio.mp3").toString()))
			.isInstanceOf(FtpFileStorageException.class)
			.hasMessage("FTP path must be relative to the configured root");
	}

	@Test
	void rejectsParentTraversal() {
		assertThatThrownBy(() -> storage.deleteFile("../audio.mp3"))
			.isInstanceOf(FtpFileStorageException.class)
			.hasMessage("FTP path must not contain parent traversal");
	}

	@Test
	void rejectsRootModification() {
		assertThatThrownBy(() -> storage.deleteDirectory("."))
			.isInstanceOf(FtpFileStorageException.class)
			.hasMessage("The FTP root cannot be modified");
	}

	@Test
	void rejectsAccessThroughSymbolicLink() throws IOException {
		Path outside = Files.createTempDirectory(root.getParent(), "outside-");
		Files.writeString(outside.resolve("external.mp3"), "external");
		Files.createSymbolicLink(root.resolve("escape"), outside);

		assertThatThrownBy(() -> storage.deleteFile("escape/external.mp3"))
			.isInstanceOf(FtpFileStorageException.class)
			.hasMessage("Symbolic links are not allowed in FTP storage paths");
		assertThat(outside.resolve("external.mp3")).hasContent("external");
	}

	@Test
	void rejectsListingSymbolicLink() throws IOException {
		Path outside = Files.createTempDirectory(root.getParent(), "outside-");
		Files.createSymbolicLink(root.resolve("escape"), outside);

		assertThatThrownBy(() -> storage.list())
			.isInstanceOf(FtpFileStorageException.class)
			.hasMessage("Symbolic links are not allowed in FTP storage paths");
	}

	@Test
	void rejectsCreatingDirectoryWhenParentDoesNotExist() {
		assertThatThrownBy(() -> storage.createDirectory("missing/new-directory"))
			.isInstanceOf(FtpFileStorageException.class)
			.hasMessage("FTP directory does not exist");
	}

	private PureFtpdProperties properties(Path homeDirectory) {
		return new PureFtpdProperties(
			"pure-pw",
			"/credentials/pureftpd.passwd",
			"/credentials/pureftpd.pdb",
			homeDirectory.toString(),
			"localhost",
			21,
			1000,
			1001
		);
	}
}
