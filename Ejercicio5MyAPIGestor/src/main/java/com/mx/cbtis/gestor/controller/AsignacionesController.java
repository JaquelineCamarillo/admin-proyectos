package com.mx.cbtis.gestor.controller;

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

import com.mx.cbtis.gestor.modelo.Asignaciones;
import com.mx.cbtis.gestor.repository.AsignacionesRepository;

@RestController
public class AsignacionesController {

	@Autowired
	AsignacionesRepository asignacionestable;

	// F-02: si la asignación no existe se responde 404 en lugar de un objeto vacío
	@GetMapping("/buscarAsignacion")
	public ResponseEntity<Asignaciones> getAsignacion(@RequestParam int id) {
		Optional<Asignaciones> asignacion = asignacionestable.findById(id);
		if (asignacion.isPresent()) {
			return ResponseEntity.ok(asignacion.get());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	// F-01: si id_usuario o id_tarea no existen, la llave foránea lo rechaza
	// y GlobalExceptionHandler responde 409 Conflict
	@PostMapping("/addAsignacion")
	public ResponseEntity<Integer> addAsignacion(@RequestBody Asignaciones asignacion) {
		asignacionestable.save(asignacion);
		return ResponseEntity.ok(1);
	}

	@PostMapping("/updateAsignacion")
	public ResponseEntity<Integer> updateAsignacion(@RequestBody Asignaciones asignacion) {
		asignacionestable.save(asignacion);
		return ResponseEntity.ok(1);
	}

	// F-04: solo se elimina si el registro existe
	@DeleteMapping("/deleteAsignacion")
	public ResponseEntity<Integer> deleteAsignacion(@RequestBody Asignaciones asignacion) {
		if (asignacion.getId_asignacion() == null || !asignacionestable.existsById(asignacion.getId_asignacion())) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
		}
		asignacionestable.deleteById(asignacion.getId_asignacion());
		return ResponseEntity.ok(1);
	}

	@PostMapping("/buscarAsignaciones")
	public List<Asignaciones> getAsignaciones() {
		List<Asignaciones> listaAsignaciones = new ArrayList<>();
		asignacionestable.findAll().forEach(listaAsignaciones::add);
		return listaAsignaciones;
	}
}