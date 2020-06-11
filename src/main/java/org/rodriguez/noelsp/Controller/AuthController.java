package org.rodriguez.noelsp.Controller;

import java.time.LocalDate;

import javax.servlet.http.HttpSession;

import org.rodriguez.noelsp.domain.LineaDeVenta;
import org.rodriguez.noelsp.domain.Persona;
import org.rodriguez.noelsp.domain.Producto;
import org.rodriguez.noelsp.domain.Venta;
import org.rodriguez.noelsp.exception.DangerException;
import org.rodriguez.noelsp.exception.InfoException;
import org.rodriguez.noelsp.helper.H;
import org.rodriguez.noelsp.helper.PRG;
import org.rodriguez.noelsp.repository.LineaDeVentaRepository;
import org.rodriguez.noelsp.repository.PersonaRepository;
import org.rodriguez.noelsp.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
	
	@Autowired 
	private PersonaRepository personaRepository;
	
	@Autowired 
	ProductoRepository productoRepository;
	
	@Autowired
	LineaDeVentaRepository lineadeventaRepository;

	@GetMapping("/persona/rAuth")
	public String rAuthPersonaGet(ModelMap m, HttpSession s) throws DangerException {
		H.isRolOK("Auth", s); 
		Persona persona = (Persona) s.getAttribute("persona");
		m.put("persona", personaRepository.getByLoginname(persona.getLoginname()));
		m.put("view", "/persona/rAuth");
		return "/_t/frame";
			}
	
	@GetMapping("/carrito/c")
	public String cCarritoGet(ModelMap m, HttpSession s) throws DangerException {
		H.isRolOK("Auth", s);
		m.put("productos", productoRepository.findAll());
		m.put("view", "carrito/c");
		return "_t/frame";
	}
	
	@PostMapping("/carrito/c")
	public String carritoAddPost(
			@RequestParam("idProducto") Long id, 
			@RequestParam("cantidad") Integer cantidad,
			HttpSession s) throws DangerException {

		try {
			Producto producto = productoRepository.getOne(id);
			if (cantidad > producto.getStock() ) {
				throw new Exception("Stock insuficiente");
			}
			
			Persona persona = (Persona) s.getAttribute("persona");
			Venta venta = persona.getVentaencurso();
			LineaDeVenta lineadeventa = new LineaDeVenta(cantidad);

			
			lineadeventa.setVenta(venta);
			venta.getLdvs().add(lineadeventa);

			lineadeventa.setProducto(producto);
			producto.getLineaDeVenta().add(lineadeventa);
			producto.setStock(producto.getStock()-cantidad);

			lineadeventaRepository.save(lineadeventa);
		} catch (Exception e) {
			PRG.error("Error al añadir producto al carrito: "+e.getMessage(),"/carrito/r");
		}
		return "redirect:/carrito/r";
	}
	
	@GetMapping("/carrito/r")
	public String carritoR(ModelMap m, HttpSession s) throws DangerException {
		H.isRolOK("auth", s);
		Persona persona = (Persona)s.getAttribute("persona");
		m.put("venta", persona.getVentaencurso());
		m.put("view", "auth/carritoR");
		return "_t/frame";
	}
	
	@PostMapping("/carrito/comprar")
	public void comprar(
			HttpSession s
			) throws DangerException, InfoException {
		try {
			// Hacer la compra
			Persona persona = (Persona)s.getAttribute("persona");
			Venta venta = persona.getVentaencurso();
			
			persona.getVentas().add(venta);
			venta.setPersona(persona);
			venta.setFecha(LocalDate.now());
			venta.setPersonaencurso(null);
			
			Venta nuevaVenta = new Venta();
			persona.setVentaencurso(nuevaVenta);
			nuevaVenta.setPersonaencurso(persona);
			personaRepository.save(persona);
			
		}
		catch (Exception e) {
			PRG.error("Error al realizar la compra","/producto/r");
		}
		PRG.info("Compra realizada","/producto/r");
	}

}
