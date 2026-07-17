package com.uteq.pfc.service;

import com.uteq.pfc.dto.EntidadRequest;
import com.uteq.pfc.entity.Categoria;
import com.uteq.pfc.entity.Entidad;
import com.uteq.pfc.exception.RecursoNoEncontradoException;
import com.uteq.pfc.repository.CategoriaRepository;
import com.uteq.pfc.repository.EntidadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas de la capa Service (complementarias; el criterio de verificacion
 * del Paso 4 exige cobertura >=70% en Repository, cubierto en
 * EntidadRepositoryTest / CategoriaRepositoryTest).
 */
@ExtendWith(MockitoExtension.class)
class EntidadServiceTest {

    @Mock
    private EntidadRepository entidadRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private EntidadService entidadService;

    @Test
    void obtenerPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(entidadRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> entidadService.obtenerPorId(1L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("1");
    }

    @Test
    void crear_conCategoriaInexistente_debeLanzarExcepcion() {
        EntidadRequest request = new EntidadRequest("Test", "desc", BigDecimal.TEN, 5, 99L, null);
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> entidadService.crear(request))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(entidadRepository, never()).save(any());
    }

    @Test
    void crear_conDatosValidos_debeGuardarYRetornarResponse() {
        Categoria categoria = new Categoria(1L, "Electronica", "desc");
        EntidadRequest request = new EntidadRequest("Laptop", "desc", new BigDecimal("899.99"), 10, 1L, null);

        Entidad guardada = new Entidad();
        guardada.setId(1L);
        guardada.setNombre("Laptop");
        guardada.setPrecio(new BigDecimal("899.99"));
        guardada.setStock(10);
        guardada.setCategoria(categoria);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(entidadRepository.save(any(Entidad.class))).thenReturn(guardada);

        var resultado = entidadService.crear(request);

        assertThat(resultado.nombre()).isEqualTo("Laptop");
        assertThat(resultado.categoriaNombre()).isEqualTo("Electronica");
    }

    @Test
    void eliminar_cuandoNoExiste_debeLanzarExcepcion() {
        when(entidadRepository.existsById(5L)).thenReturn(false);

        assertThatThrownBy(() -> entidadService.eliminar(5L))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(entidadRepository, never()).deleteById(any());
    }

    @Test
    void eliminar_cuandoExiste_debeInvocarDeleteById() {
        when(entidadRepository.existsById(5L)).thenReturn(true);

        entidadService.eliminar(5L);

        verify(entidadRepository).deleteById(5L);
    }
}
