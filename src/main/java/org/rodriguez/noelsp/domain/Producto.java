package org.rodriguez.noelsp.domain;


import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Producto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String nombre;

	private String foto;

	private Integer stock;

	private Double precio;
	
	@ManyToOne Categoria categoria;
	
	@OneToMany(mappedBy = "producto")
	private Collection<LineaDeVenta> lineaDeVenta;

	// ===========================

	public Producto(String nombre, String foto, Integer stock, Double precio) {
		super();
		this.nombre = nombre;
		this.foto = foto;
		this.stock = stock;
		this.precio = precio;
		this.lineaDeVenta = new ArrayList<LineaDeVenta>();
	}
	public Producto() {
		this.lineaDeVenta = new ArrayList<LineaDeVenta>();
	}

	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public Categoria getCategoria() {
		return categoria;
	}
	
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	
	public Collection<LineaDeVenta> getLineaDeVenta() {
		return lineaDeVenta;
	}

	public void setLineaDeVenta(Collection<LineaDeVenta> lineaDeVenta) {
		this.lineaDeVenta = lineaDeVenta;
	}
	
	

	// ========================

}
