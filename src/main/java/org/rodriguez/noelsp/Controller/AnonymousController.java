package org.rodriguez.noelsp.Controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import javax.servlet.http.HttpSession;

import org.rodriguez.noelsp.domain.Pais;
import org.rodriguez.noelsp.domain.Persona;
import org.rodriguez.noelsp.domain.Venta;
import org.rodriguez.noelsp.exception.DangerException;
import org.rodriguez.noelsp.exception.InfoException;
import org.rodriguez.noelsp.helper.H;
import org.rodriguez.noelsp.helper.PRG;
import org.rodriguez.noelsp.repository.PaisRepository;
import org.rodriguez.noelsp.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AnonymousController {
	
	@Autowired
	private PersonaRepository personaRepository;
	
	@Autowired
	private PaisRepository paisRepository;
	
	@Value("${app.uploadFolder}")
	private String UPLOAD_FOLDER;
	
	@GetMapping("/init")
	public String initGet(ModelMap m) throws DangerException {
		if(personaRepository.findAll().size() !=0) {
		PRG.error("BD no vacía. No puedo inicializar");
	}
	m.put("view", "/anon/init");
	return "/_t/frame";
	}
	
	@PostMapping("/init")
	public String initPost(@RequestParam("password") String password, ModelMap m) throws DangerException {
		if(personaRepository.getByLoginname("admin") != null) {
			PRG.error("Operacion no valida. BD no vacia");
		}
		BCryptPasswordEncoder bpe = new BCryptPasswordEncoder();
		if(!bpe.matches(password, bpe.encode("admin"))) {
			PRG.error("Contraseña incorrecta", "/init");
		}
		
		personaRepository.deleteAll();
		paisRepository.deleteAll();		
		personaRepository.save(new Persona("admin", "admin", "admin", 0, null, null));
		return "redirect:/";
	}
	
	@GetMapping("/info")
	public String info(HttpSession s, ModelMap m) {

		String mensaje = s.getAttribute("_mensaje") != null ? (String) s.getAttribute("_mensaje")
				: "Pulsa para volver a home";
		String severity = s.getAttribute("_severity") != null ? (String) s.getAttribute("_severity") : "info";
		String link = s.getAttribute("_link") != null ? (String) s.getAttribute("_link") : "/";

		s.removeAttribute("_mensaje");
		s.removeAttribute("_severity");
		s.removeAttribute("_link");

		m.put("mensaje", mensaje);
		m.put("severity", severity);
		m.put("link", link);

		m.put("view", "/_t/info");
		return "/_t/frame";
	}

	@GetMapping("/")
	public String home(ModelMap m) {
		m.put("view", "/anon/home");
		return "/_t/frame";
	}
	
	@GetMapping("/login")
	public String loginGet(ModelMap m, HttpSession s) throws DangerException {
		H.isRolOK("anon", s);
		m.put("view", "/anon/login");
		return "/_t/frame";
	}

	
	@PostMapping("/login")
	public String loginPost (
			@RequestParam("loginname") String loginname,
			@RequestParam("password") String password,
			ModelMap m, HttpSession s
			) throws DangerException {
		H.isRolOK("anon", s);
		
		try {
			Persona persona = personaRepository.getByLoginname(loginname);
			if(!(new BCryptPasswordEncoder()).matches(password, persona.getPassword())) {
				throw new Exception();
			}
			s.setAttribute("persona", persona);
		} catch (Exception e) {
			PRG.error("Usuario o contraseña incorrecta", "/login");
		}
		return "redirect:/";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession s) throws DangerException {
		H.isRolOK("auth", s);
	
		s.invalidate(); 
		return "redirect:/";
	}
	
	
	//================================
	@GetMapping("/persona/c")
	public String cPersonaGet(
			ModelMap m
			) {
		m.put("paises", paisRepository.findAll());
		m.put("personas", personaRepository.findAll());
		m.put("view", "/persona/c");
		return "/_t/frame";
	}
	
	@PostMapping("/persona/c")
	public void cPersonaPost(
			@RequestParam("loginname") String loginname,
			@RequestParam("password") String password,
			@RequestParam("nombre") String nombre,
			@RequestParam(value = "altura", required = false) Integer altura,
			@RequestParam(value = "fnac", required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
			LocalDate fnac,
			@RequestParam("foto") MultipartFile foto,
			@RequestParam(value = "idPais", required = true) Long idPais
			
			) throws DangerException, InfoException {
		try {
			String ext_foto = null;
			if(!foto.isEmpty()) {
				ext_foto = (foto.getOriginalFilename().split("\\."))[1];	
			}
			
			Persona persona = (new Persona(loginname, password, nombre, altura, fnac, ext_foto));
				
				if (idPais != null) {
					Pais paisNacimiento = paisRepository.getOne(idPais);
					paisNacimiento.getNacidos().add(persona);
					persona.setNace(paisNacimiento);
				}
									
				Venta ventaEnCurso = new Venta(LocalDate.now());
				ventaEnCurso.setPersona(persona);
				persona.setVentaencurso(ventaEnCurso);
				
				personaRepository.save(persona);
				
				if(ext_foto !=null) {
					byte[] contenido = foto.getBytes();
					Path path = Paths.get(UPLOAD_FOLDER + "persona-" + persona.getId() + "." + persona.getFoto());
					Files.write(path, contenido);
					}
				
		}
		catch (Exception e) {
			PRG.error(e.getMessage(), "/persona/c");
		}
		PRG.info("Persona "+nombre+" creado");
	}
}
