# Anexo A — Estructura del repositorio

## Propósito
Este anexo documenta la organización de carpetas y archivos del repositorio del PFC, así como la convención de ramas y commits utilizada por el equipo.

## Estructura de carpetas (estado actual)

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