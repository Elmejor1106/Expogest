# 🚀 Guía Rápida para Dockerizar ExpoGest

## ⚠️ IMPORTANTE: Antes de Empezar

1. **Abre Docker Desktop** 
   - Busca "Docker Desktop" en el menú inicio de Windows
   - Espera a que inicie completamente (icono de ballena en la bandeja del sistema)
   - Verás un mensaje "Docker Desktop is running" cuando esté listo

## 📦 Opción 1: Usar Docker Compose (Recomendado - Más Fácil)

```powershell
# 1. Construir y ejecutar
docker-compose up -d --build

# 2. Ver logs
docker-compose logs -f

# 3. Acceder a la aplicación
# http://localhost:8115

# 4. Detener
docker-compose down
```

## 🐳 Opción 2: Docker Manual

```powershell
# 1. Construir la imagen
docker build -t expogest:latest .

# 2. Ejecutar el contenedor
docker run -d -p 8115:8115 --name expogest-app expogest:latest

# 3. Ver logs
docker logs -f expogest-app

# 4. Detener y eliminar
docker stop expogest-app
docker rm expogest-app
```

## ✅ Verificación

Una vez que el contenedor esté corriendo:

1. Abre tu navegador
2. Ve a: `http://localhost:8115`
3. Inicia sesión con:
   - **Email:** admin@expogest.com
   - **Contraseña:** admin123

## 🔍 Comandos Útiles

```powershell
# Ver contenedores activos
docker ps

# Ver todas las imágenes
docker images

# Ver logs del contenedor
docker logs expogest-app

# Entrar al contenedor (shell)
docker exec -it expogest-app sh

# Ver uso de recursos
docker stats expogest-app
```

## 🐛 Solución de Problemas

### Error: "Cannot find file specified" o "Docker daemon not running"
**Solución:** Docker Desktop no está corriendo
1. Abre Docker Desktop
2. Espera a que inicie completamente
3. Vuelve a intentar el comando

### Error: Puerto 8115 ya en uso
**Solución:** Otra aplicación está usando el puerto
```powershell
# Ver qué usa el puerto 8115
netstat -ano | findstr :8115

# Matar el proceso (reemplaza PID con el número que veas)
taskkill /PID <número> /F

# O cambiar el puerto en docker-compose.yml
ports:
  - "8116:8115"  # Ahora usa el puerto 8116
```

### La construcción tarda mucho
**Es normal la primera vez:**
- Descarga Java 25
- Descarga todas las dependencias de Maven
- Compila el proyecto completo
- Puede tardar 5-10 minutos

**Las siguientes construcciones serán más rápidas** gracias al caché de Docker.

## 📊 Información de la Imagen

- **Tamaño aproximado:** ~200-250 MB
- **Java:** Eclipse Temurin 25 JRE
- **Base:** Alpine Linux (muy ligera)
- **Arquitectura:** Multi-stage build (optimizada)
- **Seguridad:** Usuario no-root

## 🌐 Despliegue en la Nube (Opcional)

### Docker Hub
```powershell
# Login
docker login

# Etiquetar
docker tag expogest:latest tuusuario/expogest:latest

# Publicar
docker push tuusuario/expogest:latest
```

### Azure Container Instances
```bash
az container create \
  --resource-group miGrupo \
  --name expogest \
  --image tuusuario/expogest:latest \
  --ports 8115 \
  --dns-name-label expogest-app
```

## 📝 Notas Importantes

1. ✅ La aplicación se conecta a MongoDB Atlas (cloud) - no necesita base de datos local
2. ✅ El admin se crea automáticamente al iniciar
3. ✅ Todos los archivos de configuración están listos
4. ✅ El proyecto está usando Java 25 (última versión LTS)

---

¿Tienes problemas? Revisa que:
- [ ] Docker Desktop esté corriendo
- [ ] El puerto 8115 esté libre
- [ ] Tienes conexión a internet (para MongoDB Atlas)
