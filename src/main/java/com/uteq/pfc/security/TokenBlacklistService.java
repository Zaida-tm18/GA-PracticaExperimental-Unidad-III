package com.uteq.pfc.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * GA punto 2a: gestion de estado stateless con JWT + blacklist de JTI en Redis.
 *
 * El JWT en si es stateless (no se guarda sesion en el servidor), pero el
 * "logout" en un esquema puramente stateless no puede invalidar un token ya
 * emitido -> se resuelve con una blacklist de corta duracion en Redis:
 * al hacer logout, se guarda el JTI (JWT ID) del token con un TTL igual al
 * tiempo de vida restante del token. En cada peticion protegida se verifica
 * que el JTI NO este en la blacklist.
 *
 * Ver ADR-003 (Panama) para la justificacion de usar Redis en este rol.
 */
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String PREFIJO = "jwt:blacklist:";

    private final StringRedisTemplate redisTemplate;

    /**
     * Marca un JTI como invalidado. El TTL debe ser el tiempo restante de
     * vida del token para no ocupar Redis indefinidamente: pasado ese TTL
     * el propio JWT ya habria expirado por su campo "exp", asi que ya no
     * hace falta seguir bloqueandolo explicitamente.
     */
    public void invalidar(String jti, Duration tiempoRestante) {
        if (tiempoRestante.isNegative() || tiempoRestante.isZero()) {
            return; // el token ya expiro por si mismo, no hace falta blacklist
        }
        redisTemplate.opsForValue().set(PREFIJO + jti, "invalidado", tiempoRestante);
    }

    public boolean estaInvalidado(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIJO + jti));
    }
}
