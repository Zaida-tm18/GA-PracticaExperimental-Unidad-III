# PFC Backend

**UTEQ — Facultad de Ciencias de la Computación — Aplicaciones Web [111] — 5to Nivel A**
Stack: Java 21 · Spring Boot 3.3 · PostgreSQL 16 · Redis 7 · Flyway · JUnit 5 + JaCoCo

## Estructura

```
pfc-backend/
├── pom.xml
├── docker-compose.yml          # PostgreSQL + Redis + Redis Commander
├── src/main/java/com/uteq/pfc/
│   ├── entity/                 # Usuario, Categoria, Entidad (JPA)
│   ├── repository/             # JpaRepository + Specifications (filtros)
│   ├── service/                # EntidadService (cache-aside), AuthService
│   ├── controller/              # EntidadController, AuthController
│   ├── security/                 # JwtProvider, JwtAuthFilter, TokenBlacklistService
│   ├── config/                  # SecurityConfig, RedisCacheConfig
│   ├── dto/                     # Requests/Responses, PageResponse
│   └── exception/                # Manejo global de errores
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/            # V1, V2, V3 (Flyway)
├── src/test/java/com/uteq/pfc/
│   ├── repository/               # >=70% cobertura exigida
│   └── service/
└── docs/
    ├── benchmark/                 # benchmark.sh, analizar_benchmark.py, resultados
    └── evidencias/                 # checklist de capturas pendientes
```

## Flujo rápido

```bash
docker compose up -d
mvn spring-boot:run
# en otra terminal:
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"ana.torres@uteq.edu.ec","password":"..."}' -c cookies.txt

curl http://localhost:8080/api/entidades?page=0&size=10 -b cookies.txt

curl -X POST http://localhost:8080/api/auth/logout -b cookies.txt

# confirmar que el token ya no sirve:
curl http://localhost:8080/api/entidades -b cookies.txt   # debe dar 401/403
```

