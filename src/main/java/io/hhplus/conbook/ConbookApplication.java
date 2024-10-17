package io.hhplus.conbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ConbookApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConbookApplication.class, args);
	}

}
