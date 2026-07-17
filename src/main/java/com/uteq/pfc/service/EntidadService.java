package com.uteq.pfc.service;

import com.uteq.pfc.dto.EntidadRequest;
import com.uteq.pfc.dto.EntidadResponse;
import com.uteq.pfc.dto.PageResponse;
import com.uteq.pfc.entity.Categoria;
import com.uteq.pfc.entity.Entidad;
import com.uteq.pfc.exception.RecursoNoEncontradoException;
import com.uteq.pfc.repository.CategoriaRepository;
import com.uteq.pfc.repository.EntidadRepository;
import com.uteq.pfc.repository.EntidadSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Capa Service (arquitectura en capas: Controller -> Service -> Repository -> Entity,
 * GA punto 2c).
 *
 * Implementa el patron CACHE-ASIDE (Paso 3.1):
 *   - Lectura (listar): @Cacheable -> primero busca en Redis; si hay miss,
 *     consulta la BD via Repository y guarda el resultado en cache.
 *   - Escritura (crear/actualizar/eliminar): @CacheEvict -> invalida las
 *     entradas de cache afectadas para evitar servir datos obsoletos.
 *
 * Estrategia de invalidacion: se usa allEntries=true sobre el cache "entidades"
 * en cada escritura. Es una estrategia simple y segura (evita servir datos
 * obsoletos) a costa de invalidar mas de lo estrictamente necesario; para un
 * PFC universitario el volumen de escrituras no justifica una invalidacion
 * selectiva por clave compuesta.
 */
@Service
@RequiredArgsConstructor
public class EntidadService {

    private final EntidadRepository entidadRepository;
    private final CategoriaRepository categoriaRepository;

    private static final String CACHE_NAME = "entidades";

    /**
     * Paso 2.3: CRUD con paginacion, ordenamiento dinamico (via Pageable.sort)
     * y busqueda con filtros multiples (via Specification).
     *
     * Esta es tambien LA CONSULTA PRINCIPAL usada en el benchmark del Paso 3.2
     * (con y sin cache) porque es el listado mas costoso del sistema.
     */
    @Cacheable(
            value = CACHE_NAME,
            key = "'listado:' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + " +
                  "#pageable.sort.toString() + ':' + #nombre + ':' + #categoriaId + ':' + " +
                  "#precioMin + ':' + #precioMax + ':' + #stockMin"
    )
    @Transactional(readOnly = true)
    public PageResponse<EntidadResponse> listar(Pageable pageable,
                                                 String nombre,
                                                 Long categoriaId,
                                                 BigDecimal precioMin,
                                                 BigDecimal precioMax,
                                                 Integer stockMin) {
        Specification<Entidad> spec = Specification
                .where(EntidadSpecifications.nombreContiene(nombre))
                .and(EntidadSpecifications.categoriaId(categoriaId))
                .and(EntidadSpecifications.precioMinimo(precioMin))
                .and(EntidadSpecifications.precioMaximo(precioMax))
                .and(EntidadSpecifications.stockMinimo(stockMin));

        Page<Entidad> page = entidadRepository.findAll(spec, pageable);
        Page<EntidadResponse> mapped = page.map(EntidadResponse::from);
        return PageResponse.from(mapped);
    }

    @Transactional(readOnly = true)
    public EntidadResponse obtenerPorId(Long id) {
        Entidad entidad = entidadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Entidad con id " + id + " no encontrada"));
        return EntidadResponse.from(entidad);
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    @Transactional
    public EntidadResponse crear(EntidadRequest request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Categoria con id " + request.categoriaId() + " no encontrada"));

        Entidad entidad = new Entidad();
        entidad.setNombre(request.nombre());
        entidad.setDescripcion(request.descripcion());
        entidad.setPrecio(request.precio());
        entidad.setStock(request.stock());
        entidad.setCategoria(categoria);

        Entidad guardada = entidadRepository.save(entidad);
        return EntidadResponse.from(guardada);
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    @Transactional
    public EntidadResponse actualizar(Long id, EntidadRequest request) {
        Entidad entidad = entidadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Entidad con id " + id + " no encontrada"));

        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Categoria con id " + request.categoriaId() + " no encontrada"));

        entidad.setNombre(request.nombre());
        entidad.setDescripcion(request.descripcion());
        entidad.setPrecio(request.precio());
        entidad.setStock(request.stock());
        entidad.setCategoria(categoria);

        Entidad actualizada = entidadRepository.save(entidad);
        return EntidadResponse.from(actualizada);
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    @Transactional
    public void eliminar(Long id) {
        if (!entidadRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Entidad con id " + id + " no encontrada");
        }
        entidadRepository.deleteById(id);
    }
}
