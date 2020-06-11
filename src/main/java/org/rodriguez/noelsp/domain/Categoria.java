package org.rodriguez.noelsp.domain;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Categoria {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true)
	private String nombre;
	
	@OneToMany(mappedBy = "categoria")
	private Collection<Producto> pertenecen;

	//=================================

	public Categoria(String nombre) {
		this.nombre = nombre;
		this.pertenecen = new ArrayList<Producto>();
	}
	
	public Categoria() {
		this.pertenecen = new ArrayList<Producto>();
	}

	//================== ID ==================
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	//================== NOMBRE ==================
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	//================== PERTENECEN ==================
	public Collection<Producto> getPertenecen() {
		return pertenecen;
	}

	public void setPertenecen(Collection<Producto> pertenecen) {
		this.pertenecen = pertenecen;
	}
	
}