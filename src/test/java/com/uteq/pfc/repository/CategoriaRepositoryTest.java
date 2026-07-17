package com.uteq.pfc.repository;

import com.uteq.pfc.entity.Categoria;
import com.uteq.pfc.entity.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void guardarCategoria_debeAsignarIdAutogenerado() {
        Categoria categoria = categoriaRepository.save(new Categoria(null, "Deportes", "Equipamiento"));

        assertThat(categoria.getId()).isNotNull();
        assertThat(categoria.getNombre()).isEqualTo("Deportes");
    }

    @Test
    void nombreDeCategoria_debeSerUnico() {
        categoriaRepository.save(new Categoria(null, "Libros", "Material educativo"));

        assertThat(categoriaRepository.findAll()).hasSize(1);
    }

    @Test
    void usuarioRepository_findByEmail_debeEncontrarUsuarioExistente() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Ana Torres");
        usuario.setEmail("ana.torres@uteq.edu.ec");
        usuario.setPasswordHash("hash_bcrypt_simulado");
        usuario.setRol("ADMIN");
        usuarioRepository.save(usuario);

        var encontrado = usuarioRepository.findByEmail("ana.torres@uteq.edu.ec");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getRol()).isEqualTo("ADMIN");
    }

    @Test
    void usuarioRepository_findByEmail_debeRetornarVacioSiNoExiste() {
        var encontrado = usuarioRepository.findByEmail("no-existe@uteq.edu.ec");

        assertThat(encontrado).isEmpty();
    }
}
