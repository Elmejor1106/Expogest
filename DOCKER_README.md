# 🐳 Docker - ExpoGest

## 📋 Requisitos Previos

- Docker Desktop instalado ([Descargar aquí](https://www.docker.com/products/docker-desktop))
- Docker Compose (incluido en Docker Desktop)

## 🚀 Comandos Básicos

### 1️⃣ Construir y Ejecutar con Docker Compose (Recomendado)

```bash
# Construir y ejecutar en segundo plano
docker-compose up -d --build

# Ver logs en tiempo real
docker-compose logs -f

# Detener los contenedores
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v
```

### 2️⃣ Construir Imagen Docker Manualmente

```bash
# Construir la imagen
docker build -t expogest:latest .

# Ejecutar el contenedor
docker run -d -p 8115:8115 --name expogest-app expogest:latest

# Ver logs
docker logs -f expogest-app

# Detener el contenedor
docker stop expogest-app

# Eliminar el contenedor
docker rm expogest-app
```

## 🔍 Verificación

Una vez que los contenedores estén corriendo:

- **Aplicación:** http://localhost:8115
- **Login Admin:**
  - Usuario: `admin@expogest.com`
  - Contraseña: `admin123`

## 📊 Comandos Útiles

```bash
# Ver contenedores en ejecución
docker ps

# Ver todas las imágenes
docker images

# Entrar al contenedor (shell)
docker exec -it expogest-app sh

# Ver uso de recursos
docker stats expogest-app

# Ver logs con timestamps
docker-compose logs -f --timestamps

# Reiniciar servicio
docker-compose restart expogest-app
```

## 🔧 Variables de Entorno

Puedes modificar las variables de entorno en `docker-compose.yml`:

```yaml
environment:
  - SPRING_DATA_MONGODB_URI=tu_conexion_mongodb
  - SERVER_PORT=8115
  - JAVA_OPTS=-Xmx512m -Xms256m
```

## 🏗️ Arquitectura Multi-Stage

El `Dockerfile` usa construcción multi-stage para optimizar la imagen:

1. **Etapa Build:** Compila la aplicación con Maven
2. **Etapa Runtime:** Solo ejecuta el JAR con JRE (imagen más liviana)

**Ventajas:**
- ✅ Imagen final más pequeña (~200 MB vs ~800 MB)
- ✅ Mayor seguridad (usuario no-root)
- ✅ Mejor rendimiento

## 🐛 Resolución de Problemas

### El contenedor no inicia

```bash
# Ver logs detallados
docker-compose logs expogest-app

# Verificar que el puerto 8115 no esté en uso
netstat -ano | findstr :8115
```

### Reconstruir desde cero

```bash
# Eliminar todo y reconstruir
docker-compose down -v
docker system prune -a
docker-compose up -d --build
```

### Actualizar la aplicación

```bash
# Detener, reconstruir y reiniciar
docker-compose down
docker-compose up -d --build
```

## 📦 Publicar en Docker Hub (Opcional)

```bash
# Login en Docker Hub
docker login

# Etiquetar la imagen
docker tag expogest:latest tuusuario/expogest:latest

# Publicar
docker push tuusuario/expogest:latest
```

## 🌐 Despliegue en Producción

Para producción, considera:

1. **Variables de entorno externas** (no en docker-compose.yml)
2. **Secrets de Docker** para contraseñas
3. **Health checks** configurados
4. **Logs centralizados** (ELK, Splunk, etc.)
5. **Reverse proxy** (Nginx, Traefik)
6. **HTTPS/SSL** configurado

Ejemplo con archivo `.env`:

```bash
# Crear archivo .env
MONGODB_URI=mongodb+srv://usuario:password@host/database
SERVER_PORT=8115

# Usar en docker-compose.yml
environment:
  - SPRING_DATA_MONGODB_URI=${MONGODB_URI}
  - SERVER_PORT=${SERVER_PORT}
```

## 📝 Notas

- La aplicación se conecta a MongoDB Atlas (cloud)
- El usuario admin se crea automáticamente al iniciar
- El puerto 8115 está expuesto por defecto
- Los logs se guardan en stdout (accesibles con `docker logs`)

---

✅ **Proyecto dockerizado exitosamente con Java 25**
