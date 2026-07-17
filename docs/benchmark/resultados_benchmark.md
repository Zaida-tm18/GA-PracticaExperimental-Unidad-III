## Resultados del Benchmark de Cache (Paso 3.2)

n = 10 repeticiones sobre `GET /api/entidades?page=0&size=20&sort=precio,desc`

| Iteracion | Sin cache (ms) | Con cache (ms) |
|---|---|---|
| 1 | 43.38 | 13.64 |
| 2 | 41.75 | 13.96 |
| 3 | 36.07 | 12.34 |
| 4 | 49.87 | 13.76 |
| 5 | 37.02 | 13.42 |
| 6 | 47.70 | 16.41 |
| 7 | 36.49 | 14.14 |
| 8 | 55.30 | 18.87 |
| 9 | 69.90 | 15.72 |
| 10 | 37.42 | 11.37 |
| **Media** | **45.49** | **14.36** |
| **Desv. estandar** | 10.76 | 2.14 |
| **P95** | 63.33 | 17.76 |

**Speedup S = Tsin / Tcon = 3.17x**

¿Justifica la complejidad operacional de Redis (umbral S > 2)? **SI**
