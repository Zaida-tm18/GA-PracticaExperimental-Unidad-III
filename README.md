# PFC Backend — UTEQ

**Facultad de Ciencias de la Computación — Carrera: Software (Rediseño)**
**Asignatura:** Aplicaciones Web [111] — 5to Nivel A
**Práctica:** Acceso a Datos y Patrones de Arquitectura: Integración ORM, Caché con Redis y Diseño Arquitectónico C4 (Unidad III)
**Docente responsable:** Guerrero Ulloa Gleiston Cicerón

Implementación de la capa de integración del Proyecto Fin de Curso (PFC): arquitectura documentada con Modelo C4, acceso a datos con ORM y patrón Repository, autenticación JWT, caché con Redis (patrón cache-aside) y pruebas unitarias de la capa Repository con cobertura ≥ 70%.

## Stack tecnológico

| Componente | Tecnología |
|---|---|
| Lenguaje / runtime | Java 21 |
| Framework backend | Spring Boot 3.3.2 |
| ORM | Spring Data JPA / Hibernate |
| Base de datos relacional | PostgreSQL 16 |
| Migraciones | Flyway |
| Caché | Redis 7 (cache-aside) |
| Autenticación | JWT (cookie HttpOnly) + blacklist en Redis |
| Pruebas | JUnit 5 + JaCoCo (cobertura del Repository) |
| Orquestación local | Docker Compose (PostgreSQL + Redis + Redis Commander) |
| Frontend de pruebas | HTML/JS estático (`panel/`) servido en `http://localhost:5500` |

## Estructura del repositorio

```
GA-PracticaExperimental-Unidad-III/
├── pom.xml
├── docker-compose.yml            # PostgreSQL + Redis + Redis Commander
├── panel/                        # Frontend de pruebas (PE-U1)
│   ├── index.html
│   └── servidor.ps1
├── src/main/java/com/uteq/pfc/
│   ├── entity/                   # Usuario, Categoria, Entidad (JPA)
│   ├── repository/                # JpaRepository + Specifications (filtros dinámicos)
│   ├── service/                   # EntidadService (cache-aside), AuthService
│   ├── controller/                # EntidadController, AuthController
│   ├── security/                  # JwtProvider, JwtAuthFilter, TokenBlacklistService
│   ├── config/                    # SecurityConfig, RedisCacheConfig
│   ├── dto/                       # Requests/Responses, PageResponse
│   └── exception/                 # GlobalExceptionHandler
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/              # V1 (esquema), V2 (auditoría/tokens), V3 (seeders, 55 registros)
├── src/test/java/com/uteq/pfc/
│   ├── repository/                # EntidadRepositoryTest, CategoriaRepositoryTest (≥70% cobertura)
│   └── service/                   # EntidadServiceTest
└── docs/
    ├── adr/                        # ADR-001, ADR-002, ADR-003
    ├── arquitectura/                # Diagramas C4 (Niveles 1-3), ER y UML de clases
    ├── benchmark/                   # Scripts y resultados del benchmark de caché
    ├── evidencias/                  # Capturas de pantalla exigidas por la guía
    ├── referencias/                 # Papers usados como referencia IEEE
    └── Anexo-A-estructura-repositorio.md
```

## Arquitectura

La arquitectura se documenta con el **Modelo C4** de Simon Brown en `docs/arquitectura/`:

- **C4 Nivel 1 — Contexto**: el PFC como caja negra, actores (usuario final, administrador) y sistemas externos.
- **C4 Nivel 2 — Contenedores**: frontend, backend Spring Boot, PostgreSQL y Redis.
- **C4 Nivel 3 — Componentes**: descomposición del backend en Controladores, Servicios, Repositorios, Middleware de autenticación y capa ORM.

Las decisiones arquitectónicas relevantes están registradas como **ADR** (Architecture Decision Records, formato Nygard) en `docs/adr/`:

- **ADR-001** — Arquitectura monolítica por capas (presentación / negocio / datos), justificada para un equipo de 3 personas en contexto académico.
- **ADR-002** — Selección de Angular como framework de frontend.
- **ADR-003** — Uso de Redis como almacén centralizado para caché (cache-aside) y blacklist de tokens JWT.

## Requisitos previos

- Docker Desktop 4.x con Docker Compose 2.x
- JDK 21
- Maven 3.9+ (o el wrapper `mvnw` si está disponible)

## Puesta en marcha

**1. Levantar la infraestructura (PostgreSQL, Redis, Redis Commander):**

```bash
docker compose up -d
```

Esto expone:

- PostgreSQL en `localhost:5433` (`pfc_db` / `pfc_user` / `pfc_password`)
- Redis en `localhost:6379`
- Redis Commander (GUI) en `http://localhost:8082`

**2. Ejecutar el backend:**

```bash
mvn spring-boot:run
```

Al iniciar, Flyway aplica automáticamente las migraciones de `src/main/resources/db/migration/` (esquema inicial, auditoría/tokens y seeders con 55 registros).

El backend queda disponible en `http://localhost:8080`.

**3. Servir el panel de pruebas (frontend, PE-U1):**

Servir el contenido de `panel/` en `http://localhost:5500` (origen permitido por CORS en `application.yml`). Puede usarse `panel/servidor.ps1` en Windows o cualquier servidor estático equivalente.

