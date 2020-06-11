package org.rodriguez.noelsp.repository;
import org.rodriguez.noelsp.domain.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
	public Persona getByLoginname(String loginname);
}
