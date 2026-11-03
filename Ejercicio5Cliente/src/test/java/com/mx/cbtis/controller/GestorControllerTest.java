package com.mx.cbtis.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
 * Práctica 2 y 3 – Pruebas automáticas completas del frontend (GestorController y RestTemplateConfig).
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

	private static final String TAREAS_JSON = "[{\"id_tarea\":199,\"nombre\":\"Base de datos\",\"descripcion\":\"Tablas\","
			+ "\"fecha_vencimiento\":\"2024-06-13\",\"estado\":\"danger\",\"lista\":\"terminado\",\"id_proyecto\":197}]";

	private static final String COMENTARIOS_JSON = "[{\"id_comentario\":201,\"texto_comentario\":\"Subirlo YA!!!\","
			+ "\"fecha_hora\":\"2024-06-26 13:48:00\",\"id_usuario\":\"Perla Joceline Martínez Meza\",\"id_tarea\":199}]";

	@Autowired
	private MockMvc mvc;

	@Autowired
	private RestTemplate restTemplate;

	private MockRestServiceServer api;

	@BeforeEach
	void prepararApiSimulada() {
		api = MockRestServiceServer.bindTo(restTemplate).build();
	}

	// ======================= VISTAS =======================

	@Test
	@DisplayName("FRONT-V01 Vistas estáticas: /, /tutorial, /manual, /login")
	void vistasEstaticas_devuelvenPlantillas() throws Exception {
		mvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("home"));
		mvc.perform(get("/tutorial")).andExpect(status().isOk()).andExpect(view().name("tutorial"));
		mvc.perform(get("/manual")).andExpect(status().isOk()).andExpect(view().name("manual"));
		mvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));
	}

	@Test
	@DisplayName("FRONT-V02 Vistas con modelo: /register, /inicio, /tarea")
	void vistasConModelo_agreganAtributos() throws Exception {
		mvc.perform(get("/register"))
				.andExpect(status().isOk())
				.andExpect(view().name("register"))
				.andExpect(model().attributeExists("usuario"));

		mvc.perform(get("/inicio"))
				.andExpect(status().isOk())
				.andExpect(view().name("inicio"))
				.andExpect(model().attributeExists("proyectos"));

		mvc.perform(get("/tarea"))
				.andExpect(status().isOk())
				.andExpect(view().name("proyecto"))
				.andExpect(model().attributeExists("tareas"));
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

	@Test
	@DisplayName("FRONT-U01 consultarUsuarios: devuelve lista JSON de usuarios")
	void consultarUsuarios_devuelveLista() throws Exception {
		api.expect(requestTo(API + "/buscarUsuarios")).andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess(USUARIOS_JSON, MediaType.APPLICATION_JSON));

		mvc.perform(post("/consultarUsuarios"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[0].id_usuario", is(131)));
		api.verify();
	}

	@Test
	@DisplayName("FRONT-P01 Operaciones de proyectos: saveProyecto, eliminarProyecto y actualizarProyecto")
	void operacionesProyectos_exito() throws Exception {
		api.expect(requestTo(API + "/addProyecto")).andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess("1", MediaType.APPLICATION_JSON));
		api.expect(requestTo(API + "/deleteProyecto")).andExpect(method(HttpMethod.DELETE))
				.andRespond(withSuccess("1", MediaType.APPLICATION_JSON));
		api.expect(requestTo(API + "/buscarProyecto?id_proyecto=197")).andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("{\"id_proyecto\":197,\"nombre\":\"My API Rest\"}", MediaType.APPLICATION_JSON));

		mvc.perform(post("/saveProyecto").param("nombre", "Nuevo"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/inicio"));

		mvc.perform(post("/eliminarProyecto").contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_proyecto\":197}"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/inicio"));

		mvc.perform(post("/actualizarProyecto").contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_proyecto\":197}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre", is("My API Rest")));

		api.verify();
	}

	@Test
	@DisplayName("FRONT-T01 Operaciones de tareas: obtenerTareas, saveTarea, actualizarTarea, eliminarTarea, cambiosTareas, consultarTareas")
	void operacionesTareas_exito() throws Exception {
		api.expect(requestTo(API + "/getTarea")).andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess(TAREAS_JSON, MediaType.APPLICATION_JSON));
		api.expect(requestTo(API + "/addTareas")).andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess("1", MediaType.APPLICATION_JSON));
		api.expect(requestTo(API + "/buscarTarea?id_tarea=199")).andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("{\"id_tarea\":199,\"nombre\":\"Base de datos\"}", MediaType.APPLICATION_JSON));
		api.expect(requestTo(API + "/deleteTareas")).andExpect(method(HttpMethod.DELETE))
				.andRespond(withSuccess("1", MediaType.APPLICATION_JSON));
		api.expect(requestTo(API + "/addTareas")).andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess("1", MediaType.APPLICATION_JSON));
		api.expect(requestTo(API + "/buscarTareas")).andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess(TAREAS_JSON, MediaType.APPLICATION_JSON));

		mvc.perform(post("/obtenerTareas").contentType(MediaType.APPLICATION_JSON).content("{\"id_proyecto\":197}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));

		mvc.perform(post("/saveTarea").param("nombre", "T1").param("id_proyecto", "197"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/proyecto?id_proyecto=197"));

		mvc.perform(post("/actualizarTarea").contentType(MediaType.APPLICATION_JSON).content("{\"id_tarea\":199}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_tarea", is(199)));

		mvc.perform(post("/eliminarTarea").contentType(MediaType.APPLICATION_JSON).content("{\"id_tarea\":199}"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/proyecto"));

		mvc.perform(post("/cambiosTareas").contentType(MediaType.APPLICATION_JSON).content("{\"id_tarea\":199,\"lista\":\"terminado\"}"))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		mvc.perform(post("/consultarTareas"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));

		api.verify();
	}

	@Test
	@DisplayName("FRONT-C01 Operaciones de comentarios: obtenerComentarios, saveComentario y consultarComentarios")
	void operacionesComentarios_exito() throws Exception {
		api.expect(requestTo(API + "/getComentario")).andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess(COMENTARIOS_JSON, MediaType.APPLICATION_JSON));
		api.expect(requestTo(API + "/addComentario")).andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess("1", MediaType.APPLICATION_JSON));
		api.expect(requestTo(API + "/buscarComentarios")).andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess(COMENTARIOS_JSON, MediaType.APPLICATION_JSON));

		mvc.perform(post("/obtenerComentarios").contentType(MediaType.APPLICATION_JSON).content("{\"id_tarea\":199}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id_comentario", is(201)));

		mvc.perform(post("/saveComentario").contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_tarea\":199,\"texto_comentario\":\"Listo\"}"))
				.andExpect(status().isOk())
				.andExpect(view().name("proyecto"));

		mvc.perform(post("/consultarComentarios"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));

		api.verify();
	}

	// ======================= CASOS DE FRACASO Y VALIDACIONES =======================

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
	@DisplayName("AUT-F02 Formulario de registro: datos incompletos no llegan al backend (todas las ramas)")
	void saveUsuario_datosIncompletos() throws Exception {
		// Sin nombre (null)
		mvc.perform(post("/saveUsuario").param("correo_electronico", "a@b.c").param("contrasena", "123"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/register?error"));

		// Nombre en blanco
		mvc.perform(post("/saveUsuario").param("nombre", "   ").param("correo_electronico", "a@b.c").param("contrasena", "123"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/register?error"));

		// Sin correo
		mvc.perform(post("/saveUsuario").param("nombre", "Sin correo"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/register?error"));

		// Sin contraseña
		mvc.perform(post("/saveUsuario").param("nombre", "Juana").param("correo_electronico", "a@b.c").param("contrasena", ""))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/register?error"));

		api.verify();
	}

	@Test
	@DisplayName("AUT-F03 Recurso inexistente: proyecto 99999 (404 sin cuerpo) y error 400 sin cuerpo")
	void actualizarProyecto_noExisteYBadRequestSinCuerpo() throws Exception {
		api.expect(requestTo(API + "/buscarProyecto?id_proyecto=99999")).andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.NOT_FOUND));
		api.expect(requestTo(API + "/buscarProyecto?id_proyecto=-1")).andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.BAD_REQUEST));

		mvc.perform(post("/actualizarProyecto").contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_proyecto\":99999}"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.mensaje").value("El recurso solicitado no existe."));

		mvc.perform(post("/actualizarProyecto").contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_proyecto\":-1}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensaje").value("La solicitud no es válida."));

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
	@DisplayName("AUT-F05 Formulario de tarea: la API rechaza la fecha (400 con cuerpo)")
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
