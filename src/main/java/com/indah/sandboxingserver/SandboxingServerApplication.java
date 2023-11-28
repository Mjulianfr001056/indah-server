package com.indah.sandboxingserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.indah.sandboxingserver.model")
public class SandboxingServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SandboxingServerApplication.class, args);
	}

}
