package com.mx.cbtis.gestor.dto;

import com.mx.cbtis.gestor.modelo.Asignaciones;

/**
 * DTO para transferencia segura de datos de Asignaciones en controladores REST (mitigación CWE-915 / Mass Assignment).
 */
public class AsignacionesDTO {

	private Integer id_asignacion;
	private Integer id_usuario;
	private Integer id_tarea;
	private String fecha_asignacion;

	public AsignacionesDTO() {
		super();
	}

	public AsignacionesDTO(Integer id_asignacion, Integer id_usuario, Integer id_tarea, String fecha_asignacion) {
		super();
		this.id_asignacion = id_asignacion;
		this.id_usuario = id_usuario;
		this.id_tarea = id_tarea;
		this.fecha_asignacion = fecha_asignacion;
	}

	public Asignaciones toEntity() {
		return new Asignaciones(id_asignacion, id_usuario, id_tarea, fecha_asignacion);
	}

	public Integer getId_asignacion() {
		return id_asignacion;
	}

	public void setId_asignacion(Integer id_asignacion) {
		this.id_asignacion = id_asignacion;
	}

	public Integer getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(Integer id_usuario) {
		this.id_usuario = id_usuario;
	}

	public Integer getId_tarea() {
		return id_tarea;
	}

	public void setId_tarea(Integer id_tarea) {
		this.id_tarea = id_tarea;
	}

	public String getFecha_asignacion() {
		return fecha_asignacion;
	}

	public void setFecha_asignacion(String fecha_asignacion) {
		this.fecha_asignacion = fecha_asignacion;
	}
}
