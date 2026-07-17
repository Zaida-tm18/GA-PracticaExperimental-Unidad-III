-- =========================================================
-- V2__auditoria_tokens.sql
-- PFC - UTEQ - Aplicaciones Web [111]
-- Tabla de auditoria de sesiones (la blacklist activa de JTI vive en Redis,
-- ver RedisTokenBlacklistService; esta tabla es solo trazabilidad historica)
-- =========================================================

CREATE TABLE auditoria_tokens (
    id              BIGSERIAL PRIMARY KEY,
    jti             VARCHAR(64)   NOT NULL,
    usuario_id      BIGINT        NOT NULL REFERENCES usuarios(id),
    emitido_en      TIMESTAMP     NOT NULL DEFAULT NOW(),
    invalidado_en   TIMESTAMP,
    motivo          VARCHAR(50)   -- 'LOGOUT', 'EXPIRACION', 'REVOCACION_ADMIN'
);

CREATE INDEX idx_auditoria_jti ON auditoria_tokens(jti);
CREATE INDEX idx_auditoria_usuario ON auditoria_tokens(usuario_id);
