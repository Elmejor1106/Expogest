# 🚀 Guía de Despliegue en Render

## Preparación Completada ✅

Tu aplicación ExpoGest ya está lista para desplegarse en Render. Se han creado los siguientes archivos:

- ✅ `render.yaml` - Configuración de servicio para Render
- ✅ `system.properties` - Versión de Java y Maven
- ✅ `Procfile` - Comando de inicio
- ✅ `application-prod.properties` - Configuración de producción

## 📋 Pasos para Desplegar

### 1. Preparar el Repositorio

```bash
# Inicializar Git (si no está inicializado)
git init

# Agregar todos los archivos
git add .

# Hacer commit
git commit -m "Preparar aplicación para despliegue en Render"

# Crear repositorio en GitHub y conectar
git remote add origin https://github.com/TU_USUARIO/expogest.git
git branch -M main
git push -u origin main
```

### 2. Crear Servicio en Render

1. Ve a [render.com](https://render.com) y crea una cuenta o inicia sesión
2. Haz clic en **"New +"** → **"Web Service"**
3. Conecta tu repositorio de GitHub
4. Render detectará automáticamente `render.yaml`

### 3. Configurar Variables de Entorno

En el dashboard de Render, agrega estas variables de entorno:

| Variable | Valor |
|----------|-------|
| `MONGODB_URI` | `mongodb+srv://juandavidduranmalaver_db_user:Uts2025.@serverjuan.mdl6ggs.mongodb.net/expogest` |
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `JAVA_VERSION` | `21` |

### 4. Configuración del Servicio

Si Render no detecta automáticamente el `render.yaml`, configura manualmente:

- **Name**: `expogest`
- **Environment**: `Java`
- **Build Command**: `./mvnw clean package -DskipTests`
- **Start Command**: `java -Dserver.port=$PORT -jar target/expogest-0.0.1-SNAPSHOT.jar`
- **Plan**: Free (o el que prefieras)

### 5. Desplegar

1. Haz clic en **"Create Web Service"**
2. Render comenzará a construir y desplegar tu aplicación
3. Espera 5-10 minutos para el primer despliegue
4. Una vez completado, obtendrás una URL como: `https://expogest.onrender.com`

## 🔧 Configuración Importante

### Java Version
- **Desarrollo**: Java 25
- **Producción (Render)**: Java 21 (más estable y compatible)

### Puerto
- La aplicación usará el puerto dinámico `$PORT` proporcionado por Render
- En desarrollo sigue usando el puerto 8115

### Base de Datos
- Tu MongoDB Atlas ya está configurado correctamente
- La URI se lee desde la variable de entorno `MONGODB_URI`

## 🐛 Solución de Problemas

### Error: "Build failed"
- Verifica que el `mvnw` tenga permisos de ejecución
- Revisa los logs de build en Render

### Error: "Application failed to start"
- Verifica que la variable `MONGODB_URI` esté configurada
- Revisa los logs de la aplicación en Render

### Error: "Cannot connect to MongoDB"
- Verifica que la IP de Render esté permitida en MongoDB Atlas
- En MongoDB Atlas → Network Access → Allow Access from Anywhere (0.0.0.0/0)

## 📝 Actualizaciones Futuras

Para actualizar la aplicación desplegada:

```bash
# Hacer cambios en el código
git add .
git commit -m "Descripción de los cambios"
git push origin main
```

Render detectará automáticamente los cambios y redespleará la aplicación.

## 🌐 URLs de Acceso

- **Producción**: `https://expogest.onrender.com` (después del despliegue)
- **Desarrollo**: `http://localhost:8115`

## 📊 Monitoreo

- **Logs**: Disponibles en el dashboard de Render
- **Métricas**: CPU, memoria y tráfico en el dashboard
- **Status**: Estado del servicio y uptime

## ⚠️ Notas Importantes

1. **Plan Free de Render**: 
   - Se duerme después de 15 minutos de inactividad
   - Primera carga puede tardar 30-60 segundos

2. **MongoDB Atlas**:
   - Asegúrate de tener el plan gratuito activo
   - Configura el acceso de red correctamente

3. **Seguridad**:
   - Considera usar variables de entorno para credenciales sensibles
   - En producción, usa contraseñas más seguras

## 🎉 ¡Listo!

Tu aplicación ExpoGest está lista para ser desplegada en Render. Sigue los pasos anteriores y tendrás tu aplicación en la nube en minutos.
