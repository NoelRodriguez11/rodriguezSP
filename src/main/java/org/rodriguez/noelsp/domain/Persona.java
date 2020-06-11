package org.rodriguez.noelsp.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
public class Persona {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nombre;
	
	@Column(unique = true)
	private String loginname;
	
	private String password;

	private Integer altura;
	
	private LocalDate fnac;
	
	private String foto;
	
	@ManyToOne
	private Pais nace;
	
	@OneToMany(mappedBy = "persona")
	private Collection<Venta> ventas;
	
	@OneToOne(cascade = CascadeType.PERSIST)
	private Venta ventaencurso;
	
	//=================================
	
	public Persona(String loginname, String nombre, String password, Integer altura, LocalDate fnac, String foto ) {
		super();
		this.loginname = loginname;
		this.nombre = nombre;
		this.password = new BCryptPasswordEncoder().encode(password);
		this.altura = altura;
		this.fnac = fnac;
		this.foto = foto;
		this.ventas = new ArrayList<Venta>();
		this.ventaencurso = new Venta();
		this.ventaencurso.setPersonaencurso(this);
	}
	
	public Persona() {
		
	}
	
	public Persona(String loginname, String password) {
		super();
		this.loginname = loginname;
		this.password = new BCryptPasswordEncoder().encode(password);
	}
	
	public Persona(String nombre) {
		this.nombre = nombre;
		this.ventas = new ArrayList<Venta>();
		this.ventaencurso = new Venta();
		this.ventaencurso.setPersonaencurso(this);
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

	//================== LOGINNAME ==================
	public String getLoginname() {
		return loginname;
	}


	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	//================== PASSWORD ==================
	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = (new BCryptPasswordEncoder()).encode(password);
	}

	//================== ALTURA ==================
	public Integer getAltura() {
		return altura;
	}


	public void setAltura(Integer altura) {
		this.altura = altura;
	}

	//================== FNAC ==================
	public LocalDate getFnac() {
		return fnac;
	}


	public void setFnac(LocalDate fnac) {
		this.fnac = fnac;
	}

	//================== FOTO ==================
	public String getFoto() {
		return foto;
	}


	public void setFoto(String foto) {
		this.foto = foto;
	}

	//================== PAIS ==================
	public Pais getNace() {
		return nace;
	}


	public void setNace(Pais nace) {
		this.nace = nace;
	}
	
	//================== VENTA ==================
	public Collection<Venta> getVentas() {
		return ventas;
	}
	
	public void setVentas(Collection<Venta> ventas) {
		this.ventas = ventas;
	}

	//================== VENTAENCURSO ==================
	public Venta getVentaencurso() {
		return ventaencurso;
	}

	public void setVentaencurso(Venta ventaencurso) {
		this.ventaencurso = ventaencurso;
	}
	
}
