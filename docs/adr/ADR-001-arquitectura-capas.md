# ADR-001: Selección de arquitectura en capas (monolítica) para el PFC

## Estado
Aceptado

## Contexto
El Proyecto Fin de Curso (PFC) se desarrolla con un equipo de 3 integrantes, bajo un plazo académico limitado, usando el stack Java 21 / Spring Boot 3.x en el backend, Angular 17+ en el frontend y PostgreSQL 16 como base de datos relacional. Es necesario decidir el estilo arquitectónico general del sistema antes de avanzar con la implementación del modelo de datos, la capa de caché y la integración frontend-backend.

Las opciones evaluadas fueron:
- Arquitectura monolítica por capas (presentación, negocio, datos).
- Arquitectura de microservicios.
- Arquitectura orientada a eventos (EDA) como estilo principal.

El equipo debe considerar la complejidad operacional que puede manejar razonablemente un grupo pequeño en un contexto universitario, así como las llamadas "falacias de los sistemas distribuidos", que introducen riesgos innecesarios si se adopta una arquitectura distribuida sin la necesidad real de escalar de esa forma.

Estudios recientes que aplican metodologías de decisión multicriterio para comparar estilos arquitectónicos (monolítico, microservicios, en capas, serverless, orientado a eventos y SOA) encuentran que la arquitectura monolítica obtiene el mayor puntaje de utilidad cuando se prioriza el rendimiento y la simplicidad operacional sobre la escalabilidad extrema (Akinsola et al., 2026).

## Decisión
Se adopta una **arquitectura monolítica por capas**, estructurada como:

`Controller → Service → Repository → Entity`

- **Controller**: expone los endpoints REST y gestiona las peticiones/respuestas HTTP.
- **Service**: contiene la lógica de negocio y coordina las reglas de la aplicación.
- **Repository**: encapsula el acceso a datos mediante Spring Data JPA.
- **Entity**: representa el modelo de datos mapeado a PostgreSQL.

Esta estructura se despliega como una única aplicación backend (con posibilidad de réplicas idénticas mediante Docker Compose para escalado horizontal simple), acompañada de una capa de caché con Redis (patrón cache-aside) y el frontend Angular como aplicación independiente que consume la API REST.

## Consecuencias

**Positivas:**
- Menor complejidad operacional: un solo despliegue, un solo repositorio de código backend, sin necesidad de orquestar comunicación entre servicios.
- Menor carga cognitiva para un equipo de 3 personas (team cognitive load), permitiendo enfocarse en la calidad de cada capa en vez de en la infraestructura distribuida.
- Facilita el debugging y las pruebas unitarias, al no existir llamadas de red entre componentes internos.
- Escalado horizontal simple mediante múltiples instancias idénticas del monolito detrás de un balanceador (Nginx), sin la complejidad de coordinar microservicios independientes.

**Negativas / trade-offs:**
- Todos los módulos comparten el mismo ciclo de despliegue: un cambio en un módulo requiere redesplegar toda la aplicación.
- Menor aislamiento de fallos: un error en un componente puede afectar a toda la aplicación.
- Si el sistema creciera significativamente en el futuro, migrar hacia microservicios implicaría un esfuerzo de refactorización considerable.

## Alternativas consideradas

1. **Microservicios**: descartada por la sobrecarga operacional (múltiples despliegues, comunicación entre servicios, gestión de fallos distribuidos) que no se justifica para el alcance y el equipo del PFC. Esto es consistente con hallazgos donde la arquitectura de microservicios obtiene menor puntaje de utilidad que la monolítica bajo criterios típicos de rendimiento, seguridad y mantenibilidad, aunque puede superarla cuando la seguridad se pondera de forma mucho más agresiva (Akinsola et al., 2026).
2. **Arquitectura orientada a eventos (EDA) como estilo principal**: descartada como arquitectura base; se reserva para casos puntuales (p. ej., notificaciones en tiempo real vía WebSockets) en lugar de ser el patrón estructural del sistema completo.

## Referencias
Akinsola, J. E. T., Akinkunmi, A. O., Olaniyi, I. M., Efiong, J. E., & Olajubu, E. A. (2026). Multi-criteria decision-making (MCDM) approach for software architecture selection in cloud computing using evidential reasoning and Bayesian inference techniques. *International Journal of Information Engineering and Electronic Business, 18*(1), 1–12. https://doi.org/10.5815/ijieeb.2026.01.01

### Nota sobre el motor de base de datos: PostgreSQL en lugar de MySQL

Aunque la guía sugiere MySQL 8.0, el equipo optó por **PostgreSQL 16** como motor relacional del PFC. Ambos son compatibles con Hibernate/JPA mediante el dialecto correspondiente, por lo que el cambio no afecta los objetivos de la práctica (ORM, migraciones, caché, pruebas unitarias). La decisión se basó en el sistema de tipos más estricto de PostgreSQL (`NUMERIC` con
precisión garantizada, `JSONB` indexable), su mayor adherencia al estándar SQL, y la experiencia previa del equipo con este motor, lo que redujo el riesgo de errores de configuración dado el plazo académico limitado. El cambio solo implicó ajustar el dialecto de Hibernate (`PostgreSQLDialect`) y las imágenes en `docker-compose.yml`, sin afectar el modelo de datos ni la lógica de negocio.