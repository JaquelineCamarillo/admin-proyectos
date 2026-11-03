package com.mx.cbtis.gestor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mx.cbtis.gestor.controller.ProyectosController;

@SpringBootTest
class Ejercicio5MyApiGestorApplicationTests {

	@Autowired
	private ProyectosController proyectosController;

	@Test
	@DisplayName("API-CTX01 Carga completa del contexto de Spring Boot con base de datos en memoria H2")
	void contextLoads() {
		assertNotNull(proyectosController, "El controlador de proyectos debe estar inyectado en el contexto");
	}

}
