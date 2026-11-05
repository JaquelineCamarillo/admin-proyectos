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

import com.mx.cbtis.gestor.dto.UsuarioDTO;
import com.mx.cbtis.gestor.modelo.Usuario;
import com.mx.cbtis.gestor.repository.UsuarioRepository;

@RestController
public class UsuarioController {

	@Autowired
	UsuarioRepository usuariotable;

	@PostMapping("/getUsuarioLogin")
	public Usuario getUsuarioLogin(@RequestBody UsuarioDTO userParam) {

		Optional<Usuario> optionaluser = usuariotable.findByCorreoPass(
				userParam != null ? userParam.getCorreo_electronico() : null,
				userParam != null ? userParam.getContrasena() : null);

		Usuario user = new Usuario();

		if (optionaluser.isPresent()) {
			user = optionaluser.get();
		} else {
			// Contrato que usa el front (login.js) para mostrar la alerta
			user.setNombre("000");
		}

		return user;
	}

	// F-02: si el usuario no existe se responde 404 en lugar de un objeto vacío
	@GetMapping("/buscarUsuario")
	public ResponseEntity<Usuario> getUsuario(@RequestParam int id) {
		Optional<Usuario> usuario = usuariotable.findById(id);
		if (usuario.isPresent()) {
			return ResponseEntity.ok(usuario.get());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	// F-03: se validan los campos obligatorios antes de guardar
	@PostMapping("/addUsuario")
	public ResponseEntity<Integer> addUsuario(@RequestBody(required = false) UsuarioDTO usuarioDTO) {
		if (usuarioDTO == null) {
			return ResponseEntity.badRequest().body(0);
		}
		Usuario usuario = usuarioDTO.toEntity();
		if (!esValido(usuario)) {
			return ResponseEntity.badRequest().body(0);
		}
		usuariotable.save(usuario);
		return ResponseEntity.ok(1);
	}

	@PostMapping("/updateUsuario")
	public ResponseEntity<Integer> updateUsuario(@RequestBody(required = false) UsuarioDTO usuarioDTO) {
		if (usuarioDTO == null) {
			return ResponseEntity.badRequest().body(0);
		}
		Usuario usuario = usuarioDTO.toEntity();
		if (!esValido(usuario)) {
			return ResponseEntity.badRequest().body(0);
		}
		usuariotable.save(usuario);
		return ResponseEntity.ok(1);
	}

	// F-04: solo se elimina si el registro existe
	@DeleteMapping("/deleteUsuario")
	public ResponseEntity<Integer> deleteUsuario(@RequestBody(required = false) UsuarioDTO usuarioDTO) {
		if (usuarioDTO == null || usuarioDTO.getId_usuario() == null || !usuariotable.existsById(usuarioDTO.getId_usuario())) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
		}
		usuariotable.deleteById(usuarioDTO.getId_usuario());
		return ResponseEntity.ok(1);
	}

	@PostMapping("/buscarUsuarios")
	public List<Usuario> getUsuarios() {
		List<Usuario> listaUsuarios = new ArrayList<>();
		usuariotable.findAll().forEach(listaUsuarios::add);
		return listaUsuarios;
	}

	private boolean esValido(Usuario u) {
		return u != null
				&& !vacio(u.getNombre())
				&& !vacio(u.getCorreo_electronico())
				&& !vacio(u.getContrasena());
	}

	private boolean vacio(String s) {
		return s == null || s.trim().isEmpty();
	}
}
