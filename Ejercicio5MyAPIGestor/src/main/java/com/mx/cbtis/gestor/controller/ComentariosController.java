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

import com.mx.cbtis.gestor.modelo.Comentarios;
import com.mx.cbtis.gestor.repository.ComentariosRepository;

@RestController
public class ComentariosController {

	@Autowired
	ComentariosRepository comentariostable;

	@PostMapping("/getComentario")
	public List<Comentarios> getComentario(@RequestBody Comentarios idComentario) {
		return comentariostable.findByTarea(idComentario.getId_tarea());
	}

	// F-02: si el comentario no existe se responde 404 en lugar de un objeto vacío
	@GetMapping("/buscarComentario")
	public ResponseEntity<Comentarios> getComentario(@RequestParam int id) {
		Optional<Comentarios> comentario = comentariostable.findById(id);
		if (comentario.isPresent()) {
			return ResponseEntity.ok(comentario.get());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@PostMapping("/addComentario")
	public ResponseEntity<Integer> addComentario(@RequestBody Comentarios comentario) {
		comentariostable.save(comentario);
		return ResponseEntity.ok(1);
	}

	@PostMapping("/updateComentario")
	public ResponseEntity<Integer> updateComentario(@RequestBody Comentarios comentario) {
		comentariostable.save(comentario);
		return ResponseEntity.ok(1);
	}

	// F-04: solo se elimina si el registro existe
	@DeleteMapping("/deleteComentario")
	public ResponseEntity<Integer> deleteComentario(@RequestBody Comentarios comentario) {
		if (comentario.getId_comentario() == null || !comentariostable.existsById(comentario.getId_comentario())) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
		}
		comentariostable.deleteById(comentario.getId_comentario());
		return ResponseEntity.ok(1);
	}

	@PostMapping("/buscarComentarios")
	public List<Comentarios> getComentarios() {
		List<Comentarios> listaComentarios = new ArrayList<>();
		comentariostable.findAll().forEach(listaComentarios::add);
		return listaComentarios;
	}
}