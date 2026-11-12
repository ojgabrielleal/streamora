package com.streamora.streamora.infrastructure.pureFtpd.user;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.streamora.streamora.infrastructure.pureFtpd.user.interfaces.FtpCommandExecutor;
import com.streamora.streamora.infrastructure.pureFtpd.user.records.PureFtpdProperties;

@ExtendWith(MockitoExtension.class)
class PureFtpdTests {

	private static final String PASSWORD_FILE = "/credentials/pureftpd.passwd";
	private static final String DATABASE_FILE = "/credentials/pureftpd.pdb";

	@Mock
	private FtpCommandExecutor commandExecutor;

	private PureFtpd pureFtpd;

	@BeforeEach
	void setUp() {
		pureFtpd = new PureFtpd(
			commandExecutor,
			new PureFtpdProperties(
				"pure-pw",
				PASSWORD_FILE,
				DATABASE_FILE,
				"/home/ftpusers",
				1000,
				1001
			)
		);
	}

	@Test
	void createsUserAndSuppliesPasswordThroughStandardInput() {
		pureFtpd.createFtpdUser("stream.user", "secret");

		verify(commandExecutor).execute(
			List.of(
				"pure-pw", "useradd", "stream.user",
				"-f", PASSWORD_FILE,
				"-F", DATABASE_FILE,
				"-m",
				"-u", "1000",
				"-g", "1001",
				"-d", "/home/ftpusers/stream.user"
			),
			"secret" + System.lineSeparator() + "secret" + System.lineSeparator()
		);
	}

	@Test
	void updatesPasswordAndSuppliesItThroughStandardInput() {
		pureFtpd.updateFtpdPassword("stream-user", "new-secret");

		verify(commandExecutor).execute(
			List.of(
				"pure-pw", "passwd", "stream-user",
				"-f", PASSWORD_FILE,
				"-F", DATABASE_FILE,
				"-m"
			),
			"new-secret" + System.lineSeparator() + "new-secret" + System.lineSeparator()
		);
	}

	@Test
	void deletesUserAndRebuildsDatabase() {
		pureFtpd.deleteFtpdUser("stream_user");

		verify(commandExecutor).execute(
			List.of(
				"pure-pw", "userdel", "stream_user",
				"-f", PASSWORD_FILE,
				"-F", DATABASE_FILE,
				"-m"
			),
			""
		);
	}

	@Test
	void rejectsUnsafeUsernameWithoutExecutingCommand() {
		assertThatThrownBy(() -> pureFtpd.createFtpdUser("../escape", "secret"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("FTP username contains invalid characters");

		verify(commandExecutor, never()).execute(
			org.mockito.ArgumentMatchers.anyList(),
			org.mockito.ArgumentMatchers.anyString()
		);
	}

	@Test
	void rejectsBlankPasswordWithoutExecutingCommand() {
		assertThatThrownBy(() -> pureFtpd.createFtpdUser("valid-user", " "))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("FTP password must not be blank");

		verify(commandExecutor, never()).execute(
			org.mockito.ArgumentMatchers.anyList(),
			org.mockito.ArgumentMatchers.anyString()
		);
	}
}
