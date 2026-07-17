# ADR-002: Selección de Angular como framework de frontend

## Estado
Aceptado

## Contexto
El PFC requiere un framework de frontend para consumir la API REST del backend (Spring Boot) y renderizar la interfaz de usuario. El equipo evaluó dos opciones principales del ecosistema JavaScript/TypeScript: **Angular 17+** y **React**.

Los criterios de evaluación considerados fueron:
- Estructura y convenciones impuestas por el framework (opinionated vs. flexible).
- Curva de aprendizaje para un equipo de estudiantes con experiencia previa limitada en frontend avanzado.
- Integración nativa con TypeScript y tipado fuerte.
- Manejo de estado y de peticiones HTTP de forma integrada.
- Consistencia con el resto del stack, orientado a un desarrollo estructurado en capas (similar al backend con Spring Boot).
- Disponibilidad de herramientas para pruebas unitarias y documentación oficial.

Angular es un framework completo ("batteries-included") que impone una estructura de proyecto definida (módulos, componentes, servicios, inyección de dependencias), mientras que React es una biblioteca de UI que requiere decisiones adicionales del equipo sobre enrutamiento, manejo de estado y estructura de carpetas, normalmente resueltas con librerías de terceros (React Router, Redux o Context API).

Estudios de rendimiento comparativo entre frameworks modernos de frontend (Angular, React, Vue, Svelte y Blazor) muestran diferencias significativas en las estrategias de renderizado, con implicaciones prácticas de rendimiento que pueden variar hasta en varios órdenes de magnitud según la complejidad de la aplicación (Ollila et al., 2022). Estas diferencias derivan principalmente de cómo cada framework gestiona las transiciones de estado en la interfaz de usuario, un factor relevante para justificar la elección de una arquitectura de frontend predecible y estructurada como Angular.

## Decisión
Se selecciona **Angular 17+** como framework de frontend para el PFC.

Los factores decisivos fueron:
- Angular provee una arquitectura predefinida basada en componentes, servicios e inyección de dependencias, lo cual reduce las decisiones de diseño que el equipo debe tomar por su cuenta y mantiene consistencia con el enfoque en capas usado en el backend.
- El uso de TypeScript es nativo y obligatorio en Angular, lo que refuerza el tipado fuerte y reduce errores en tiempo de desarrollo, algo valioso para un equipo con experiencia limitada.
- Angular incluye de forma integrada el módulo `HttpClient` para consumo de APIs REST y un sistema de inyección de dependencias que facilita la organización de servicios, sin depender de configuración adicional de terceros.
- Al ser un framework completo, reduce el riesgo de que decisiones de arquitectura de frontend queden inconsistentes entre los tres integrantes del equipo, un riesgo mayor en React por su naturaleza más flexible y menos prescriptiva.

## Consecuencias

**Positivas:**
- Estructura de proyecto consistente y predecible, más fácil de mantener con un equipo pequeño trabajando en paralelo.
- Menor necesidad de tomar decisiones adicionales de arquitectura de frontend (enrutamiento, inyección de dependencias, manejo de formularios ya vienen integrados).
- Tipado fuerte con TypeScript reduce errores en tiempo de desarrollo y mejora el autocompletado/documentación en el editor.
- Curva de aprendizaje más guiada gracias a la documentación oficial y las convenciones estrictas del framework.

**Negativas / trade-offs:**
- Angular tiene una curva de aprendizaje inicial más pronunciada que React para conceptos específicos (RxJS, ciclo de vida de componentes, módulos).
- Mayor tamaño del bundle final en comparación con aplicaciones React equivalentes, aunque esto no es crítico para el alcance del PFC.
- Menor flexibilidad para personalizar la arquitectura del frontend si el equipo quisiera un enfoque distinto al impuesto por Angular.

## Alternativas consideradas

1. **React**: descartado por requerir decisiones adicionales de arquitectura (enrutamiento, manejo de estado, estructura de carpetas) que debían resolverse con librerías externas, lo cual introduce mayor riesgo de inconsistencia entre los tres integrantes del equipo dado el plazo académico limitado. Su mayor flexibilidad es una ventaja en proyectos con necesidades muy específicas de personalización, pero no era prioritaria para el PFC.
2. **Vue.js**: no se evaluó en profundidad por no ser parte del stack tecnológico definido inicialmente para el PFC (Angular 17+ especificado en los requisitos del proyecto).

## Referencias
Ollila, R., Mäkitalo, N., & Mikkonen, T. (2022). Modern web frameworks: A comparison of rendering performance. *Journal of Web Engineering, 21*(3), 1–35. https://doi.org/10.13052/jwe1540-9589.21311