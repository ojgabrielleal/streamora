package com.streamora.streamora.infrastructure.pureFtpd.user.interfaces;

import java.util.List;

public interface FtpCommandExecutor {

	void execute(List<String> command, String standardInput);
}
