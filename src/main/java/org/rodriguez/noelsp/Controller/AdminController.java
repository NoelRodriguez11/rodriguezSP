package org.rodriguez.noelsp.Controller;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import java.nio.file.Files;
import java.nio.file.Path;

import org.rodriguez.noelsp.domain.Categoria;
import org.rodriguez.noelsp.domain.Pais;
import org.rodriguez.noelsp.domain.Persona;
import org.rodriguez.noelsp.domain.Producto;
import org.rodriguez.noelsp.exception.DangerException;
import org.rodriguez.noelsp.exception.InfoException;
import org.rodriguez.noelsp.helper.H;
import org.rodriguez.noelsp.helper.PRG;
import org.rodriguez.noelsp.repository.CategoriaRepository;
import org.rodriguez.noelsp.repository.PaisRepository;
import org.rodriguez.noelsp.repository.PersonaRepository;
import org.rodriguez.noelsp.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class AdminController {
	
	@Autowired
	private PaisRepository paisRepository;
	
	@Autowired
	private PersonaRepository personaRepository;
	
	@Autowired
	private ProductoRepository productoRepository;
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Value("${app.uploadFolder}")
	private String UPLOAD_FOLDER;
	
	
	//===================================================== PAIS ================================================================= //
	@GetMapping("/pais/c")
	public String cPaisGet(ModelMap m, HttpSession s) throws DangerException {
		H.isRolOK("admin", s);
		m.put("view","/pais/c");
		return "/_t/frame";
		
	}

	@PostMapping("/pais/c")
	public void cPaisPost(
			@RequestParam("nombre") String nombre,
			HttpSession s
			) throws DangerException, InfoException {
		H.isRolOK("admin", s);
		try {
		Pais pais = new Pais(nombre);
		paisRepository.save(pais);
		
		}
		
		catch (Exception e) {
			PRG.error("Error al crear " + nombre, "pais/r");
	}
		PRG.info("País "+nombre+" creado", "/pais/r" );
	}
	
	@GetMapping("/pais/r")
	public String rPaisGet(ModelMap m, HttpSession s) throws DangerException {
		H.isRolOK("admin", s);
		List<Pais> ordenados = paisRepository.findAllByOrderByNombreAsc();
		m.put("paises", ordenados);
		m.put("view","/pais/r");
		return "/_t/frame";
}
	
	@GetMapping("/pais/u")
	public String uPaisGet(@RequestParam("id") Long idPais, ModelMap m, HttpSession s) throws DangerException {
		H.isRolOK("admin", s);
		m.put("pais", paisRepository.getOne(idPais));
		m.put("view", "/pais/u");
		return "/_t/frame";
	}

	@PostMapping("/pais/u")
	public void uPaisPost(@RequestParam("nombre") String nombrePais, @RequestParam("id") Long idPais, HttpSession s)
			throws DangerException, InfoException {
		H.isRolOK("admin", s);
		try {
			Pais pais = paisRepository.getOne(idPais);
			pais.setNombre(nombrePais);
			paisRepository.save(pais);
		} catch (Exception e) {
			PRG.error("País " + nombrePais + " duplicado", "/pais/r");
		}
		PRG.info("País " + nombrePais + " actualizado correctamente", "/pais/r");
	}
	
	@PostMapping("/pais/d")
	public String dPaisPost(@RequestParam("id") Long idPais, ModelMap m, HttpSession s) throws DangerException {
		H.isRolOK("admin", s);
		String nombrePais = "----";
		try {
			paisRepository.delete(paisRepository.getOne(idPais));
		} catch (Exception e) {
			PRG.error("Error al borrar el pais", "/pais/r");
		}
		return "redirect:/pais/r";
		
	}

	
	//===================================================== PERSONA ================================================================= //
	@GetMapping("/persona/r")
	public String rPersonaGet(ModelMap m, HttpSession s) throws DangerException {
		H.isRolOK("admin", s);
		m.put("paises", paisRepository.findAll());
		m.put("personas", personaRepository.findAll());
		m.put("uploadFolder", UPLOAD_FOLDER);
		m.put("view","/persona/r");
		return "/_t/frame";
}
	
	@PostMapping("/persona/d")
	public String dPersonaPost(@RequestParam("id") Long idPersona, ModelMap m, HttpSession s) throws DangerException {
		H.isRolOK("admin", s);
		try {
			personaRepository.delete(personaRepository.getOne(idPersona));
		} catch (Exception e) {
			PRG.error("Error al borrar la persona", "/persona/r");
		}
		return "redirect:/persona/r";
		
	}
	
	@GetMapping("/persona/u")
	public String uPersonaGet(@RequestParam("id") Long idPersona, ModelMap m, HttpSession s) throws DangerException {
		H.isRolOK("admin", s);
		m.put("persona", personaRepository.getOne(idPersona));
		m.put("paises", paisRepository.findAll());
		m.put("view", "persona/u");
		return "_t/frame";
	}
	
	@PostMapping("/persona/u")
	public void uPersonaPost(
			@RequestParam("id") Long idPersona,
			@RequestParam("loginname") String loginname, 
			@RequestParam("nombre") String nombre,
			@RequestParam(value = "altura", required = false) Integer altura,
			@RequestParam(value = "fnac", required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fnac,
			@RequestParam(value = "idPais", required = false) Long idPais,
			HttpSession s) throws DangerException, InfoException {
		H.isRolOK("admin", s);
		try {
			
			Persona persona = personaRepository.getOne(idPersona);
			
			if (idPais != null) {
				Pais paisNacimiento = paisRepository.getOne(idPais);
				paisNacimiento.getNacidos().add(persona);
				persona.setNace(paisNacimiento);
			}
		
			persona.setLoginname(loginname);
			persona.setNombre(nombre);
			persona.setAltura(altura);
			persona.setFnac(fnac);			
			
			personaRepository.save(persona);
			
		} catch (Exception e) {
			PRG.error("Error al actualizar, persona " + nombre + "duplicada ", "/persona/r");
		}
		PRG.info("Persona " + nombre + " actualizada correctamente", "/persona/r");
	}

	//===================================================== PRODUCTO ================================================================= //
	
	@PostMapping("/producto/d")
	public String dProductoPost(@RequestParam("id") Long idProducto, ModelMap m, HttpSession s) throws DangerException {

		H.isRolOK("admin", s);
		try {
			productoRepository.delete(productoRepository.getOne(idProducto));
		} catch (Exception e) {
			PRG.error("Error al borrar el producto", "/producto/r");
		}

		return "redirect:/producto/r";

	}

	@GetMapping("/producto/u")
	public String uProductoGet(@RequestParam("id") Long idProducto, ModelMap m, HttpSession s) throws DangerException {
		H.isRolOK("admin", s);
		m.put("producto", productoRepository.getOne(idProducto));
		m.put("categorias", categoriaRepository.findAll());
		m.put("view", "producto/u");
		return "_t/frame";
	}
	
	@PostMapping("/producto/u")
	public void uProductoPost(
			@RequestParam("id") Long idProducto,
			@RequestParam("nombre") String nombre, 
			@RequestParam("stock") Integer stock,
			@RequestParam("precio") Double precio,
			@RequestParam(value = "idCategoria", required = false) Long idCategoria,
			HttpSession s) throws DangerException, InfoException {
		H.isRolOK("admin", s);
		try {
			Producto producto = productoRepository.getOne(idProducto);
			
			producto.setNombre(nombre);
			producto.setStock(stock);
			producto.setPrecio(precio);
			
			Categoria categoria = categoriaRepository.getOne(idCategoria);
			categoria.getPertenecen().add(producto);
			producto.setCategoria(categoria);
			
			productoRepository.save(producto);
			
		} catch (Exception e) {
			PRG.error("Error al actualizar, producto " + nombre + "duplicada ", "/producto/r");
		}
		PRG.info("Producto " + nombre + " actualizada correctamente", "/producto/r");
	}

	@GetMapping("/producto/c")
	public String cProductoGet(ModelMap m, HttpSession s) throws DangerException {
		H.isRolOK("admin", s);
		m.put("categorias", categoriaRepository.findAll());
		m.put("view", "producto/c");
		return "_t/frame";
	}

	@PostMapping("/producto/c")
	public String cProductoPost(
			@RequestParam("nombre") String nombre,
			@RequestParam(value = "foto", required = false) MultipartFile foto,
			@RequestParam("stock") Integer stock,
			@RequestParam("precio") Double precio,
			@RequestParam(value = "idCategoria", required = false) Long idCategoria, HttpSession s
			) throws DangerException, InfoException {
		
		if (idCategoria != null) {
		
		try {
			
			H.isRolOK("admin", s);
			
			String ext_foto = null;
			ext_foto = foto.getOriginalFilename().split("\\.")[1];
			
			Producto producto = (new Producto(nombre, ext_foto, stock, precio));
			
			Categoria categoria = categoriaRepository.getOne(idCategoria);
			categoria.getPertenecen().add(producto);
			producto.setCategoria(categoria);
			
			productoRepository.save(producto);
			
				byte[] contenido = foto.getBytes();
				Path path = Paths.get(UPLOAD_FOLDER + "producto-" + producto.getId() + "." + producto.getFoto());
				Files.write(path, contenido);
			
			
			//PRG.info("Producto "+nombre+" creado");
		}
		catch (Exception e) {
			PRG.error("Error al crear el producto "+nombre, "producto/r");
			
		}
		}
		else {
		PRG.info("Debes asociar el producto a una categoría");
		}
		return "redirect:/producto/r";
	}

	@GetMapping("/producto/r")
	public String rProductoGet(HttpSession s, ModelMap m) throws DangerException {
		H.isRolOK("admin", s);
		m.put("productos", productoRepository.findAllByOrderByCategoriaNombreAscNombreAsc());
		m.put("view", "producto/r");
		return "_t/frame";
	}
	//===================================================== CATEGORIA ================================================================= //
	
	@GetMapping("/categoria/c")
	public String cCategoriaGet(ModelMap m, HttpSession s) throws DangerException {
		H.isRolOK("admin", s);
		m.put("view", "/categoria/c");
		return "/_t/frame";
	}

	@PostMapping("/categoria/c")
	public void cCategoriaPost(@RequestParam("nombre") String nombre, HttpSession s) throws DangerException, InfoException {
		H.isRolOK("admin", s);
		try {
			categoriaRepository.save(new Categoria(nombre));
		} catch (Exception e) {
			PRG.error("Categoria " + nombre + " duplicada", "/categoria/c");
		}
		PRG.info("Categoria " + nombre + " creada correctamente", "/categoria/r");
	}
	
	@GetMapping("/categoria/r")
	public String rCategoriaGet(ModelMap m, HttpSession s) throws DangerException {
		H.isRolOK("admin", s);
		List<Categoria> categorias = categoriaRepository.findAll();
		m.put("categorias", categorias);

		m.put("view", "/categoria/r");
		return "/_t/frame";
	}
	
	@GetMapping("/categoria/u")
	public String uCategoriaGet(@RequestParam("id") Long id, ModelMap m, HttpSession s) throws DangerException {
		H.isRolOK("admin", s);
		m.put("categoria", categoriaRepository.getOne(id));
		m.put("view", "/categoria/u");
		return "/_t/frame";
	}

	@PostMapping("/categoria/u")
	public void updatePost(@RequestParam("nombre") String nombre, @RequestParam("id") Long id, HttpSession s)
			throws DangerException, InfoException {
		H.isRolOK("admin", s);
		try {
			Categoria categoria = categoriaRepository.getOne(id);
			categoria.setNombre(nombre);
			categoriaRepository.save(categoria);
		} catch (Exception e) {
			PRG.error("Categoria " + nombre + " duplicada", "/categoria/r");
		}
		PRG.info("Categoria " + nombre + " actualizada correctamente", "/categoria/r");
	}
	
	@PostMapping("/categoria/d")
	public String dCategoriaPost(@RequestParam("id") Long id, HttpSession s) throws DangerException {
		H.isRolOK("admin", s);
		String nombre = null;
		try {
			Categoria categoria = categoriaRepository.getOne(id);
			nombre = categoria.getNombre();
			categoriaRepository.delete(categoria);
		} catch (Exception e) {
			PRG.error("Error al borrar la categoria " + nombre, "/categoria/r");
		}
		return "redirect:/categoria/r";
	}
	//===================================================== AJAX ================================================================= //	
	
	@ResponseBody
	@PostMapping(value="/producto/lanzarAJAX", produces="text/plain")
	public String lanzarAJAX(
			@RequestParam String nombreProducto
			) throws JsonProcessingException{
		
		HashMap<String, Integer> nombre = new HashMap<>();
		
			if (productoRepository.getByNombre(nombreProducto) != null) {
				nombre.put("coincide", 1);
			}
			else {
				nombre.put("coincide", 0);
			}
					
		return new ObjectMapper().writeValueAsString(nombre);
		
	}
}

