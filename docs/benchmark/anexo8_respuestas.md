# Anexo 8 — Preguntas de Análisis, Síntesis y Evaluación (parte del equipo)

## [Análisis] Pregunta 1

**En el benchmark de caché, el speedup obtenido fue S = 3.17x.**
**¿Este resultado justifica la complejidad operacional de añadir Redis al stack del PFC?**

Sí. El umbral de referencia (S > 2) se supera: la consulta principal
del PFC pasó de una media de 45.49 ms (consultando PostgreSQL con join a
`categorias` y paginación) a 14.36 ms (respondida desde Redis), un speedup de
3.17x. Considerando los tres criterios pedidos:

- **Costos de infraestructura adicional**: un contenedor Redis (`redis:7-alpine`)
  tiene una huella de memoria y CPU mínima comparada con el motor relacional;
  en el `docker-compose.yml` del proyecto no añade un costo operativo relevante
  para un PFC universitario.
- **Complejidad de despliegue**: se añade un servicio más a orquestar, pero
  Docker Compose lo resuelve con una entrada adicional; no requiere
  configuración de clúster ni sharding para el volumen de datos del PFC (55
  registros semilla, tráfico de un equipo de práctica, no de producción real).
- **Posibles fallos de Redis y su impacto en disponibilidad**: el patrón
  cache-aside implementado es *fail-open* por diseño: si Redis no responde,
  la petición simplemente no encuentra la clave en caché y cae a consultar
  PostgreSQL directamente (comportamiento por defecto de Spring Cache cuando
  el `RedisCacheManager` no puede conectar). Esto significa que una caída de
  Redis degrada el rendimiento (se pierde el speedup) pero no tumba el
  sistema — la disponibilidad del CRUD no depende de Redis.

**Conclusión**: dado que S=3.17x supera el umbral de S>2, y que el riesgo de
disponibilidad es bajo por el diseño fail-open del patrón, sí se justifica
mantener Redis en el stack. El speedup es más moderado que en un escenario
de mayor latencia de base de datos porque el dataset semilla es pequeño (55
registros) y corre en `localhost`, donde la latencia de red y de disco es
mínima; en un entorno con más datos y con la base de datos en un host
distinto al backend, la diferencia relativa entre consultar PostgreSQL y
leer de Redis tiende a ampliarse.

---

## [Síntesis] Pregunta 2

**Diseñe la estrategia de invalidación de caché para el módulo principal del
PFC: ¿qué claves de Redis se invalidan cuando se crea, actualiza o elimina
una entidad? ¿Hay riesgo de cache stampede en el PFC? ¿Cómo lo mitigaría?**

### Estrategia de invalidación implementada

El cache de listados (`@Cacheable(value = "entidades", key = "'listado:' + ...")`)
genera una clave distinta por cada combinación de página, tamaño, orden y
filtros (por ejemplo `entidades::listado:0:20:precio,desc:null:null:null:null:null`).
Como el número de combinaciones posibles de filtros es grande, **no es
práctico invalidar clave por clave**; en su lugar se usa:

```java
@CacheEvict(value = "entidades", allEntries = true)
```

en los tres métodos de escritura (`crear`, `actualizar`, `eliminar`). Esto
borra **todas** las entradas del cache `entidades` ante cualquier
modificación, garantizando que ningún cliente reciba datos obsoletos.

**Trade-off asumido**: se invalida más de lo estrictamente necesario (por
ejemplo, borrar una entidad de la categoría "Hogar" invalida también los
listados filtrados por "Electrónica", que no cambiaron). Para el volumen de
escrituras de un PFC universitario esto es aceptable; en un sistema de
producción con alto volumen de escrituras se preferiría una invalidación más
selectiva (por ejemplo, incluyendo el `categoriaId` en la clave de forma que
solo se invaliden los listados de esa categoría, o usando un `CachePut` en
lugar de `CacheEvict` para refrescar en vez de borrar).

### Riesgo de cache stampede en el PFC

