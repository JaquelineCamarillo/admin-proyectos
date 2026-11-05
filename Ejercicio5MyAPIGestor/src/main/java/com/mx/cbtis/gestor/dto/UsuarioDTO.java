package com.mx.cbtis.gestor.dto;

import com.mx.cbtis.gestor.modelo.Usuario;

/**
 * DTO para transferencia segura de datos de Usuario en controladores REST (mitigación CWE-915 / Mass Assignment).
 */
public class UsuarioDTO {

	private Integer id_usuario;
	private String nombre;
	private String correo_electronico;
	private String contrasena;
	private String rol;
	private String imagen_usuario;

	public UsuarioDTO() {
		super();
	}

	public UsuarioDTO(Integer id_usuario, String nombre, String correo_electronico, String contrasena, String rol,
			String imagen_usuario) {
		super();
		this.id_usuario = id_usuario;
		this.nombre = nombre;
		this.correo_electronico = correo_electronico;
		this.contrasena = contrasena;
		this.rol = rol;
		this.imagen_usuario = imagen_usuario;
	}

	public Usuario toEntity() {
		return new Usuario(id_usuario, nombre, correo_electronico, contrasena, rol, imagen_usuario);
	}

	public Integer getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(Integer id_usuario) {
		this.id_usuario = id_usuario;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCorreo_electronico() {
		return correo_electronico;
	}

	public void setCorreo_electronico(String correo_electronico) {
		this.correo_electronico = correo_electronico;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public String getImagen_usuario() {
		return imagen_usuario;
	}

	public void setImagen_usuario(String imagen_usuario) {
		this.imagen_usuario = imagen_usuario;
	}
}
