# Script rápido para usar Java 25 en la terminal actual
# Ejecuta esto cada vez que abras un nuevo terminal: .\usar-java25.ps1

$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.1"
$env:Path = "C:\Program Files\Java\jdk-25.0.1\bin;$env:Path"

Write-Host "✓ Java 25 configurado en esta terminal" -ForegroundColor Green
java -version
