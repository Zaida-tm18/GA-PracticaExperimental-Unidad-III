# ADR-003: Estrategia de caché y blacklist de tokens con Redis

## Estado
Aceptado

## Contexto
El PFC implementa autenticación stateless mediante tokens JWT almacenados en cookies HttpOnly, y requiere además una capa de caché para optimizar el rendimiento de las consultas más frecuentes (patrón cache-aside sobre el listado principal del sistema). Ambas necesidades comparten un mismo requisito técnico: un almacén rápido, en memoria, accesible desde múltiples instancias del backend.

Dos problemas concretos motivan esta decisión:

1. **Invalidación de JWT en logout**: al ser stateless, un JWT válido no puede "eliminarse" del lado del servidor una vez emitido; sigue siendo válido hasta su expiración natural. Esto es un problema cuando el usuario cierra sesión (logout) o cuando se necesita revocar acceso de forma inmediata (por ejemplo, ante una sospecha de robo de sesión).
2. **Costo de las consultas repetidas**: la consulta principal de listado del PFC se ejecuta con alta frecuencia y su recomputación constante contra PostgreSQL introduce latencia innecesaria cuando los datos no cambian entre peticiones.

Las alternativas evaluadas para resolver ambos problemas fueron:
- Base de datos relacional (tabla de tokens revocados / tabla de caché manual).
- Almacenamiento en memoria local de cada instancia del backend (caché en proceso).
- Redis como almacén centralizado en memoria.

## Decisión
Se adopta **Redis** como almacén centralizado para dos responsabilidades:

**(a) Blacklist de JTI (JWT ID) para logout:**
Al hacer logout, el `jti` (identificador único) del token se almacena en Redis con una expiración (`EX`) igual al tiempo de vida restante del propio JWT. En cada petición autenticada, el backend verifica si el `jti` del token recibido está en la blacklist antes de aceptarlo como válido. Esto permite invalidación efectiva sin necesidad de mantener estado de sesión completo en el servidor.

**(b) Caché cache-aside para la consulta principal:**
La aplicación primero consulta Redis; si el dato no está (cache miss), consulta PostgreSQL vía el Repository y almacena el resultado en Redis con un TTL apropiado. Las escrituras (creación, actualización, eliminación de la entidad principal) invalidan explícitamente la clave correspondiente en Redis mediante `@CacheEvict`.

## Consecuencias

**Positivas:**
- Redis centraliza tanto la blacklist de JWT como la caché de datos, evitando mantener dos sistemas distintos para necesidades similares (almacén rápido en memoria, compartido entre instancias).
- Al ser un almacén compartido, el logout invalida el token de forma efectiva sin importar cuál instancia del backend atienda la siguiente petición, algo que un caché en memoria local de cada instancia no podría garantizar.
- El TTL nativo de Redis (`EX`) simplifica la expiración automática tanto de las entradas de blacklist como de las entradas de caché, sin necesidad de tareas de limpieza programadas.
- El patrón cache-aside reduce la carga sobre PostgreSQL en la consulta principal, mejorando el tiempo de respuesta medido (ver benchmark de speedup en el informe técnico).

**Negativas / trade-offs:**
- Introduce una dependencia de infraestructura adicional (Redis debe estar disponible; su caída afecta tanto la validación de logout como el rendimiento de las consultas cacheadas).
- Requiere gestionar la coherencia entre caché y base de datos: toda escritura debe recordar invalidar la clave correspondiente, o se sirven datos desactualizados (stale cache).
- Introduce complejidad operacional adicional al stack (un servicio más que desplegar, monitorear y mantener disponible), lo cual debe justificarse con una mejora de rendimiento medible.

## Alternativas consideradas

1. **Tabla de tokens revocados en PostgreSQL**: descartada porque cada verificación de token implicaría una consulta adicional a la base de datos relacional en cada petición autenticada, lo cual es más lento que una consulta a un almacén en memoria como Redis, y no resuelve el problema de rendimiento del punto (b).
2. **Caché en memoria local de cada instancia del backend**: descartada porque no es compartida entre instancias; en un escenario de escalado horizontal (múltiples réplicas del backend), un token invalidado en una instancia seguiría siendo válido en otra, y las entradas de caché no serían consistentes entre instancias.