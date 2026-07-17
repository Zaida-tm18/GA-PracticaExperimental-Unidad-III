package com.uteq.pfc.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Lee el JWT desde la cookie HttpOnly (nunca desde localStorage/header,
 * ver justificacion en fundamento teorico 5.1 de Cajas sobre riesgos XSS),
 * valida la firma y verifica que el JTI no este en la blacklist de Redis
 * antes de autenticar la peticion.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final TokenBlacklistService blacklistService;

    @Value("${app.jwt.cookie-name}")
    private String cookieName;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        String token = extraerTokenDeCookie(request);

        if (token != null && jwtProvider.esValido(token)) {
            Claims claims = jwtProvider.parsearClaims(token);
            String jti = claims.getId();

            if (!blacklistService.estaInvalidado(jti)) {
                String email = claims.getSubject();
                String rol = claims.get("rol", String.class);

                var authToken = new UsernamePasswordAuthenticationToken(
                        email, null, List.of(new SimpleGrantedAuthority("ROLE_" + rol)));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            // si esta en blacklist, simplemente no se autentica -> 401 en endpoints protegidos
        }

        filterChain.doFilter(request, response);
    }

    private String extraerTokenDeCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
