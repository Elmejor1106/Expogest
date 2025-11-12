# 🚀 Comandos para Desplegar en Render

## Paso 1: Inicializar Git y Subir a GitHub

# 1. Inicializar repositorio Git (si no está inicializado)
git init

# 2. Agregar todos los archivos
git add .

# 3. Crear primer commit
git commit -m "feat: Preparar ExpoGest para despliegue en Render

- Configurar Java 21 para compatibilidad con Render
- Agregar render.yaml con configuración de servicio
- Crear application-prod.properties con configuración de producción
- Agregar Procfile y system.properties
- Implementar diseño profesional con colores amarillo/gris
- Completar CRUDs de usuarios, eventos y stands
- Agregar sistema de autenticación por roles"

# 4. Crear repositorio en GitHub
# Ve a: https://github.com/new
# Nombre sugerido: expogest
# No inicialices con README (ya tienes uno)

# 5. Conectar con el repositorio remoto
git remote add origin https://github.com/TU_USUARIO/expogest.git

# 6. Renombrar rama a main (si es necesario)
git branch -M main

# 7. Subir a GitHub
git push -u origin main

## Paso 2: Configurar MongoDB Atlas

# 1. Ve a: https://cloud.mongodb.com
# 2. Inicia sesión con tu cuenta
# 3. Ve a: Network Access → Add IP Address
# 4. Selecciona: "Allow Access from Anywhere" (0.0.0.0/0)
# 5. Guarda los cambios

## Paso 3: Crear Servicio en Render

# 1. Ve a: https://render.com
# 2. Inicia sesión o crea una cuenta
# 3. Haz clic en: "New +" → "Web Service"
# 4. Conecta tu cuenta de GitHub
# 5. Selecciona el repositorio: expogest
# 6. Render detectará automáticamente render.yaml

## Paso 4: Configurar Variables de Entorno en Render

# En el dashboard de Render, agrega estas variables:

MONGODB_URI=mongodb+srv://juandavidduranmalaver_db_user:Uts2025.@serverjuan.mdl6ggs.mongodb.net/expogest
SPRING_PROFILES_ACTIVE=prod
JAVA_VERSION=21

## Paso 5: Desplegar

# 1. Haz clic en "Create Web Service"
# 2. Espera 5-10 minutos mientras Render construye tu aplicación
# 3. Una vez completado, obtendrás una URL como:
#    https://expogest.onrender.com

## 🎉 ¡Listo!

# Tu aplicación estará disponible en:
# https://TU_APP.onrender.com

# Credenciales de acceso:
# Usuario: admin@expogest.com
# Contraseña: admin123

## 📝 Actualizaciones Futuras

# Para actualizar la aplicación:
git add .
git commit -m "Descripción de los cambios"
git push origin main

# Render detectará automáticamente los cambios y redespleará

## 🐛 Si algo sale mal

# Ver logs en Render:
# Dashboard → Tu servicio → Logs

# Redeployar manualmente:
# Dashboard → Tu servicio → Manual Deploy

## ⚠️ Notas Importantes

# 1. El plan FREE de Render:
#    - Se duerme después de 15 minutos sin actividad
#    - La primera carga después de dormir puede tardar 30-60 segundos
#    - Es perfecto para desarrollo y demostración

# 2. MongoDB Atlas:
#    - El plan FREE tiene límite de 512 MB
#    - Suficiente para desarrollo y proyectos pequeños

# 3. Para producción real:
#    - Considera actualizar al plan Starter de Render ($7/mes)
#    - Esto evita que la aplicación se duerma
