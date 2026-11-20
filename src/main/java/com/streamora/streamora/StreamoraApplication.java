package com.streamora.streamora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.streamora.streamora.infrastructure.icecast.records.IcecastProperties;
import com.streamora.streamora.infrastructure.pureFtpd.user.records.PureFtpdProperties;
import com.streamora.streamora.infrastructure.liquidsoap.records.LiquidsoapProperties;

@SpringBootApplication
@EnableConfigurationProperties({
	IcecastProperties.class,
	LiquidsoapProperties.class,
	PureFtpdProperties.class
})
public class StreamoraApplication {

	public static void main(String[] args) {
		SpringApplication.run(StreamoraApplication.class, args);
	}

}
