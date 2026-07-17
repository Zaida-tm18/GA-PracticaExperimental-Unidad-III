# ADR-002: Selección de Hibernate/JPA como ORM

## Estado
Aceptado

## Contexto
El PFC requiere una capa de acceso a datos que mapee el modelo relacional
(PostgreSQL) al modelo orientado a objetos usado en el backend (Spring Boot,
Java). La guía de práctica planteó tres opciones de ORM a evaluar según el
stack tecnológico posible del proyecto:

- **Doctrine** — ORM del ecosistema PHP (Symfony/Laravel).
- **Entity Framework Core (EF Core)** — ORM del ecosistema .NET.
- **Hibernate / JPA** — ORM estándar del ecosistema Java.

Los criterios de evaluación considerados fueron:
- Compatibilidad directa con el lenguaje y framework ya elegidos para el
  backend (Spring Boot 3.x sobre Java 21).
- Madurez y soporte de la comunidad.
- Integración con Spring Data (repositorios, paginación, Specifications
  para filtros dinámicos).
- Manejo de relaciones, lazy/eager loading y mapeo de herencia.
- Curva de aprendizaje para un equipo de estudiantes con experiencia previa
  en Java.

Dado que el backend del PFC está construido en Spring Boot, la elección de
ORM está fuertemente condicionada por el lenguaje: Doctrine y EF Core
pertenecen a ecosistemas distintos (PHP y .NET respectivamente) y no son
compatibles de forma nativa con una aplicación Java/Spring. Aun así, ambos
se evaluaron como alternativas hipotéticas para justificar formalmente por
qué Hibernate/JPA es la opción correcta y no solo la opción "por defecto".

## Decisión
Se selecciona **Hibernate como implementación de JPA**, integrado a través
de **Spring Data JPA**, como ORM del PFC.

Los factores decisivos fueron:
- Es el ORM estándar de facto en el ecosistema Java/Spring, con integración
  nativa a Spring Boot sin configuración adicional de terceros.
- Spring Data JPA provee `JpaRepository`, que reduce drásticamente el
  código boilerplate para operaciones CRUD, paginación (`Pageable`) y
  ordenamiento dinámico, requisitos explícitos del OE2.
- Soporta `Specifications` para construir filtros múltiples combinables de
  forma programática, necesario para el endpoint de listado del PFC.
- Manejo maduro de relaciones (`@OneToMany`, `@ManyToOne`) y estrategias de
  fetch (lazy/eager), permitiendo controlar explícitamente el problema N+1
  descrito en el fundamento teórico de la guía.
- Amplia documentación oficial y comunidad, lo que reduce el riesgo para un
  equipo con plazo académico limitado.

## Consecuencias

**Positivas:**
- Integración directa con Spring Boot: no se requiere configuración manual
  de conexión ORM-framework.
- Código de la capa Repository muy reducido gracias a `JpaRepository` y
  Query Methods derivados por convención de nombres.
- Facilita alcanzar la cobertura de pruebas unitarias ≥70% exigida por el
  OE4, al probar contra una capa Repository delgada y bien definida.
- Flyway (usado para migraciones versionadas) se integra sin fricción con
  Hibernate, ya que este último no gestiona el esquema en producción
  (`ddl-auto=validate`), evitando el riesgo de `hbm2ddl.auto=update`
  descrito en el fundamento teórico.

**Negativas / trade-offs:**
- Riesgo del problema N+1 si no se usa `JOIN FETCH` o `@EntityGraph` al
  navegar relaciones (`Entidad` → `Categoria`), mitigado en el PFC
  cargando explícitamente las relaciones necesarias en las consultas del
  listado paginado.
- Curva de aprendizaje en conceptos propios de JPA (estados de entidad,
  ciclo de vida del `EntityManager`, diferencias entre `CascadeType`) frente
  a escribir SQL puro.
- Menor control granular sobre las queries generadas en comparación con un
  query builder o SQL directo, aunque esto se mitiga habilitando el log de
  queries de Hibernate durante el desarrollo.

## Alternativas consideradas

1. **Doctrine**: descartado por pertenecer al ecosistema PHP, incompatible
   con un backend Java/Spring Boot. Habría sido la opción natural si el
   backend se hubiera construido en Laravel/Symfony, pero ese no era el
   stack elegido para el PFC.
2. **Entity Framework Core**: descartado por la misma razón — pertenece al
   ecosistema .NET/C#. Ofrece ventajas similares a Hibernate en cuanto a
   integración con su framework nativo (ASP.NET Core), pero implicaría
   reescribir el backend completo en otro lenguaje, lo cual no se justifica
   dado que el equipo ya tenía código y experiencia en Spring Boot.

## Referencias
- Fowler, M. (2002). *Patterns of Enterprise Application Architecture*.
  Addison-Wesley. ISBN 978-0-321-12521-7.
- Spring Data JPA, "Reference Documentation," 2024. [Online]. Available:
  https://docs.spring.io/spring-data/jpa/reference/
