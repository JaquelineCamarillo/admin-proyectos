package com.mx.cbtis.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.mx.cbtis.modelo.Comentarios;
import com.mx.cbtis.modelo.Proyectos;
import com.mx.cbtis.modelo.Tareas;
import com.mx.cbtis.modelo.Usuario;

@Controller
public class GestorController {

	// F-01: RestTemplate inyectado (antes se creaba con "new" en cada método)
	private final RestTemplate template;

	// F-01: URL base de la API configurable en application.properties (api.url)
	private final String api;

	public GestorController(RestTemplate template, @Value("${api.url:http://localhost:8081}") String api) {
		this.template = template;
		this.api = api;
	}

	private HttpEntity<Void> peticionVacia() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new HttpEntity<>(headers);
	}

	// ======================= VISTAS =======================

	@GetMapping("/")
	public String getHome() {
		return "home";
	}

	@GetMapping("/tutorial")
	public String getTutorial() {
		return "tutorial";
	}

	@GetMapping("/manual")
	public String getManual() {
		return "manual";
	}

	@GetMapping("/login")
	public String getLogin() {
		return "login";
	}

	@GetMapping("/register")
	public String getRegister(Model modelo) {
		modelo.addAttribute("usuario", new Usuario());
		return "register";
	}

	@GetMapping("/inicio")
	public String getInicio(Model modelo) {
		modelo.addAttribute("proyectos", new Proyectos());
		return "inicio";
	}

	@GetMapping("/proyecto")
	public String getProyecto(Model modelo) {
		modelo.addAttribute("tarea", new Tareas());
		modelo.addAttribute("comentarios", new Comentarios());

		Usuario[] responseUsuario = template.postForObject(api + "/buscarUsuarios", peticionVacia(), Usuario[].class);
		List<Usuario> usuarios = Arrays.asList(responseUsuario);
		modelo.addAttribute("listaUsuarios", usuarios);

		return "proyecto";
	}

	@GetMapping("/tarea")
	public String getTarea(Model modelo) {
		modelo.addAttribute("tareas", new Tareas());
		return "proyecto";
	}

	// ======================= USUARIOS =======================

	// F-03: validación del lado del servidor antes de llamar a la API
	@PostMapping("/saveUsuario")
	public ModelAndView setSaveUsuario(@ModelAttribute Usuario usuario) {
		if (vacio(usuario.getNombre()) || vacio(usuario.getCorreo_electronico()) || vacio(usuario.getContrasena())) {
			return new ModelAndView("redirect:/register?error");
		}
		template.postForEntity(api + "/addUsuario", usuario, Integer.class);
		return new ModelAndView("redirect:/login");
	}

	@PostMapping("/verificarLogin")
	public ResponseEntity<Usuario> verificarUsuario(@RequestBody Usuario paramUser) {
		Usuario usuario = template.postForObject(api + "/getUsuarioLogin", paramUser, Usuario.class);
		return ResponseEntity.ok(usuario);
	}

	@PostMapping("/consultarUsuarios")
	public ResponseEntity<Object> consultarUsuarios(Model modelo) {
		Usuario[] response = template.postForObject(api + "/buscarUsuarios", peticionVacia(), Usuario[].class);
		List<Usuario> usuarios = Arrays.asList(response);
		modelo.addAttribute("listaUsuarios", usuarios);
		return ResponseEntity.ok(usuarios);
	}

	// ======================= PROYECTOS =======================

	@PostMapping("/saveProyecto")
	public ModelAndView setSaveProyecto(@ModelAttribute Proyectos proyecto) {
		template.postForEntity(api + "/addProyecto", proyecto, Integer.class);
		return new ModelAndView("redirect:/inicio");
	}

	@PostMapping("/consultarProyectos")
	public ResponseEntity<Object> consultarProyectos(Model modelo) {
		Proyectos[] response = template.postForObject(api + "/buscarProyectos", peticionVacia(), Proyectos[].class);
		List<Proyectos> proyectos = Arrays.asList(response);
		modelo.addAttribute("listaProyectos", proyectos);
		return ResponseEntity.ok(proyectos);
	}

	@PostMapping("/eliminarProyecto")
	public ModelAndView eliminarProyecto(@RequestBody Proyectos proyecto) {
		HttpEntity<Proyectos> request = new HttpEntity<>(proyecto);
		template.exchange(api + "/deleteProyecto", HttpMethod.DELETE, request, Integer.class);
		return new ModelAndView("redirect:/inicio");
	}

	@PostMapping("/actualizarProyecto")
	public ResponseEntity<Object> updateProyecto(@RequestBody Proyectos proyecto) {
		Map<String, Integer> params = new HashMap<>();
		params.put("id_proyecto", proyecto.getId_proyecto());
		Proyectos proyectos = template.getForObject(api + "/buscarProyecto?id_proyecto={id_proyecto}", Proyectos.class, params);
		return ResponseEntity.ok(proyectos);
	}

	// ======================= TAREAS =======================

	@PostMapping("/obtenerTareas")
	public ResponseEntity<List<Tareas>> obtenerTareas(@RequestBody Tareas idProyecto, Model modelo) {
		Tareas[] response = template.postForObject(api + "/getTarea", idProyecto, Tareas[].class);
		List<Tareas> listaTareas = Arrays.asList(response);
		modelo.addAttribute("tareas", listaTareas);
		return ResponseEntity.ok(listaTareas);
	}

	@PostMapping("/saveTarea")
	public ModelAndView setSaveTareas(@ModelAttribute Tareas tareas) {
		template.postForEntity(api + "/addTareas", tareas, Integer.class);
		ModelAndView mav = new ModelAndView("redirect:/proyecto");
		mav.addObject("id_proyecto", tareas.getId_proyecto());
		return mav;
	}

	@PostMapping("/actualizarTarea")
	public ResponseEntity<Object> updateTarea(@RequestBody Tareas tarea) {
		Map<String, Integer> params = new HashMap<>();
		params.put("id_tarea", tarea.getId_tarea());
		Tareas tareas = template.getForObject(api + "/buscarTarea?id_tarea={id_tarea}", Tareas.class, params);
		return ResponseEntity.ok(tareas);
	}

	@PostMapping("/eliminarTarea")
	public ModelAndView eliminarTarea(@RequestBody Tareas tareas) {
		HttpEntity<Tareas> request = new HttpEntity<>(tareas);
		template.exchange(api + "/deleteTareas", HttpMethod.DELETE, request, Integer.class);
		return new ModelAndView("redirect:/proyecto");
	}

	@PostMapping("/verificarTarea")
	public ResponseEntity<Integer> verificarTarea(@RequestBody Tareas paramName) {
		return template.postForEntity(api + "/addTareas", paramName, Integer.class);
	}

	@PostMapping("/cambiosTareas")
	public ResponseEntity<Integer> cambiosTareas(@RequestBody Tareas paramDatos) {
		return template.postForEntity(api + "/addTareas", paramDatos, Integer.class);
	}

	@PostMapping("/consultarTareas")
	public ResponseEntity<Object> consultarTareas() {
		Tareas[] response = template.postForObject(api + "/buscarTareas", peticionVacia(), Tareas[].class);
		return ResponseEntity.ok(Arrays.asList(response));
	}

	// ======================= COMENTARIOS =======================

	@PostMapping("/obtenerComentarios")
	public ResponseEntity<List<Comentarios>> obtenerComentarios(@RequestBody Comentarios idComentario, Model modelo) {
		Comentarios[] response = template.postForObject(api + "/getComentario", idComentario, Comentarios[].class);
		List<Comentarios> listaComentarios = Arrays.asList(response);
		modelo.addAttribute("comentarios", listaComentarios);
		return ResponseEntity.ok(listaComentarios);
	}

	@PostMapping("/saveComentario")
	public String setSaveComentario(@RequestBody Comentarios comentarios) {
		template.postForEntity(api + "/addComentario", comentarios, Integer.class);
		return "proyecto";
	}

	@PostMapping("/consultarComentarios")
	public ResponseEntity<Object> consultarComentarios() {
		Comentarios[] response = template.postForObject(api + "/buscarComentarios", peticionVacia(), Comentarios[].class);
		return ResponseEntity.ok(Arrays.asList(response));
	}

	// ======================= MANEJO DE ERRORES =======================

	// F-02: la API no está disponible (apagada, puerto cerrado) -> 503 con mensaje
	@ExceptionHandler(ResourceAccessException.class)
	public ResponseEntity<Map<String, Object>> apiNoDisponible(ResourceAccessException ex) {
		return error(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo conectar con el servidor. Intente más tarde.");
	}

	// F-02: la API respondió con un error interno (5xx) -> 503 con mensaje
	@ExceptionHandler(HttpServerErrorException.class)
	public ResponseEntity<Map<String, Object>> errorDeApi(HttpServerErrorException ex) {
		return error(HttpStatus.SERVICE_UNAVAILABLE, "El servidor no pudo procesar la solicitud. Intente más tarde.");
	}

	// F-04 y F-05: errores 4xx de la API (404, 400, 409) se devuelven al navegador con el mismo código
	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<?> errorDeCliente(HttpClientErrorException ex) {
		String cuerpo = ex.getResponseBodyAsString();
		if (cuerpo != null && !cuerpo.isBlank()) {
			return ResponseEntity.status(ex.getStatusCode()).contentType(MediaType.APPLICATION_JSON).body(cuerpo);
		}
		String mensaje = ex.getStatusCode().value() == 404
				? "El recurso solicitado no existe."
				: "La solicitud no es válida.";
		return error(HttpStatus.valueOf(ex.getStatusCode().value()), mensaje);
	}

	private ResponseEntity<Map<String, Object>> error(HttpStatus status, String mensaje) {
		Map<String, Object> cuerpo = new HashMap<>();
		cuerpo.put("status", status.value());
		cuerpo.put("mensaje", mensaje);
		return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(cuerpo);
	}

	private boolean vacio(String s) {
		return s == null || s.trim().isEmpty();
	}
}