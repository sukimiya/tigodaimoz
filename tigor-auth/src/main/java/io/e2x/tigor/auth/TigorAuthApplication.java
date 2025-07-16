package io.e2x.tigor.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "io.e2x.tigor")
public class TigorAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(TigorAuthApplication.class, args);
	}

}
