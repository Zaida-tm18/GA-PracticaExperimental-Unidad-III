package com.uteq.pfc.controller;

import com.uteq.pfc.dto.EntidadRequest;
import com.uteq.pfc.dto.EntidadResponse;
import com.uteq.pfc.dto.PageResponse;
import com.uteq.pfc.service.EntidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Servicio veterinario del PFC "MarketPlace UTEQ" — plataforma de comercio electrónico 
 * multicategoría para la gestión de catálogo de productos, con backend Spring Boot.
 * Se mantiene el nombre técnico "Entidad"/"entidades" en clase, tabla y
 * endpoint por consistencia con la nomenclatura definida catálogo de servicios veterinarios;
 * el dominio real que representa es el catálogo de
 * servicios/productos de la veterinaria.
 */
@RestController
@RequestMapping("/api/entidades")
@RequiredArgsConstructor
public class EntidadController {

    private final EntidadService entidadService;

    /**
     * Paso 2.3: paginacion via Pageable (?page=0&size=20&sort=precio,desc)
     * y filtros multiples opcionales por query params.
     * Es tambien la "consulta principal" usada en el benchmark del Paso 3.2.
     */
    @GetMapping
    public PageResponse<EntidadResponse> listar(
            Pageable pageable,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) Integer stockMin) {
        return entidadService.listar(pageable, nombre, categoriaId, precioMin, precioMax, stockMin);
    }

    @GetMapping("/{id}")
    public EntidadResponse obtener(@PathVariable Long id) {
        return entidadService.obtenerPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntidadResponse crear(@Valid @RequestBody EntidadRequest request) {
        return entidadService.crear(request);
    }

    @PutMapping("/{id}")
    public EntidadResponse actualizar(@PathVariable Long id, @Valid @RequestBody EntidadRequest request) {
        return entidadService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        entidadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
