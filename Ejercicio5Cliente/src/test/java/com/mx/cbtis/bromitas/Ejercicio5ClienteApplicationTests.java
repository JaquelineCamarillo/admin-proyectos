package com.mx.cbtis.bromitas;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class Ejercicio5ClienteApplicationTests {

	@Autowired
	private RestTemplate restTemplate;

	@Test
	@DisplayName("FRONT-CTX01 Carga del contexto y verificación del bean RestTemplateConfig")
	void contextLoads() {
		assertNotNull(restTemplate, "El bean RestTemplate debe estar configurado en el contexto");
	}

}
