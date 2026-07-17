package com.uteq.pfc.controller;

import com.uteq.pfc.dto.AuthDtos;
import com.uteq.pfc.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${app.jwt.cookie-name}")
    private String cookieName;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    @PostMapping("/login")
    public AuthDtos.LoginResponse login(@Valid @RequestBody AuthDtos.LoginRequest request,
                                         HttpServletResponse response) {
        AuthService.LoginResultado resultado = authService.login(request.email(), request.password());

        // Cookie HttpOnly: inaccesible desde JavaScript -> mitiga robo de
        // token via XSS (ver fundamento teorico 5.1). No se usa localStorage.
        Cookie cookie = new Cookie(cookieName, resultado.token());
        cookie.setHttpOnly(true);
        cookie.setSecure(false);          // solo HTTPS en produccion
        cookie.setPath("/");
        cookie.setMaxAge((int) (expirationMs / 1000));
        response.addCookie(cookie);

        return new AuthDtos.LoginResponse("Login exitoso", resultado.nombre(), resultado.rol());
    }

    @PostMapping("/logout")
    public AuthDtos.LogoutResponse logout(@CookieValue(name = "${app.jwt.cookie-name}", required = false) String token,
                                           HttpServletResponse response) {
        if (token != null) {
            authService.logout(token);
        }

        // Expira la cookie del lado del cliente tambien
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return new AuthDtos.LogoutResponse("Sesion cerrada correctamente.", null);
    }
}
