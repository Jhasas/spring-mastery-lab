package com.spring_base.fundamentals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class FundamentalsApplicationTests {

	@Test
	void contextLoads() {
	}

}
