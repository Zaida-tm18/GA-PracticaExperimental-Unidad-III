#!/usr/bin/env bash
#
# Paso 3.2 - Benchmark: medir el speedup de la cache
# S = Tsin / Tcon, 10 repeticiones sobre la consulta principal (GET /api/entidades)
#
# Uso:
#   1) Levantar el stack: docker compose up -d
#   2) Levantar la app: mvn spring-boot:run
#   3) Ejecutar: bash benchmark.sh
#
# Metodologia (ver fundamento teorico 5.4):
#   - Se aisla la variable: misma query, mismas condiciones de red (localhost).
#   - n = 10 repeticiones para calcular media y desviacion estandar.
#   - "Sin cache": se fuerza un CacheEvict antes de cada peticion para
#     garantizar un cache-miss real (no solo la primera peticion del batch).
#   - "Con cache": se deja la entrada cacheada de la peticion anterior
#     (cache-hit) para las repeticiones 2..10; ver nota mas abajo.

set -euo pipefail

BASE_URL="http://localhost:8080/api/entidades?page=0&size=20&sort=precio,desc"
REPETICIONES=10
CSV_OUT="resultados_benchmark.csv"

echo "iteracion,sin_cache_ms,con_cache_ms" > "$CSV_OUT"

medir_ms() {
  local url=$1
  # curl -w reporta time_total en segundos con 6 decimales; convertir a ms
  local tiempo
  tiempo=$(curl -s -o /dev/null -w "%{time_total}" "$url")
  echo "scale=3; $tiempo * 1000" | bc
}

echo "Ejecutando $REPETICIONES repeticiones..."
echo "iteracion | sin_cache(ms) | con_cache(ms)"
echo "----------------------------------------"

for i in $(seq 1 "$REPETICIONES"); do
  # --- SIN CACHE: invalidar antes de medir para forzar cache-miss ---
  curl -s -X POST "http://localhost:8080/actuator/caches/entidades/clear" \
       -H "Content-Type: application/json" > /dev/null 2>&1 || true
  # Alternativa si no se expone el endpoint de actuator para cache:
  # limpiar manualmente con redis-cli: redis-cli FLUSHDB (solo en entorno de pruebas)
  redis-cli FLUSHDB > /dev/null 2>&1 || true

  sin_cache=$(medir_ms "$BASE_URL")

  # --- CON CACHE: misma peticion inmediatamente despues (debe ser cache-hit) ---
  con_cache=$(medir_ms "$BASE_URL")

  echo "$i | $sin_cache | $con_cache"
  echo "$i,$sin_cache,$con_cache" >> "$CSV_OUT"
done

echo ""
echo "Resultados guardados en $CSV_OUT"
echo "Ejecuta: python3 analizar_benchmark.py  para obtener media, P95 y speedup S."
