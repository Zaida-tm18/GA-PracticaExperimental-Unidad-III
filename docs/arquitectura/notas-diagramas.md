# Notas de diagramas de arquitectura — PFC

Este documento resume el propósito y contenido de cada diagrama entregado en `docs/arquitectura/`, como referencia rápida antes de leer las imágenes.

## C4 Nivel 1 — Contexto
**Archivo:** `C4-Nivel1-Contexto.png`

Muestra el sistema del PFC como una caja negra y sus interacciones con actores externos.

- **Actores:** Usuario Final, Administrador.
- **Sistemas externos:** Base de Datos PostgreSQL.
- **Relaciones:** Usuario y Administrador acceden al sistema vía HTTPS/REST; el sistema lee/escribe en PostgreSQL vía JDBC.

## C4 Nivel 2 — Contenedores
**Archivo:** `C4-Nivel2-Contenedores.png`

Muestra los contenedores de software que componen el sistema, dentro del límite (system boundary) del PFC.

- **Contenedores:** Web Application (Angular 17+), API Application (Spring Boot 3.x / Java 21), Database (PostgreSQL 16), Cache (Redis 7.x).
- **Relaciones:** el frontend consume la API vía HTTPS/JSON; la API lee/escribe en PostgreSQL vía JDBC y en Redis vía protocolo Redis (cache-aside + blacklist de JTI).

## C4 Nivel 3 — Componentes
**Archivo:** `C4-Nivel3-Componentes.png`

Descompone el contenedor de mayor complejidad (API Application) en sus componentes internos.

- **Componentes:** AuthController, ResourceController, JwtAuthenticationFilter, AuthService, ResourceService, ResourceRepository.
- **Relaciones clave:** el filtro JWT verifica la blacklist de JTI en Redis antes de permitir el paso a los controllers; ResourceService aplica `@Cacheable`/`@CacheEvict` sobre Redis antes de llegar al Repository/PostgreSQL.

## Diagrama de clases UML
**Archivo:** `Diagrama-UML-Clases.png`

Refleja el paquete `entity` implementado hasta esta entrega (`Usuario`, `Categoria`, `Entidad`), con atributos, tipos y asociaciones según las anotaciones JPA reales del código.

- **Asociaciones:** `Entidad(0..*) → Categoria(1)` (FK `categoria_id` obligatoria); `Entidad(0..*) → Usuario(0..1)` (FK `usuario_id` opcional).
- **Nota:** la tabla `auditoria_tokens` existe en la base de datos pero aún no tiene clase JPA correspondiente; se actualizará este diagrama si se agrega.


## Diagrama Entidad-Relación
**Archivo:** `Diagrama-ER.png`

Generado desde pgAdmin 4 ERD Tool, muestra las 4 tablas de la base de datos PostgreSQL y sus relaciones.

- **Tablas:** `usuarios`, `categorias`, `entidades`, `auditoria_tokens`.
- **Relaciones:** `entidades.categoria_id` → `categorias.id` (obligatoria); `entidades.usuario_id` → `usuarios.id` (opcional); `auditoria_tokens.usuario_id` → `usuarios.id` (obligatoria).


---
*Última actualización: 13 de julio de 2026 — C4 Nivel 1, 2 y 3 completados.*