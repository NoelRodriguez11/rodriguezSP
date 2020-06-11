package org.rodriguez.noelsp.repository;

import java.util.List;

import org.rodriguez.noelsp.domain.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Long> {

	List<Pais> findAllByOrderByNombreAsc();
}
