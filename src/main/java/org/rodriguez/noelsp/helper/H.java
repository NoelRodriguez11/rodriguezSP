package org.rodriguez.noelsp.helper;

import javax.servlet.http.HttpSession;

import org.rodriguez.noelsp.domain.Persona;
import org.rodriguez.noelsp.exception.DangerException;

public class H {
	/**
	 * 
	 * @param 	rol 				Tres posibilidades "anon", "auth", "admin"
	 * @param 	s   				la sesión activa
	 * @throws 	DangerException 	            si el rol no coincide con el del usuario activo
	 */
	public static void isRolOK(String rol, HttpSession s) throws DangerException {
		Persona persona = null;

		if (s.getAttribute("persona") != null) {
			persona = (Persona) s.getAttribute("persona");
		}

		if (persona == null) {
			if (!rol.equals("anon")) { // Para usos de “anon” exclusivo
				PRG.error("Rol inadecuado");
			}
		} else {
			if (!persona.getLoginname().equals("admin") && rol.equals("admin")) {
				PRG.error("Rol inadecuado");
			}
		}

	}
}

