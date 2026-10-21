package com.mx.cbtis.gestor.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mx.cbtis.gestor.modelo.Tareas;
import com.mx.cbtis.gestor.repository.TareasRepository;

@RestController
public class TareasController {

	private static final DateTimeFormatter FORMATO_FECHA =
			DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

	@Autowired
	TareasRepository tareastable;

	@PostMapping("/getTarea")
	public List<Tareas> getTarea(@RequestBody Tareas id_proyecto) {
		return tareastable.findByIdProyecto(id_proyecto.getId_proyecto());
	}

	// F-02: si la tarea no existe se responde 404 en lugar de un objeto vacío
	@GetMapping("/buscarTarea")
	public ResponseEntity<Tareas> getTarea(@RequestParam int id_tarea) {
		Optional<Tareas> tarea = tareastable.findById(id_tarea);
		if (tarea.isPresent()) {
			return ResponseEntity.ok(tarea.get());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	// F-05: se valida el formato de la fecha antes de guardar
	@PostMapping("/addTareas")
	public ResponseEntity<Integer> addTareas(@RequestBody Tareas tareas) {
		normalizarFecha(tareas);
		if (!fechaValida(tareas.getFecha_vencimiento())) {
			return ResponseEntity.badRequest().body(0);
		}
		tareastable.save(tareas);
		return ResponseEntity.ok(1);
	}

	@PostMapping("/updateTareas")
	public ResponseEntity<Integer> updateTareas(@RequestBody Tareas tareas) {
		normalizarFecha(tareas);
		if (!fechaValida(tareas.getFecha_vencimiento())) {
			return ResponseEntity.badRequest().body(0);
		}
		tareastable.save(tareas);
		return ResponseEntity.ok(1);
	}

	// F-04: solo se elimina si el registro existe
	@DeleteMapping("/deleteTareas")
	public ResponseEntity<Integer> deleteTareas(@RequestBody Tareas tareas) {
		if (tareas.getId_tarea() == null || !tareastable.existsById(tareas.getId_tarea())) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
		}
		tareastable.deleteById(tareas.getId_tarea());
		return ResponseEntity.ok(1);
	}

	@PostMapping("/buscarTareas")
	public List<Tareas> getTareas() {
		List<Tareas> listaTareas = new ArrayList<>();
		tareastable.findAll().forEach(listaTareas::add);
		return listaTareas;
	}

	// Práctica 2 (INT-07): el formulario del front envía "" cuando no se elige fecha;
	// se guarda como null en lugar de mandar una cadena vacía a la columna DATE
	private void normalizarFecha(Tareas tareas) {
		String f = tareas.getFecha_vencimiento();
		if (f != null && f.trim().isEmpty()) {
			tareas.setFecha_vencimiento(null);
		}
	}

	// La fecha es opcional; si viene, debe ser una fecha real con formato yyyy-MM-dd
	private boolean fechaValida(String fecha) {
		if (fecha == null || fecha.trim().isEmpty()) {
			return true;
		}
		try {
			LocalDate.parse(fecha.trim(), FORMATO_FECHA);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}
}