## Endpoints principales

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/auth/login` | Autentica y entrega el JWT en cookie HttpOnly |
| `POST` | `/api/auth/logout` | Invalida el token (blacklist en Redis) |
| `GET` | `/api/entidades` | Listado paginado, con ordenamiento y filtros (cache-aside) |
| `GET` | `/api/entidades/{id}` | Detalle de una entidad |
| `POST` | `/api/entidades` | Crea una entidad (invalida caché) |
| `PUT` | `/api/entidades/{id}` | Actualiza una entidad (invalida caché) |
| `DELETE` | `/api/entidades/{id}` | Elimina una entidad (invalida caché) |

### Flujo de prueba manual

```bash
# 1. Login (guarda la cookie con el JWT)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"ana.torres@uteq.edu.ec","password":"..."}' -c cookies.txt

# 2. Listado paginado (usa la caché en el segundo llamado)
curl "http://localhost:8080/api/entidades?page=0&size=10" -b cookies.txt

# 3. Logout (invalida el token vía blacklist en Redis)
curl -X POST http://localhost:8080/api/auth/logout -b cookies.txt

# 4. Confirmar que el token ya no sirve (debe responder 401/403)
curl http://localhost:8080/api/entidades -b cookies.txt
```

## Caché: patrón cache-aside

El listado principal (`GET /api/entidades`) implementa **cache-aside** con Spring Cache + Redis (`RedisCacheConfig`, TTL de 300 s):

- La clave de caché combina página, tamaño, orden y filtros (`entidades::listado:...`).
- Los métodos de escritura (`crear`, `actualizar`, `eliminar`) usan `@CacheEvict(value = "entidades", allEntries = true)` para invalidar todas las entradas y evitar datos obsoletos.
- El diseño es *fail-open*: si Redis no responde, la petición cae directamente a PostgreSQL sin interrumpir el servicio.

### Resultados del benchmark (10 repeticiones)

Consulta medida: `GET /api/entidades?page=0&size=20&sort=precio,desc`

| Métrica | Sin caché (ms) | Con caché (ms) |
|---|---|---|
| Media | 45.49 | 14.36 |
| Desv. estándar | 10.76 | 2.14 |
| P95 | 63.33 | 17.76 |

**Speedup S = Tsin / Tcon = 3.17x** (supera el umbral S > 2 que justifica la complejidad operacional de Redis).

Detalle completo, scripts (`benchmark.sh`, `benchmark.ps1`, `analizar_benchmark.py`) y datos crudos en `docs/benchmark/`. El análisis de las preguntas de anexo (justificación del speedup, estrategia de invalidación y riesgo de cache stampede) está en `docs/benchmark/anexo8_respuestas.md`.

## Pruebas y cobertura

```bash
mvn test
```

El reporte de cobertura JaCoCo se genera en `target/site/jacoco/index.html`. La cobertura mínima exigida para la capa Repository es **≥ 70%** (evidencia en `docs/evidencias/evidencia_cobertura_jacoco.png`).

Clases de prueba:

- `EntidadRepositoryTest`
- `CategoriaRepositoryTest`
- `EntidadServiceTest`

## Modelo de datos

Migraciones versionadas con Flyway en `src/main/resources/db/migration/`:

- **V1** — Esquema inicial (tablas `usuarios`, `categorias`, `entidades`).
- **V2** — Auditoría y soporte de tokens.
- **V3** — Datos semilla (55 registros).

`ddl-auto: validate` en `application.yml`: el esquema lo gestiona exclusivamente Flyway, no Hibernate (ver ADR-002).

## Escalabilidad horizontal

Ante un escenario de 10 000 usuarios concurrentes, el diseño contempla:

- Balanceador de carga (Nginx como reverse proxy).
- Múltiples réplicas del backend (`docker-compose` con `replicas: 3`).
- Sesiones/tokens centralizados en Redis, sin *sticky sessions*.
- Réplica de lectura para la base de datos relacional.

## Evidencias

Capturas de pantalla del flujo completo (migraciones, seeder, login con cookie HttpOnly, logout con 401, endpoint paginado, caché y blacklist en Redis Commander, cobertura JaCoCo, Network tab) disponibles en `docs/evidencias/`.

## Bibliografía

- L. Bass, P. Clements, and R. Kazman, *Software Architecture in Practice*, 4th ed. Boston, MA, USA: Addison-Wesley, 2021, ISBN: 978-0-13-688080-1.
- M. Fowler, *Patterns of Enterprise Application Architecture*. Boston, MA, USA: Addison-Wesley, 2002, ISBN: 978-0-321-12521-7.
- M. T. Nygard, *Release It!: Design and Deploy Production-Ready Software*, 2nd ed. Raleigh, NC, USA: Pragmatic Bookshelf, 2018, ISBN: 978-1-68050-239-8.
- R. Nixon, *Learning PHP, MySQL & JavaScript*, 6th ed. O'Reilly Media, 2021, ISBN: 978-1-492-06229-9.
- H. Washizaki, Ed., *Guide to the Software Engineering Body of Knowledge (SWEBOK)* Version 4.0. Piscataway, NJ, USA: IEEE Computer Society, 2024.
- Laravel, "Laravel 11.x Documentation," 2024. [Online]. Available: https://laravel.com/docs/11.x
- PHPUnit, "PHPUnit 11 Manual," 2024. [Online]. Available: https://phpunit.de/documentation.html

Referencias adicionales usadas para los ADR y el fundamento teórico en `docs/referencias/`.

## Responsables

| Rol | Nombre | CI |
|---|---|---|
| Docente responsable | Guerrero Ulloa Gleiston Cicerón | 0913531752 |
| Coordinador de carrera | Ponce Ordóñez Jessica Alexandra | 1205316456 |
