# Notas de diagramas de arquitectura — PFC

Este documento resume el propósito y contenido de cada diagrama entregado en `docs/arquitectura/`, como referencia rápida antes de leer las imágenes.

## C4 Nivel 1 — Contexto
**Archivo:** `C4-Nivel1-Contexto.png`

Muestra el sistema del PFC como una caja negra y sus interacciones con actores externos.

- **Actores:** Usuario Final, Administrador.
- **Sistemas externos:** Base de Datos PostgreSQL.
- **Relaciones:** Usuario y Administrador acceden al sistema vía HTTPS/REST; el sistema lee/escribe en PostgreSQL vía JDBC.

## C4 Nivel 2 — Contenedores
**Archivo:** `C4-Nivel2-Contenedores.png` *(pendiente)*

Va a mostrar los contenedores de software que componen el sistema: frontend (Angular), backend (Spring Boot), base de datos (PostgreSQL) y caché (Redis), con la tecnología y responsabilidad de cada uno.

## C4 Nivel 3 — Componentes
**Archivo:** `C4-Nivel3-Componentes.png` *(pendiente)*

Va a descomponer el contenedor backend en sus componentes internos: Controllers, Services, Repositories, capa de seguridad/JWT, y su relación con el ORM.

## Diagrama de clases UML
**Archivo:** `Diagrama-UML-Clases.png` *(pendiente)*

Diagrama de clases refactorizado con todos los paquetes del sistema implementado hasta la entrega actual (exportado desde IntelliJ IDEA Diagrams o PlantUML).

## Diagrama Entidad-Relación
**Archivo:** `Diagrama-ER.png` *(pendiente)*

Generado desde pgAdmin 4 ERD Tool, muestra las tablas de la base de datos PostgreSQL y sus relaciones.

---
*Última actualización: 13 de julio de 2026 — avance inicial, C4 Nivel 1 completado.*