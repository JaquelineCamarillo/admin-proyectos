package com.mx.cbtis.gestor.dto;

import com.mx.cbtis.gestor.modelo.Tareas;

/**
 * DTO para transferencia segura de datos de Tareas en controladores REST (mitigación CWE-915 / Mass Assignment).
 */
public class TareasDTO {

	private Integer id_tarea;
	private String nombre;
	private String descripcion;
	private String fecha_vencimiento;
	private String lista;
	private String estado;
	private Integer id_proyecto;

	public TareasDTO() {
		super();
	}

	public TareasDTO(Integer id_tarea, String nombre, String descripcion, String fecha_vencimiento, String lista,
			String estado, Integer id_proyecto) {
		super();
		this.id_tarea = id_tarea;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.fecha_vencimiento = fecha_vencimiento;
		this.lista = lista;
		this.estado = estado;
		this.id_proyecto = id_proyecto;
	}

	public Tareas toEntity() {
		return new Tareas(id_tarea, nombre, descripcion, fecha_vencimiento, lista, estado, id_proyecto);
	}

	public Integer getId_tarea() {
		return id_tarea;
	}

	public void setId_tarea(Integer id_tarea) {
		this.id_tarea = id_tarea;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getFecha_vencimiento() {
		return fecha_vencimiento;
	}

	public void setFecha_vencimiento(String fecha_vencimiento) {
		this.fecha_vencimiento = fecha_vencimiento;
	}

	public String getLista() {
		return lista;
	}

	public void setLista(String lista) {
		this.lista = lista;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Integer getId_proyecto() {
		return id_proyecto;
	}

	public void setId_proyecto(Integer id_proyecto) {
		this.id_proyecto = id_proyecto;
	}
}
