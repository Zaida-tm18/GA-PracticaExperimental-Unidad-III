# Anexo A — Estructura del repositorio

## Propósito
Este anexo documenta la organización de carpetas y archivos del repositorio del PFC, así como la convención de ramas y commits utilizada por el equipo.

## Estructura de carpetas (estado actual)

```
GA-PracticaExperimental-Unidad-III/
├── docs/
│   ├── adr/
│   │   ├── ADR-001-arquitectura-capas.md
│   │   ├── ADR-002-angular-vs-react.md
│   │   └── ADR-003-redis-cache-jwt.md
│   ├── arquitectura/
│   │   ├── notas-diagramas.md
│   │   ├── C4-Nivel1-Contexto.png
│   │   ├── C4-Nivel2-Contenedores.png
│   │   └── C4-Nivel3-Componentes.png
│   └── Anexo-A-estructura-repositorio.md
└── pfc-backend/
    ├── pom.xml
    ├── docker-compose.yml              # PostgreSQL + Redis + Redis Commander
    ├── src/main/java/com/uteq/pfc/
    │   ├── entity/                     # Usuario, Categoria, Entidad (JPA)
    │   ├── repository/                 # JpaRepository + Specifications (filtros)
    │   ├── service/                    # EntidadService (cache-aside), AuthService
    │   ├── controller/                 # EntidadController, AuthController
    │   ├── security/                   # JwtProvider, JwtAuthFilter, TokenBlacklistService
    │   ├── config/                     # SecurityConfig, RedisCacheConfig
    │   ├── dto/                        # Requests/Responses, PageResponse
    │   └── exception/                  # Manejo global de errores
    ├── src/main/resources/
    │   ├── application.yml
    │   └── db/migration/               # V1, V2, V3 (Flyway)
    ├── src/test/java/com/uteq/pfc/
    │   ├── repository/                 # ≥70% cobertura exigida
    │   └── service/
    └── docs/
        ├── benchmark/                  # benchmark.sh, analizar_benchmark.py, resultados
        └── evidencias/                 # checklist de capturas pendientes
```

*(Estructura de `pfc-backend/` según el README del equipo; corresponde a los Pasos 2, 3 y 4, a cargo de otro integrante. La carpeta `docs/` en la raíz corresponde al Paso 1, diseño arquitectónico.)*

## Convención de ramas
El equipo trabaja con una rama por integrante/responsabilidad, sin hacer commits directos a `main`:

- `main`: rama estable, se integra mediante Pull Request cuando cada parte está validada.
- `feature/arquitectura-c4`: diseño arquitectónico — C4, ADR, UML, E-R (Paso 1).
- *(ramas de los demás integrantes, según lo que definan para los Pasos 2 al 5)*

## Convención de commits
Se sigue el estándar de **Conventional Commits**:

| Prefijo | Uso |
|---|---|
| `feat:` | funcionalidad nueva |
| `fix:` | corrección de errores |
| `docs:` | documentación, diagramas, ADR, notas |
| `test:` | pruebas unitarias |
| `refactor:` | reestructuración de código sin cambiar comportamiento |
| `chore:` | tareas de mantenimiento, configuración |

## Responsables por carpeta
| Carpeta/Archivo | Responsable |
|---|---|
| `docs/adr/` | Diseño arquitectónico |
| `docs/arquitectura/` | Diseño arquitectónico |
| `docs/Anexo-A-*.md` | Diseño arquitectónico |
| `pfc-backend/` | Implementación (Pasos 2-4) |

---
*Última actualización: 13 de julio de 2026 — estructura actualizada con el backend real (pfc-backend/) documentado por el equipo.*