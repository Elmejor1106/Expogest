# 🔥 Desarrollo con Hot Reload - ExpoGest

## 🚀 Modo Desarrollo (Recomendado para programar)

Ejecuta la aplicación SIN Docker para aprovechar el hot reload:

```powershell
# Configura Java 25
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.1"
$env:Path = "C:\Program Files\Java\jdk-25.0.1\bin;$env:Path"

# Ejecuta con Maven
.\mvnw.cmd spring-boot:run
```

### ✅ Ventajas del Modo Desarrollo:
- ✨ **Hot Reload automático** - Spring DevTools recarga cambios sin reiniciar
- ⚡ **Más rápido** - No necesitas reconstruir la imagen Docker
- 🐛 **Mejor debugging** - Puedes usar el debugger de VS Code directamente
- 📝 **Ver logs inmediatamente** - Logs directos en la terminal

### 🔄 Qué se recarga automáticamente:
- ✅ Cambios en clases Java (controllers, services, entities)
- ✅ Cambios en templates HTML (Thymeleaf)
- ✅ Cambios en archivos estáticos (CSS, JS)
- ✅ Cambios en properties

### ❌ Qué NO se recarga (requiere reiniciar):
- Cambios en `pom.xml` (nuevas dependencias)
- Cambios estructurales mayores

## 🐳 Modo Docker (Para pruebas finales)

Usa Docker solo cuando:
- Quieras probar en un ambiente limpio
- Vayas a desplegar a producción
- Necesites probar la configuración Docker

```powershell
# Reconstruir y ejecutar
docker-compose down
docker-compose up -d --build

# Ver logs
docker-compose logs -f
```

## 🎯 Flujo de Trabajo Ideal

### 1️⃣ Mientras Desarrollas (90% del tiempo):
```powershell
.\mvnw.cmd spring-boot:run
# Edita código → Guarda → Se recarga automáticamente ✨
```

### 2️⃣ Antes de Commitear (verificación):
```powershell
# Compila y ejecuta tests
.\mvnw.cmd clean test

# Si todo está bien, haz commit
git add .
git commit -m "Nueva funcionalidad"
```

### 3️⃣ Antes de Desplegar (validación final):
```powershell
# Prueba con Docker
docker-compose up -d --build

# Verifica que funcione en http://localhost:8115
# Si todo OK, publica la imagen
docker push tuusuario/expogest:latest
```

## ⚙️ Configuración Opcional: Script de Desarrollo

Crea un archivo `dev.ps1`:

```powershell
# dev.ps1 - Script para iniciar desarrollo rápido
Write-Host "🚀 Iniciando ExpoGest en modo desarrollo..." -ForegroundColor Green

# Configurar Java 25
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.1"
$env:Path = "C:\Program Files\Java\jdk-25.0.1\bin;$env:Path"

# Verificar Java
java -version

# Iniciar aplicación
Write-Host "✨ Hot reload activado - Los cambios se recargarán automáticamente" -ForegroundColor Cyan
.\mvnw.cmd spring-boot:run
```

Luego solo ejecutas:
```powershell
.\dev.ps1
```

## 📊 Comparación

| Característica | Sin Docker (Dev) | Con Docker |
|----------------|------------------|------------|
| Hot Reload | ✅ Sí | ❌ No |
| Velocidad | ⚡ Rápido | 🐌 Lento (rebuild) |
| Debugging | ✅ Fácil | ⚠️ Complejo |
| Ambiente | 💻 Local | 📦 Contenedor |
| Uso | 🔧 Desarrollo | 🚀 Producción |

## 🎓 Recomendación

**Para programar diariamente:** 
- Usa `.\mvnw.cmd spring-boot:run` (sin Docker)
- Disfruta del hot reload automático

**Para probar/desplegar:**
- Usa Docker cuando termines funcionalidades
- Valida que todo funcione en el contenedor

---

✨ **Tip:** Mantén una terminal con `.\mvnw.cmd spring-boot:run` mientras programas y solo usa Docker para validaciones finales.
