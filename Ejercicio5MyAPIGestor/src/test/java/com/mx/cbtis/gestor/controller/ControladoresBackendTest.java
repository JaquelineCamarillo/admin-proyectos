package com.mx.cbtis.gestor.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.mx.cbtis.gestor.modelo.Asignaciones;
import com.mx.cbtis.gestor.modelo.Comentarios;
import com.mx.cbtis.gestor.modelo.Proyectos;
import com.mx.cbtis.gestor.modelo.Tareas;
import com.mx.cbtis.gestor.modelo.Usuario;
import com.mx.cbtis.gestor.repository.AsignacionesRepository;
import com.mx.cbtis.gestor.repository.ComentariosRepository;
import com.mx.cbtis.gestor.repository.ProtectosRepository;
import com.mx.cbtis.gestor.repository.TareasRepository;
import com.mx.cbtis.gestor.repository.UsuarioRepository;

/**
 * Práctica 3 – Pruebas unitarias y de controladores REST del Backend (Ejercicio5MyAPIGestor).
 * Cubre todos los controladores y GlobalExceptionHandler verificando códigos HTTP y cuerpo JSON.
 */
@WebMvcTest(controllers = {
		AsignacionesController.class,
		ComentariosController.class,
		ProyectosController.class,
		TareasController.class,
		UsuarioController.class,
		GlobalExceptionHandler.class
})
class ControladoresBackendTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private AsignacionesRepository asignacionesRepo;

	@MockBean
	private ComentariosRepository comentariosRepo;

	@MockBean
	private ProtectosRepository proyectosRepo;

	@MockBean
	private TareasRepository tareasRepo;

	@MockBean
	private UsuarioRepository usuarioRepo;

	// ======================= ASIGNACIONES CONTROLLER =======================

	@Test
	@DisplayName("API-A01 getAsignacion: devuelve 200 y la asignación cuando existe")
	void getAsignacion_existe() throws Exception {
		Asignaciones a = new Asignaciones(1, 131, 199, "2026-10-30");
		when(asignacionesRepo.findById(1)).thenReturn(Optional.of(a));

		mvc.perform(get("/buscarAsignacion").param("id", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_asignacion", is(1)))
				.andExpect(jsonPath("$.id_usuario", is(131)))
				.andExpect(jsonPath("$.id_tarea", is(199)));
	}

	@Test
	@DisplayName("API-A02 getAsignacion: devuelve 404 cuando no existe")
	void getAsignacion_noExiste() throws Exception {
		when(asignacionesRepo.findById(999)).thenReturn(Optional.empty());

		mvc.perform(get("/buscarAsignacion").param("id", "999"))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("API-A03 addAsignacion: guarda y devuelve 200 con cuerpo 1")
	void addAsignacion_exito() throws Exception {
		when(asignacionesRepo.save(any(Asignaciones.class))).thenReturn(new Asignaciones());

		mvc.perform(post("/addAsignacion")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_usuario\":131,\"id_tarea\":199,\"fecha_asignacion\":\"2026-10-30\"}"))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));
	}

	@Test
	@DisplayName("API-A04 addAsignacion: violación de integridad devuelve 409 Conflict (GlobalExceptionHandler)")
	void addAsignacion_conflictoIntegridad() throws Exception {
		when(asignacionesRepo.save(any(Asignaciones.class)))
				.thenThrow(new DataIntegrityViolationException("FK constraint fails"));

		mvc.perform(post("/addAsignacion")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_usuario\":9999,\"id_tarea\":9999}"))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.status", is(409)))
				.andExpect(jsonPath("$.error", is("Conflict")))
				.andExpect(jsonPath("$.mensaje").exists());
	}

	@Test
	@DisplayName("API-A05 updateAsignacion: actualiza y devuelve 200 con cuerpo 1")
	void updateAsignacion_exito() throws Exception {
		when(asignacionesRepo.save(any(Asignaciones.class))).thenReturn(new Asignaciones());

		mvc.perform(post("/updateAsignacion")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_asignacion\":1,\"id_usuario\":131,\"id_tarea\":199,\"fecha_asignacion\":\"2026-10-30\"}"))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));
	}

	@Test
	@DisplayName("API-A06 deleteAsignacion: elimina cuando existe y devuelve 200 con cuerpo 1")
	void deleteAsignacion_existe() throws Exception {
		when(asignacionesRepo.existsById(1)).thenReturn(true);
		doNothing().when(asignacionesRepo).deleteById(1);

		mvc.perform(delete("/deleteAsignacion")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_asignacion\":1}"))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));
		verify(asignacionesRepo).deleteById(1);
	}

	@Test
	@DisplayName("API-A07 deleteAsignacion: devuelve 404 cuando id es null o no existe")
	void deleteAsignacion_noExisteONull() throws Exception {
		when(asignacionesRepo.existsById(999)).thenReturn(false);

		mvc.perform(delete("/deleteAsignacion")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_asignacion\":999}"))
				.andExpect(status().isNotFound())
				.andExpect(content().string("0"));

		mvc.perform(delete("/deleteAsignacion")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
				.andExpect(status().isNotFound())
				.andExpect(content().string("0"));
	}

	@Test
	@DisplayName("API-A08 getAsignaciones: devuelve lista completa")
	void getAsignaciones_lista() throws Exception {
		when(asignacionesRepo.findAll()).thenReturn(
				Arrays.asList(new Asignaciones(1, 131, 199, "2026-10-30")));

		mvc.perform(post("/buscarAsignaciones"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id_asignacion", is(1)));
	}

	// ======================= COMENTARIOS CONTROLLER =======================

	@Test
	@DisplayName("API-C01 getComentario por tarea: devuelve comentarios de la tarea")
	void getComentarioPorTarea() throws Exception {
		Comentarios c = new Comentarios(201, "Perla", 199, "Subirlo YA!!!", "2024-06-26 13:48:00");
		when(comentariosRepo.findByTarea(199)).thenReturn(Collections.singletonList(c));

		mvc.perform(post("/getComentario")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_tarea\":199}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].texto_comentario", is("Subirlo YA!!!")));
	}

	@Test
	@DisplayName("API-C02 buscarComentario: devuelve 200 cuando existe y 404 cuando no")
	void buscarComentario_existeYNoExiste() throws Exception {
		Comentarios c = new Comentarios(201, "Perla", 199, "Hola", "2024-06-26");
		when(comentariosRepo.findById(201)).thenReturn(Optional.of(c));
		when(comentariosRepo.findById(999)).thenReturn(Optional.empty());

		mvc.perform(get("/buscarComentario").param("id", "201"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_comentario", is(201)));

		mvc.perform(get("/buscarComentario").param("id", "999"))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("API-C03 addComentario y updateComentario: guardan y devuelven 1")
	void addYUpdateComentario() throws Exception {
		when(comentariosRepo.save(any(Comentarios.class))).thenReturn(new Comentarios());

		mvc.perform(post("/addComentario")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_tarea\":199,\"texto_comentario\":\"Nuevo\"}"))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		mvc.perform(post("/updateComentario")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_comentario\":201,\"id_tarea\":199,\"texto_comentario\":\"Editado\"}"))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));
	}

	@Test
	@DisplayName("API-C04 deleteComentario: elimina existente (200) y rechaza inexistente o null (404)")
	void deleteComentario_casos() throws Exception {
		when(comentariosRepo.existsById(201)).thenReturn(true);
		when(comentariosRepo.existsById(999)).thenReturn(false);

		mvc.perform(delete("/deleteComentario")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_comentario\":201}"))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		mvc.perform(delete("/deleteComentario")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_comentario\":999}"))
				.andExpect(status().isNotFound())
				.andExpect(content().string("0"));

		mvc.perform(delete("/deleteComentario")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
				.andExpect(status().isNotFound())
				.andExpect(content().string("0"));
	}

	@Test
	@DisplayName("API-C05 buscarComentarios: devuelve lista de todos los comentarios")
	void getComentarios_lista() throws Exception {
		when(comentariosRepo.findAll()).thenReturn(
				Collections.singletonList(new Comentarios(201, "Perla", 199, "Texto", "2024-06-26")));

		mvc.perform(post("/buscarComentarios"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));
	}

	// ======================= PROYECTOS CONTROLLER =======================

	@Test
	@DisplayName("API-P01 buscarProyecto: devuelve 200 cuando existe y 404 cuando no existe")
	void getProyecto_existeYNoExiste() throws Exception {
		Proyectos p = new Proyectos(197, "My API Rest", "Desc", "2024-05-31", "2024-06-29", "En proceso", "fondo.gif");
		when(proyectosRepo.findById(197)).thenReturn(Optional.of(p));
		when(proyectosRepo.findById(999)).thenReturn(Optional.empty());

		mvc.perform(get("/buscarProyecto").param("id_proyecto", "197"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre", is("My API Rest")));

		mvc.perform(get("/buscarProyecto").param("id_proyecto", "999"))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("API-P02 addProyecto y updateProyecto: guardan y devuelven 200 con cuerpo 1")
	void addYUpdateProyecto() throws Exception {
		when(proyectosRepo.save(any(Proyectos.class))).thenReturn(new Proyectos());

		mvc.perform(post("/addProyecto")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"Nuevo Proyecto\"}"))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		mvc.perform(post("/updateProyecto")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_proyecto\":197,\"nombre\":\"Proyecto Actualizado\"}"))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));
	}

	@Test
	@DisplayName("API-P03 deleteProyecto: elimina existente (200) y rechaza inexistente o null (404)")
	void deleteProyecto_casos() throws Exception {
		when(proyectosRepo.existsById(197)).thenReturn(true);
		when(proyectosRepo.existsById(999)).thenReturn(false);

		mvc.perform(delete("/deleteProyecto")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_proyecto\":197}"))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		mvc.perform(delete("/deleteProyecto")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_proyecto\":999}"))
				.andExpect(status().isNotFound())
				.andExpect(content().string("0"));

		mvc.perform(delete("/deleteProyecto")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
				.andExpect(status().isNotFound())
				.andExpect(content().string("0"));
	}

	@Test
	@DisplayName("API-P04 buscarProyectos: devuelve lista de proyectos")
	void getProyectos_lista() throws Exception {
		when(proyectosRepo.findAll()).thenReturn(
				Collections.singletonList(new Proyectos(197, "My API Rest", "Desc", "2024-05-31", "2024-06-29", "Activo", "img")));

		mvc.perform(post("/buscarProyectos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id_proyecto", is(197)));
	}

	// ======================= TAREAS CONTROLLER =======================

	@Test
	@DisplayName("API-T01 getTarea por proyecto y buscarTarea por ID (200 y 404)")
	void getTarea_casos() throws Exception {
		Tareas t = new Tareas(199, "Base de datos", "Tablas", "2024-06-13", "terminado", "danger", 197);
		when(tareasRepo.findByIdProyecto(197)).thenReturn(Collections.singletonList(t));
		when(tareasRepo.findById(199)).thenReturn(Optional.of(t));
		when(tareasRepo.findById(999)).thenReturn(Optional.empty());

		mvc.perform(post("/getTarea")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_proyecto\":197}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].nombre", is("Base de datos")));

		mvc.perform(get("/buscarTarea").param("id_tarea", "199"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_tarea", is(199)));

		mvc.perform(get("/buscarTarea").param("id_tarea", "999"))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("API-T02 addTareas y updateTareas: aceptan fecha válida, fecha vacía o null y rechazan fecha inválida (400)")
	void addYUpdateTareas_validacionFechas() throws Exception {
		when(tareasRepo.save(any(Tareas.class))).thenReturn(new Tareas());

		// Fecha válida
		mvc.perform(post("/addTareas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"T1\",\"fecha_vencimiento\":\"2026-10-30\",\"id_proyecto\":197}"))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		// Fecha vacía (se normaliza a null)
		mvc.perform(post("/addTareas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"T2\",\"fecha_vencimiento\":\"   \",\"id_proyecto\":197}"))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		// Fecha null
		mvc.perform(post("/updateTareas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_tarea\":199,\"nombre\":\"T3\",\"id_proyecto\":197}"))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		// Fecha inválida en addTareas -> 400 Bad Request
		mvc.perform(post("/addTareas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"T4\",\"fecha_vencimiento\":\"2026-02-30\",\"id_proyecto\":197}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("0"));

		// Fecha inválida en updateTareas -> 400 Bad Request
		mvc.perform(post("/updateTareas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_tarea\":199,\"nombre\":\"T5\",\"fecha_vencimiento\":\"fecha-mala\",\"id_proyecto\":197}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("0"));
	}

	@Test
	@DisplayName("API-T03 deleteTareas y buscarTareas: elimina (200/404) y consulta lista")
	void deleteYBuscarTareas() throws Exception {
		when(tareasRepo.existsById(199)).thenReturn(true);
		when(tareasRepo.existsById(999)).thenReturn(false);
		when(tareasRepo.findAll()).thenReturn(
				Collections.singletonList(new Tareas(199, "BD", "Desc", "2024-06-13", "terminado", "danger", 197)));

		mvc.perform(delete("/deleteTareas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_tarea\":199}"))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		mvc.perform(delete("/deleteTareas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_tarea\":999}"))
				.andExpect(status().isNotFound())
				.andExpect(content().string("0"));

		mvc.perform(delete("/deleteTareas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
				.andExpect(status().isNotFound())
				.andExpect(content().string("0"));

		mvc.perform(post("/buscarTareas"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));
	}

	// ======================= USUARIO CONTROLLER =======================

	@Test
	@DisplayName("API-U01 getUsuarioLogin: devuelve usuario cuando credenciales coinciden y nombre '000' cuando fallan")
	void getUsuarioLogin_exitoYFallo() throws Exception {
		Usuario ana = new Usuario(131, "Ana Paola", "ana@cbtis.edu.mx", "0722", "Estudiante", "#70FAB7");
		when(usuarioRepo.findByCorreoPass("ana@cbtis.edu.mx", "0722")).thenReturn(Optional.of(ana));
		when(usuarioRepo.findByCorreoPass("ana@cbtis.edu.mx", "mal")).thenReturn(Optional.empty());

		mvc.perform(post("/getUsuarioLogin")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"correo_electronico\":\"ana@cbtis.edu.mx\",\"contrasena\":\"0722\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre", is("Ana Paola")));

		mvc.perform(post("/getUsuarioLogin")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"correo_electronico\":\"ana@cbtis.edu.mx\",\"contrasena\":\"mal\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre", is("000")));
	}

	@Test
	@DisplayName("API-U02 buscarUsuario: devuelve 200 cuando existe y 404 cuando no existe")
	void buscarUsuario_casos() throws Exception {
		Usuario ana = new Usuario(131, "Ana Paola", "ana@cbtis.edu.mx", "0722", "Estudiante", "#70FAB7");
		when(usuarioRepo.findById(131)).thenReturn(Optional.of(ana));
		when(usuarioRepo.findById(999)).thenReturn(Optional.empty());

		mvc.perform(get("/buscarUsuario").param("id", "131"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_usuario", is(131)));

		mvc.perform(get("/buscarUsuario").param("id", "999"))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("API-U03 addUsuario y updateUsuario: validan campos obligatorios (200 y 400)")
	void addYUpdateUsuario_validacion() throws Exception {
		when(usuarioRepo.save(any(Usuario.class))).thenReturn(new Usuario());

		String valido = "{\"nombre\":\"Juana\",\"correo_electronico\":\"juana@utng.edu.mx\",\"contrasena\":\"12345\"}";
		mvc.perform(post("/addUsuario").contentType(MediaType.APPLICATION_JSON).content(valido))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		mvc.perform(post("/updateUsuario").contentType(MediaType.APPLICATION_JSON).content(valido))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		// Casos inválidos: sin nombre, sin correo, sin contraseña
		mvc.perform(post("/addUsuario").contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"  \",\"correo_electronico\":\"a@b.c\",\"contrasena\":\"123\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("0"));

		mvc.perform(post("/addUsuario").contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"Juana\",\"correo_electronico\":null,\"contrasena\":\"123\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("0"));

		mvc.perform(post("/updateUsuario").contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"Juana\",\"correo_electronico\":\"a@b.c\",\"contrasena\":\"\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("0"));

		// Validación directa de esValido(null) para cubrir la rama defensiva u != null
		UsuarioController ctrl = new UsuarioController();
		org.junit.jupiter.api.Assertions.assertEquals(400, ctrl.addUsuario(null).getStatusCodeValue());
	}

	@Test
	@DisplayName("API-U04 deleteUsuario y buscarUsuarios: elimina (200/404) y lista todos")
	void deleteYBuscarUsuarios() throws Exception {
		when(usuarioRepo.existsById(131)).thenReturn(true);
		when(usuarioRepo.existsById(999)).thenReturn(false);
		when(usuarioRepo.findAll()).thenReturn(
				Collections.singletonList(new Usuario(131, "Ana", "ana@cbtis.edu.mx", "0722", "Estudiante", "#fff")));

		mvc.perform(delete("/deleteUsuario")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_usuario\":131}"))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		mvc.perform(delete("/deleteUsuario")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_usuario\":999}"))
				.andExpect(status().isNotFound())
				.andExpect(content().string("0"));

		mvc.perform(delete("/deleteUsuario")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
				.andExpect(status().isNotFound())
				.andExpect(content().string("0"));

		mvc.perform(post("/buscarUsuarios"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));
	}
}
