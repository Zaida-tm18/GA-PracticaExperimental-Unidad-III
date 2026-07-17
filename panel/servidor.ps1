# Mini servidor HTTP en PowerShell puro (sin depender de Python ni Node)
# Sirve el archivo index.html de la carpeta actual en http://localhost:5500

$port = 5500
$root = $PSScriptRoot
$listener = New-Object System.Net.HttpListener
$listener.Prefixes.Add("http://localhost:$port/")
$listener.Start()
Write-Host "Servidor corriendo en http://localhost:$port  (Ctrl+C para detener)"

try {
    while ($listener.IsListening) {
        $context = $listener.GetContext()
        $request = $context.Request
        $response = $context.Response

        $filePath = Join-Path $root "index.html"

        if (Test-Path $filePath) {
            $content = [System.IO.File]::ReadAllBytes($filePath)
            $response.ContentType = "text/html; charset=utf-8"
            $response.ContentLength64 = $content.Length
            $response.OutputStream.Write($content, 0, $content.Length)
        } else {
            $response.StatusCode = 404
        }

        $response.OutputStream.Close()
    }
} finally {
    $listener.Stop()
}
