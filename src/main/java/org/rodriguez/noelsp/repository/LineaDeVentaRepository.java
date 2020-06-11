package org.rodriguez.noelsp.repository;

import org.rodriguez.noelsp.domain.LineaDeVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineaDeVentaRepository extends JpaRepository<LineaDeVenta, Long> {
}
