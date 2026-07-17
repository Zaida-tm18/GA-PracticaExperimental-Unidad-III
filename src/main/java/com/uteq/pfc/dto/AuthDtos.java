package com.uteq.pfc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    public record LoginResponse(
            String mensaje,
            String nombre,
            String rol
    ) {}

    public record LogoutResponse(
            String mensaje,
            String jtiInvalidado
    ) {}
}
