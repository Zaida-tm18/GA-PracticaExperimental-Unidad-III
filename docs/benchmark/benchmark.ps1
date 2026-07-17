# =========================================================
# Paso 3.2 - Benchmark: medir el speedup de la cache
# S = Tsin / Tcon, 10 repeticiones sobre la consulta principal (GET /api/entidades)
#
# Uso:
#   1) Levantar el stack: docker compose up -d
#   2) Levantar la app:  mvn spring-boot:run
#   3) Ejecutar:         .\benchmark.ps1
#
# Metodologia (ver fundamento teorico 5.4):
#   - Se aisla la variable: misma query, mismas condiciones de red (localhost).
#   - n = 10 repeticiones para calcular media y desviacion estandar.
#   - "Sin cache": se hace FLUSHDB en Redis antes de cada peticion para
#     garantizar un cache-miss real.
#   - "Con cache": se deja la entrada cacheada de la peticion anterior
#     (cache-hit) para la segunda medicion de cada iteracion.
#   - Todos los endpoints /api/entidades requieren JWT (SecurityConfig:
#     anyRequest().authenticated()), asi que primero se hace login y se
#     reutiliza la cookie HttpOnly (WebSession) en todas las peticiones.
# =========================================================

$ErrorActionPreference = "Stop"

$LoginUrl       = "http://localhost:8080/api/auth/login"
$BaseUrl        = "http://localhost:8080/api/entidades?page=0&size=20&sort=precio,desc"
$Repeticiones   = 10
$CsvOut         = "resultados_benchmark.csv"
$RedisContainer = "pfc-redis"

# Credenciales del seeder (V3__datos_semilla.sql) - password real: Password123!
$LoginBody = @{
    email    = "ana.torres@uteq.edu.ec"
    password = "Password123!"
} | ConvertTo-Json

# --- LOGIN: crea una sesion web que retiene la cookie HttpOnly access_token ---
Write-Host "Autenticando..."
$session = $null
try {
    Invoke-WebRequest -Uri $LoginUrl -Method POST -Body $LoginBody `
        -ContentType "application/json" -SessionVariable session -UseBasicParsing | Out-Null
    Write-Host "Login OK. Cookie de sesion obtenida." -ForegroundColor Green
} catch {
    Write-Host "ERROR: no se pudo hacer login -> $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Verifica que el usuario 'ana.torres@uteq.edu.ec' exista con password 'Password123!'" -ForegroundColor Red
    exit 1
}

# Encabezado del CSV
"iteracion,sin_cache_ms,con_cache_ms" | Out-File -FilePath $CsvOut -Encoding utf8

function Medir-Ms {
    param([string]$Url, $WebSession)
    $sw = [System.Diagnostics.Stopwatch]::StartNew()
    try {
        $resp = Invoke-WebRequest -Uri $Url -WebSession $WebSession -UseBasicParsing -TimeoutSec 30
        if ($resp.StatusCode -ne 200) {
            Write-Host "  ADVERTENCIA: status $($resp.StatusCode) en $Url" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "  ADVERTENCIA: la peticion fallo -> $($_.Exception.Message)" -ForegroundColor Yellow
    }
    $sw.Stop()
    return [math]::Round($sw.Elapsed.TotalMilliseconds, 2)
}

Write-Host ""
Write-Host "Ejecutando $Repeticiones repeticiones..."
Write-Host "iteracion | sin_cache(ms) | con_cache(ms)"
Write-Host "----------------------------------------"

for ($i = 1; $i -le $Repeticiones; $i++) {

    # --- SIN CACHE: invalidar antes de medir para forzar cache-miss ---
    try {
        docker exec $RedisContainer redis-cli FLUSHDB | Out-Null
    } catch {
        Write-Host "  ADVERTENCIA: no se pudo hacer FLUSHDB en $RedisContainer" -ForegroundColor Yellow
    }
    # Pequena pausa para que la conexion a Redis se reestablezca por completo
    # antes de que JwtAuthFilter verifique la blacklist (evita el 403 transitorio).
    Start-Sleep -Milliseconds 300

    $sinCache = Medir-Ms -Url $BaseUrl -WebSession $session

    # --- CON CACHE: misma peticion inmediatamente despues (debe ser cache-hit) ---
    $conCache = Medir-Ms -Url $BaseUrl -WebSession $session

    Write-Host "$i | $sinCache | $conCache"
    "$i,$sinCache,$conCache" | Out-File -FilePath $CsvOut -Encoding utf8 -Append
}

Write-Host ""
Write-Host "Resultados guardados en $CsvOut"
Write-Host "Ejecuta: python3 analizar_benchmark.py  para obtener media, P95 y speedup S."
