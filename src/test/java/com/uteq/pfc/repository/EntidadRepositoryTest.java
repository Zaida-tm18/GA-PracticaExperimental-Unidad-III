package com.uteq.pfc.repository;

import com.uteq.pfc.entity.Categoria;
import com.uteq.pfc.entity.Entidad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Paso 4: Pruebas unitarias de la capa Repository con base de datos en
 * memoria (H2, equivalente al SQLite usado en el ejemplo PHP de la guia).
 *
 * Cobertura objetivo: >=70% de EntidadRepository / EntidadSpecifications.
 * Cada metodo publico relevante del Repository (CRUD heredado de
 * JpaRepository + filtros via Specification) tiene al menos un caso de
 * prueba explicito.
 */
@DataJpaTest
@ActiveProfiles("test")
class EntidadRepositoryTest {

    @Autowired
    private EntidadRepository entidadRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria electronica;
    private Categoria hogar;

    @BeforeEach
    void setUp() {
        electronica = categoriaRepository.save(new Categoria(null, "Electronica", "Dispositivos"));
        hogar = categoriaRepository.save(new Categoria(null, "Hogar", "Articulos de hogar"));

        entidadRepository.save(crearEntidad("Laptop", new BigDecimal("899.99"), 10, electronica));
        entidadRepository.save(crearEntidad("Mouse", new BigDecimal("15.50"), 100, electronica));
        entidadRepository.save(crearEntidad("Licuadora", new BigDecimal("45.00"), 30, hogar));
        entidadRepository.save(crearEntidad("Cafetera", new BigDecimal("60.00"), 0, hogar));
    }

    private Entidad crearEntidad(String nombre, BigDecimal precio, int stock, Categoria categoria) {
        Entidad e = new Entidad();
        e.setNombre(nombre);
        e.setDescripcion("Descripcion de prueba para " + nombre);
        e.setPrecio(precio);
        e.setStock(stock);
        e.setCategoria(categoria);
        return e;
    }

    @Test
    void guardarYRecuperarPorId_debeConservarLosDatos() {
        Entidad guardada = entidadRepository.save(crearEntidad("Teclado", new BigDecimal("25.00"), 50, electronica));

        var recuperada = entidadRepository.findById(guardada.getId());

        assertThat(recuperada).isPresent();
        assertThat(recuperada.get().getNombre()).isEqualTo("Teclado");
        assertThat(recuperada.get().getCategoria().getId()).isEqualTo(electronica.getId());
    }

    @Test
    void findAll_sinFiltros_debeRetornarTodosLosRegistrosPaginados() {
        Pageable pageable = PageRequest.of(0, 10);

        var page = entidadRepository.findAll(Specification.<Entidad>where(null), pageable);

        assertThat(page.getTotalElements()).isEqualTo(4);
        assertThat(page.getContent()).hasSize(4);
    }

