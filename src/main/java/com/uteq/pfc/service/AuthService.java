package com.uteq.pfc.service;

import com.uteq.pfc.dto.AuthDtos;
import com.uteq.pfc.entity.Usuario;
import com.uteq.pfc.exception.RecursoNoEncontradoException;
import com.uteq.pfc.repository.UsuarioRepository;
import com.uteq.pfc.security.JwtProvider;
import com.uteq.pfc.security.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final TokenBlacklistService blacklistService;

    public record LoginResultado(String token, String nombre, String rol) {}

    public LoginResultado login(String email, String passwordPlano) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Credenciales invalidas"));

        if (!passwordEncoder.matches(passwordPlano, usuario.getPasswordHash())) {
            throw new RecursoNoEncontradoException("Credenciales invalidas");
        }

        String token = jwtProvider.generarToken(usuario.getEmail(), usuario.getRol());
        return new LoginResultado(token, usuario.getNombre(), usuario.getRol());
    }

    /**
     * Logout real: extrae el JTI del token vigente y lo agrega a la
     * blacklist de Redis con TTL = tiempo restante del token. Desde ese
     * momento, JwtAuthFilter rechazara ese JTI aunque la firma siga siendo
     * valida -> esto es lo que se debe demostrar en el Paso 3 (evidencia:
     * llamar a un endpoint protegido con el mismo token despues del logout
     * y confirmar que responde 401/403).
     */
    public AuthDtos.LogoutResponse logout(String token) {
        String jti = jwtProvider.extraerJti(token);
        Duration restante = jwtProvider.tiempoRestante(token);
        blacklistService.invalidar(jti, restante);
        return new AuthDtos.LogoutResponse("Sesion cerrada correctamente. Token invalidado.", jti);
    }
}
