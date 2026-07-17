package com.uteq.pfc.dto;

import com.uteq.pfc.entity.Entidad;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de salida. Se cachea en Redis (String JSON) dentro del patron
 * cache-aside, por eso implementa Serializable.
 */
public record EntidadResponse(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Integer stock,
        Long categoriaId,
        String categoriaNombre,
        LocalDateTime creadoEn,
        LocalDateTime actualizadoEn
) implements Serializable {

    public static EntidadResponse from(Entidad e) {
        return new EntidadResponse(
                e.getId(),
                e.getNombre(),
                e.getDescripcion(),
                e.getPrecio(),
                e.getStock(),
                e.getCategoria() != null ? e.getCategoria().getId() : null,
                e.getCategoria() != null ? e.getCategoria().getNombre() : null,
                e.getCreadoEn(),
                e.getActualizadoEn()
        );
    }
}
