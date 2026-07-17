package com.uteq.pfc.repository;

import com.uteq.pfc.entity.Entidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Capa Repository (patron arquitectura en capas, GA punto 2c).
 * JpaSpecificationExecutor habilita busqueda con filtros multiples
 * dinamicos (Paso 2.3 CRUD con filtros).
 */
@Repository
public interface EntidadRepository extends JpaRepository<Entidad, Long>,
        JpaSpecificationExecutor<Entidad> {
}