    @Test
    void findAll_conPaginacion_debeRespetarElTamanoDePagina() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("nombre").ascending());

        var page = entidadRepository.findAll(Specification.<Entidad>where(null), pageable);

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getTotalElements()).isEqualTo(4);
    }

    @Test
    void findAll_ordenamientoDinamico_debeOrdenarPorPrecioDescendente() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("precio").descending());

        var page = entidadRepository.findAll(Specification.<Entidad>where(null), pageable);
        List<Entidad> contenido = page.getContent();

        assertThat(contenido.get(0).getNombre()).isEqualTo("Laptop");
        assertThat(contenido.get(contenido.size() - 1).getNombre()).isEqualTo("Mouse");
    }

    @Test
    void findAll_filtroPorNombre_debeRetornarSoloCoincidencias() {
        Specification<Entidad> spec = EntidadSpecifications.nombreContiene("lap");

        var page = entidadRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getNombre()).isEqualTo("Laptop");
    }

    @Test
    void findAll_filtroPorCategoria_debeRetornarSoloEsaCategoria() {
        Specification<Entidad> spec = EntidadSpecifications.categoriaId(hogar.getId());

        var page = entidadRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getContent())
                .allMatch(e -> e.getCategoria().getId().equals(hogar.getId()));
    }

    @Test
    void findAll_filtroPorRangoDePrecio_debeAplicarMinimoYMaximo() {
        Specification<Entidad> spec = Specification
                .where(EntidadSpecifications.precioMinimo(new BigDecimal("20.00")))
                .and(EntidadSpecifications.precioMaximo(new BigDecimal("100.00")));

        var page = entidadRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getContent())
                .extracting(Entidad::getNombre)
                .containsExactlyInAnyOrder("Licuadora", "Cafetera");
    }

    @Test
    void findAll_filtroPorStockMinimo_debeExcluirSinStock() {
        Specification<Entidad> spec = EntidadSpecifications.stockMinimo(1);

        var page = entidadRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getContent())
                .extracting(Entidad::getNombre)
                .doesNotContain("Cafetera"); // stock = 0
    }

    @Test
    void findAll_filtrosCombinados_debeAplicarTodasLasCondiciones() {
        Specification<Entidad> spec = Specification
                .where(EntidadSpecifications.categoriaId(electronica.getId()))
                .and(EntidadSpecifications.precioMinimo(new BigDecimal("50.00")));

        var page = entidadRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getNombre()).isEqualTo("Laptop");
    }

    @Test
    void deleteById_debeEliminarElRegistro() {
        Entidad e = entidadRepository.save(crearEntidad("Temporal", BigDecimal.TEN, 5, hogar));
        Long id = e.getId();

        entidadRepository.deleteById(id);

        assertThat(entidadRepository.existsById(id)).isFalse();
    }

    @Test
    void existsById_debeRetornarFalseParaIdInexistente() {
        assertThat(entidadRepository.existsById(999_999L)).isFalse();
    }

    @Test
    void save_actualizarEntidadExistente_debePersistirCambios() {
        Entidad e = entidadRepository.save(crearEntidad("Original", BigDecimal.ONE, 1, hogar));

        e.setNombre("Modificado");
        e.setStock(99);
        entidadRepository.save(e);

        var recuperada = entidadRepository.findById(e.getId()).orElseThrow();
        assertThat(recuperada.getNombre()).isEqualTo("Modificado");
        assertThat(recuperada.getStock()).isEqualTo(99);
    }

    // ---------------------------------------------------------------
    // Ramas "sin filtro" (valor null) de EntidadSpecifications.
    // Cada Specification devuelve cb.conjunction() cuando el
    // parametro es null/blank; estos casos no estaban cubiertos y
    // dejaban la cobertura de LINE del paquete repository por debajo
    // del 70% exigido, aunque la de INSTRUCTION ya lo superaba.
    // ---------------------------------------------------------------

    @Test
    void nombreContiene_conNullOBlank_noDebeFiltrar() {
        Specification<Entidad> specNull = EntidadSpecifications.nombreContiene(null);
        Specification<Entidad> specBlank = EntidadSpecifications.nombreContiene("   ");

        var pageNull = entidadRepository.findAll(specNull, PageRequest.of(0, 10));
        var pageBlank = entidadRepository.findAll(specBlank, PageRequest.of(0, 10));

        assertThat(pageNull.getTotalElements()).isEqualTo(4);
        assertThat(pageBlank.getTotalElements()).isEqualTo(4);
    }

    @Test
    void categoriaId_conNull_noDebeFiltrar() {
        Specification<Entidad> spec = EntidadSpecifications.categoriaId(null);

        var page = entidadRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(4);
    }

    @Test
    void precioMinimo_conNull_noDebeFiltrar() {
        Specification<Entidad> spec = EntidadSpecifications.precioMinimo(null);

        var page = entidadRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(4);
    }

    @Test
    void precioMaximo_conNull_noDebeFiltrar() {
        Specification<Entidad> spec = EntidadSpecifications.precioMaximo(null);

        var page = entidadRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(4);
    }

    @Test
    void stockMinimo_conNull_noDebeFiltrar() {
        Specification<Entidad> spec = EntidadSpecifications.stockMinimo(null);

        var page = entidadRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(4);
    }
}