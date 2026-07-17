package com.uteq.pfc.repository;

import com.uteq.pfc.entity.Entidad;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/**
 * Paso 2.3 - CRUD con busqueda con filtros multiples.
 * Cada metodo devuelve una Specification combinable con .and() / .or(),
 * evitando condicionales gigantes en el Service.
 */
public class EntidadSpecifications {

    private EntidadSpecifications() {}

    public static Specification<Entidad> nombreContiene(String texto) {
        return (root, query, cb) -> texto == null || texto.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("nombre")), "%" + texto.toLowerCase() + "%");
    }

    public static Specification<Entidad> categoriaId(Long categoriaId) {
        return (root, query, cb) -> categoriaId == null
                ? cb.conjunction()
                : cb.equal(root.get("categoria").get("id"), categoriaId);
    }

    public static Specification<Entidad> precioMinimo(BigDecimal min) {
        return (root, query, cb) -> min == null
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("precio"), min);
    }

    public static Specification<Entidad> precioMaximo(BigDecimal max) {
        return (root, query, cb) -> max == null
                ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("precio"), max);
    }

    public static Specification<Entidad> stockMinimo(Integer min) {
        return (root, query, cb) -> min == null
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("stock"), min);
    }
}
