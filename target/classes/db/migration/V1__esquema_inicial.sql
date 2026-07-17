-- =========================================================
-- V1__esquema_inicial.sql
-- PFC - UTEQ - Aplicaciones Web [111]
-- Paso 2.2 Migraciones con el ORM elegido (Flyway)
-- =========================================================

CREATE TABLE usuarios (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(120)  NOT NULL,
    email           VARCHAR(160)  NOT NULL UNIQUE,
    password_hash   VARCHAR(255)  NOT NULL,
    rol             VARCHAR(30)   NOT NULL DEFAULT 'USUARIO',
    activo          BOOLEAN       NOT NULL DEFAULT TRUE,
    creado_en       TIMESTAMP     NOT NULL DEFAULT NOW(),
    actualizado_en  TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE categorias (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(100)  NOT NULL UNIQUE,
    descripcion     VARCHAR(255)
);

-- Entidad principal del PFC (ejemplo: "productos"; ajustar nombre segun dominio real del proyecto)
CREATE TABLE entidades (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(150)  NOT NULL,
    descripcion     TEXT,
    precio          NUMERIC(10,2) NOT NULL DEFAULT 0,
    stock           INTEGER       NOT NULL DEFAULT 0,
    categoria_id    BIGINT        NOT NULL REFERENCES categorias(id),
    usuario_id      BIGINT        REFERENCES usuarios(id),
    creado_en       TIMESTAMP     NOT NULL DEFAULT NOW(),
    actualizado_en  TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_entidades_categoria ON entidades(categoria_id);
CREATE INDEX idx_entidades_nombre    ON entidades(nombre);
CREATE INDEX idx_usuarios_email      ON usuarios(email);
