# Script para configurar Java 25
# IMPORTANTE: Ejecuta PowerShell como Administrador para que funcione

# Ruta donde instalaste el JDK
$JAVA_HOME = "C:\Program Files\Java\jdk-25.0.1"
$javaPath = "$JAVA_HOME\bin"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Configurando Java 25 como predeterminado" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Verificar si la ruta existe
if (Test-Path $JAVA_HOME) {
    Write-Host "✓ JDK 25 encontrado en: $JAVA_HOME" -ForegroundColor Green
    
    # Configurar JAVA_HOME para el sistema
    Write-Host "`nConfigurando JAVA_HOME..." -ForegroundColor Yellow
    [System.Environment]::SetEnvironmentVariable("JAVA_HOME", $JAVA_HOME, [System.EnvironmentVariableTarget]::Machine)
    
    # Obtener el PATH actual
    $currentPath = [System.Environment]::GetEnvironmentVariable("Path", [System.EnvironmentVariableTarget]::Machine)
    
    # Eliminar otras versiones de Java del PATH
    Write-Host "Limpiando versiones antiguas de Java del PATH..." -ForegroundColor Yellow
    $pathEntries = $currentPath -split ';'
    $cleanedPath = $pathEntries | Where-Object { 
        $_ -notlike "*Java\jdk-*\bin*" -and 
        $_ -notlike "*Java\jre*\bin*" 
    }
    
    # Agregar Java 25 al INICIO del PATH
    Write-Host "Agregando Java 25 al inicio del PATH..." -ForegroundColor Yellow
    $newPath = "$javaPath;" + ($cleanedPath -join ';')
    [System.Environment]::SetEnvironmentVariable("Path", $newPath, [System.EnvironmentVariableTarget]::Machine)
    
    Write-Host "✓ PATH actualizado correctamente" -ForegroundColor Green
    
    Write-Host "`n========================================" -ForegroundColor Green
    Write-Host "✓ Configuración completada!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "`nCierra y vuelve a abrir PowerShell/VS Code para aplicar los cambios" -ForegroundColor Yellow
    Write-Host "`nLuego ejecuta: java -version" -ForegroundColor Cyan
    
} else {
    Write-Host "✗ ERROR: No se encontró el JDK en: $JAVA_HOME" -ForegroundColor Red
    Write-Host "`nPosibles soluciones:" -ForegroundColor Yellow
    Write-Host "1. Verifica que hayas instalado el JDK" -ForegroundColor White
    Write-Host "2. Edita este script y cambia la ruta en la línea 5" -ForegroundColor White
    Write-Host "   Busca en: C:\Program Files\Java\" -ForegroundColor White
    Write-Host "`nRutas comunes:" -ForegroundColor Yellow
    Write-Host "   - C:\Program Files\Java\jdk-25" -ForegroundColor White
    Write-Host "   - C:\Program Files\Java\jdk-25.0.0" -ForegroundColor White
    Write-Host "   - C:\Program Files (x86)\Java\jdk-25" -ForegroundColor White
}

Write-Host "`nPresiona cualquier tecla para salir..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
