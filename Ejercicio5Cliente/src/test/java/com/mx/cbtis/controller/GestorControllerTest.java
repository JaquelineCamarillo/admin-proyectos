package com.mx.cbtis.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

/**
 * Práctica 2 – Pruebas automáticas del frontend (GestorController).
 * MockMvc simula las peticiones del navegador y MockRestServiceServer simula la API (puerto 8081),
 * por lo que las pruebas no dependen de que el backend esté encendido.
 */
@SpringBootTest
@AutoConfigureMockMvc
class GestorControllerTest {

	private static final String API = "http://localhost:8081";

	private static final String PROYECTOS_JSON = "[{\"id_proyecto\":197,\"nombre\":\"My API Rest\",\"descripcion\":\"Api\","
			+ "\"fecha_inicio\":\"2024-05-31\",\"fecha_finalizacion\":\"2024-06-29\",\"estado_proyecto\":\"En proceso\",\"fondo\":\"/Images/animacion1.gif\"},"
			+ "{\"id_proyecto\":198,\"nombre\":\"Proyecto Prueba\",\"descripcion\":\"Prueba\","
			+ "\"fecha_inicio\":\"2026-10-14\",\"fecha_finalizacion\":\"2026-10-18\",\"estado_proyecto\":\"En proceso\",\"fondo\":\"/Images/fondo.png\"}]";

	private static final String ANA_JSON = "{\"id_usuario\":131,\"nombre\":\"Ana Paola Ramírez Vázquez\","
			+ "\"correo_electronico\":\"ana22533@cbtis75.edu.mx\",\"contrasena\":\"0722\",\"rol\":\"Estudiante\",\"imagen_usuario\":\"#70FAB7\"}";

	private static final String USUARIOS_JSON = "[" + ANA_JSON + ","
			+ "{\"id_usuario\":172,\"nombre\":\"Liliana Gómez Martínez\",\"correo_electronico\":\"liliana22512@cbtis75.edu.mx\",\"contrasena\":\"ada\",\"rol\":\"Estudiante\",\"imagen_usuario\":\"#54EA01\"},"
			+ "{\"id_usuario\":196,\"nombre\":\"Perla Joceline Martínez Meza\",\"correo_electronico\":\"perla22522@cbtis75.edu.mx\",\"contrasena\":\"12345\",\"rol\":\"Estudiante\",\"imagen_usuario\":\"#805611\"}]";

	@Autowired
	private MockMvc mvc;

	@Autowired
	private RestTemplate restTemplate;

	private MockRestServiceServer api;

	@BeforeEach
	void prepararApiSimulada() {
		api = MockRestServiceServer.bindTo(restTemplate).build();
	}

	// ======================= CASOS DE ÉXITO =======================

	@Test
	@DisplayName("AUT-E01 Servicio de consulta: el backend devuelve 2 proyectos")
	void consultarProyectos_devuelveLista() throws Exception {
		api.expect(requestTo(API + "/buscarProyectos")).andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess(PROYECTOS_JSON, MediaType.APPLICATION_JSON));

		mvc.perform(post("/consultarProyectos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].nombre").value("My API Rest"));
		api.verify();
	}

	@Test
	@DisplayName("AUT-E02 Servicio de login: credenciales válidas")
	void verificarLogin_credencialesValidas() throws Exception {
		api.expect(requestTo(API + "/getUsuarioLogin")).andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess(ANA_JSON, MediaType.APPLICATION_JSON));

		mvc.perform(post("/verificarLogin").contentType(MediaType.APPLICATION_JSON)
				.content("{\"correo_electronico\":\"ana22533@cbtis75.edu.mx\",\"contrasena\":\"0722\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre").value("Ana Paola Ramírez Vázquez"));
		api.verify();
	}

	@Test
	@DisplayName("AUT-E03 Formulario de registro: datos válidos redirigen a /login")
	void saveUsuario_datosValidos_redirigeLogin() throws Exception {
		api.expect(requestTo(API + "/addUsuario")).andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess("1", MediaType.APPLICATION_JSON));

		mvc.perform(post("/saveUsuario")
				.param("nombre", "Usuario Automatico")
				.param("correo_electronico", "auto@utng.edu.mx")
				.param("contrasena", "12345")
				.param("rol", "Estudiante"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
		api.verify();
	}

	@Test
	@DisplayName("AUT-E04 Formulario de tarea: datos válidos")
	void verificarTarea_datosValidos() throws Exception {
		api.expect(requestTo(API + "/addTareas")).andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess("1", MediaType.APPLICATION_JSON));

		mvc.perform(post("/verificarTarea").contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"Tarea automatica\",\"lista\":\"pendientes\",\"id_proyecto\":197}"))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));
		api.verify();
	}

	@Test
	@DisplayName("AUT-E05 Vista /proyecto: carga la lista de usuarios")
	void vistaProyecto_cargaUsuarios() throws Exception {
		api.expect(requestTo(API + "/buscarUsuarios")).andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess(USUARIOS_JSON, MediaType.APPLICATION_JSON));

		mvc.perform(get("/proyecto"))
				.andExpect(status().isOk())
				.andExpect(view().name("proyecto"))
				.andExpect(model().attribute("listaUsuarios", hasSize(3)));
		api.verify();
	}

	// ======================= CASOS DE FRACASO =======================

	@Test
	@DisplayName("AUT-F01 Servicio de consulta: el backend responde 500")
	void consultarProyectos_backendError() throws Exception {
		api.expect(requestTo(API + "/buscarProyectos"))
				.andRespond(withServerError());

		mvc.perform(post("/consultarProyectos"))
				.andExpect(status().isServiceUnavailable())
				.andExpect(jsonPath("$.mensaje").exists());
		api.verify();
	}

	@Test
	@DisplayName("AUT-F02 Formulario de registro: datos incompletos no llegan al backend")
	void saveUsuario_datosIncompletos() throws Exception {
		// No se declara ninguna petición esperada: si el front llama a la API, la prueba falla.
		mvc.perform(post("/saveUsuario").param("nombre", "Sin correo"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/register?error"));
		api.verify();
	}

	@Test
	@DisplayName("AUT-F03 Recurso inexistente: proyecto 99999")
	void actualizarProyecto_noExiste() throws Exception {
		api.expect(requestTo(API + "/buscarProyecto?id_proyecto=99999")).andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.NOT_FOUND));

		mvc.perform(post("/actualizarProyecto").contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_proyecto\":99999}"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.mensaje").exists());
		api.verify();
	}

	@Test
	@DisplayName("AUT-F04 Servicio de login: la API no está disponible")
	void verificarLogin_apiCaida() throws Exception {
		api.expect(requestTo(API + "/getUsuarioLogin"))
				.andRespond(withException(new IOException("Connection refused")));

		mvc.perform(post("/verificarLogin").contentType(MediaType.APPLICATION_JSON)
				.content("{\"correo_electronico\":\"ana22533@cbtis75.edu.mx\",\"contrasena\":\"0722\"}"))
				.andExpect(status().isServiceUnavailable())
				.andExpect(jsonPath("$.mensaje").exists());
		api.verify();
	}

	@Test
	@DisplayName("AUT-F05 Formulario de tarea: la API rechaza la fecha (400)")
	void verificarTarea_fechaInvalida() throws Exception {
		api.expect(requestTo(API + "/addTareas"))
				.andRespond(withBadRequest().body("0").contentType(MediaType.APPLICATION_JSON));

		mvc.perform(post("/verificarTarea").contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"Tarea\",\"fecha_vencimiento\":\"2026-13-45\",\"id_proyecto\":197}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("0"));
		api.verify();
	}
}