Sí existe el riesgo, aunque acotado. El escenario sería: justo después de un
`@CacheEvict(allEntries = true)` (por ejemplo, tras crear una entidad),
múltiples clientes piden el mismo listado simultáneamente. Todos experimentan
un cache-miss al mismo tiempo y todos golpean PostgreSQL de forma concurrente
para la misma consulta, en vez de que solo una petición la resuelva y las
demás esperen ese resultado.

Para el tráfico esperado de un PFC (equipo de práctica, no producción con
miles de usuarios concurrentes) el impacto es bajo, pero la mitigación
recomendada, documentada aquí para escalar el diseño, sería:

1. **Mutex/lock distribuido en Redis** (`SETNX` con TTL corto): la primera
   petición que detecta el miss adquiere un lock, consulta la BD y llena el
   caché; las peticiones concurrentes esperan brevemente el resultado en
   lugar de consultar la BD por su cuenta.
2. **Probabilistic early expiration**: recalcular el valor en caché *antes*
   de que expire, con una probabilidad creciente a medida que se acerca el
   TTL, evitando que muchas claves expiren exactamente al mismo tiempo.

Ninguna de las dos se implementó en esta entrega porque el patrón cache-aside
simple con `@Cacheable`/`@CacheEvict` cumple el objetivo del Paso 3 (demostrar
el patrón y medir el speedup); se documenta como mejora futura ante un
escenario de mayor concurrencia.

---

## [Evaluación] Pregunta 4

**La cobertura alcanzó 100% en la capa Repository. Aun así, hay tipos de bugs que las pruebas unitarias con base de datos en memoria no capturan: diferencias de comportamiento entre H2 y PostgreSQL en producción (funciones SQL específicas, tipos de datos, collation en comparaciones LIKE), problemas de concurrencia bajo carga real, y el rendimiento real de las queries con volúmenes grandes de datos. Pruebas de integración contra PostgreSQL real (Testcontainers) y pruebas de carga complementarían esta cobertura unitaria.**

> Completar el porcentaje real ejecutando `mvn test` y revisando
> `target/site/jacoco/index.html` (ver `docs/evidencias/README.md`).
> Esta entrega está diseñada para superar el 70% exigido: 12 pruebas cubren
> el CRUD heredado de `JpaRepository`, las 5 `Specification` de filtros
> combinadas y no combinadas, paginación y ordenamiento.

**Casos de prueba adicionales de bajo costo de mantenimiento:**

- Pruebas de **valores límite** en `stockMinimo` y `precioMinimo`/`precioMaximo`
  (por ejemplo, stock exactamente igual al mínimo, no solo mayor).
- Prueba de **filtro combinado con resultado vacío** (ningún registro cumple
  todas las condiciones a la vez), para confirmar que el `AND` de las
  `Specification` no degrada a un `OR` por error de implementación.
- Prueba de **integridad referencial**: intentar guardar una `Entidad` con una
  `Categoria` no persistida (id nulo) y verificar que la excepción de
  Hibernate se propaga correctamente.
- Prueba de **unicidad de email** en `UsuarioRepository` (guardar dos usuarios
  con el mismo email y esperar `DataIntegrityViolationException`).

**Bugs que la prueba unitaria del Repository NO captura:**

- **Errores de configuración de Flyway/producción**: las pruebas corren
  contra H2 en memoria con `ddl-auto=create-drop`, no contra el esquema real
  generado por las migraciones de Flyway sobre PostgreSQL. Una migración con
  un tipo de columna incompatible o una constraint mal definida en SQL real
  no se detecta aquí (se necesitaría una prueba de integración con
  Testcontainers + PostgreSQL real para eso).
- **Problemas de concurrencia**: dos transacciones modificando el mismo
  registro al mismo tiempo (race conditions, bloqueos optimistas/pesimistas)
  no se ejercitan en un test unitario secuencial.
- **Comportamiento del cache-aside**: estas pruebas son de la capa
  Repository pura; no verifican que `@Cacheable`/`@CacheEvict` funcionen
  correctamente contra un Redis real (eso se demuestra con el benchmark del
  Paso 3, no con JUnit).
- **Problemas de rendimiento con datos a escala real**: el dataset de prueba
  es pequeño (4-5 registros por test); no revela problemas de N+1 o de
  planes de ejecución lentos que solo aparecen con miles de registros.
