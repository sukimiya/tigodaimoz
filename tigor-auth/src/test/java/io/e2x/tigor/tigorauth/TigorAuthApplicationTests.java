package io.e2x.tigor.tigorauth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class TigorAuthApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void passwordTest() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		System.out.println(bCryptPasswordEncoder.encode("A3b7_C9d"));
	}
}
