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

import com.mx.cbtis.gestor.modelo.Proyectos;
import com.mx.cbtis.gestor.repository.ProtectosRepository;

@RestController
public class ProyectosController {

	@Autowired
	ProtectosRepository proyectostable;

	// F-02: si el proyecto no existe se responde 404 en lugar de un objeto vacío
	@GetMapping("/buscarProyecto")
	public ResponseEntity<Proyectos> getProyecto(@RequestParam int id_proyecto) {
		Optional<Proyectos> proyecto = proyectostable.findById(id_proyecto);
		if (proyecto.isPresent()) {
			return ResponseEntity.ok(proyecto.get());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@PostMapping("/addProyecto")
	public ResponseEntity<Integer> addProyecto(@RequestBody Proyectos proyecto) {
		proyectostable.save(proyecto);
		return ResponseEntity.ok(1);
	}

	@PostMapping("/updateProyecto")
	public ResponseEntity<Integer> updateProyecto(@RequestBody Proyectos proyecto) {
		proyectostable.save(proyecto);
		return ResponseEntity.ok(1);
	}

	// F-04: solo se elimina si el registro existe
	@DeleteMapping("/deleteProyecto")
	public ResponseEntity<Integer> deleteProyecto(@RequestBody Proyectos proyecto) {
		if (proyecto.getId_proyecto() == null || !proyectostable.existsById(proyecto.getId_proyecto())) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
		}
		proyectostable.deleteById(proyecto.getId_proyecto());
		return ResponseEntity.ok(1);
	}

	@PostMapping("/buscarProyectos")
	public List<Proyectos> getProyectos() {
		List<Proyectos> listaProyectos = new ArrayList<>();
		proyectostable.findAll().forEach(listaProyectos::add);
		return listaProyectos;
	}
}