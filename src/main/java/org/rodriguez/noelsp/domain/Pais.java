package org.rodriguez.noelsp.domain;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Pais {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true)
	private String nombre;
	
	@OneToMany(mappedBy = "nace")
	private Collection<Persona> nacidos;
	
	//=================================
	
	public Pais(String nombre) {
		this.nombre = nombre;
		this.nacidos = new ArrayList<Persona>();
	}
	
	public Pais() {
		this.nacidos = new ArrayList<Persona>();
	}

	//================== NOMBRE ==================
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	//================== ID ==================
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	//================== NACIDOS ==================
	public Collection<Persona> getNacidos() {
		return nacidos;
	}

	public void setNacidos(Collection<Persona> nacidos) {
		this.nacidos = nacidos;
	}

}
