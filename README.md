# 🎪 ExpoGest - Sistema de Gestión de Eventos

Sistema completo de gestión de eventos, stands y exposiciones construido con Spring Boot y MongoDB.

## 🚀 Características

- ✅ **Gestión de Usuarios** - 5 roles: Administrador, Organizador, Expositor, Visitante, Evaluador
- ✅ **Gestión de Eventos** - Crear, editar y administrar eventos
- ✅ **Gestión de Stands** - Control completo de stands con estados (Disponible, Reservado, Ocupado, Mantenimiento)
- ✅ **Solicitudes de Stands** - Los expositores pueden solicitar stands
- ✅ **Evaluaciones** - Sistema de evaluación de stands
- ✅ **Panel de Control** - Interfaz personalizada para cada rol
- ✅ **Diseño Moderno** - UI profesional con colores amarillo (#FFD600) y gris (#434343)

## 🛠️ Tecnologías

- **Backend:** Spring Boot 3.5.7
- **Base de Datos:** MongoDB Atlas
- **Frontend:** Thymeleaf + Bootstrap 5.3.2
- **Java:** 21 (producción) / 25 (desarrollo)
- **Build Tool:** Maven

## 📦 Instalación Local

### 🐳 Con Docker (Recomendado)

**Requisitos:** Solo Docker Desktop

```bash
# Clonar el repositorio
git clone https://github.com/Elmejor1106/Expogest.git
cd expogest

# Ejecutar con Docker Compose
docker-compose up -d

# Ver logs
docker-compose logs -f
```

**¡Listo!** Abre: http://localhost:8080

### ☕ Sin Docker (Desarrollo)

**Requisitos:** Java 21, Maven 3.9+

```bash
# Clonar el repositorio
git clone https://github.com/Elmejor1106/Expogest.git
cd expogest

# Ejecutar
./mvnw spring-boot:run
```

Abre: http://localhost:8115

### 👤 Credenciales por Defecto
- **Usuario:** admin@expogest.com
- **Contraseña:** admin123

## 🌐 Despliegue en Render

### 🐳 Con Docker (Super Simple)

1. **Tu código ya está en GitHub** ✅

2. **Crear servicio en Render**:
   - Ve a [render.com](https://render.com)
   - New + → Web Service
   - Conecta tu repositorio `Elmejor1106/Expogest`
   - **Environment**: Docker
   - **Variables de entorno**:
     - `MONGODB_URI`: mongodb+srv://...
     - `SPRING_PROFILES_ACTIVE`: prod

3. **Deploy** → Render construye automáticamente con Docker

**¡Listo!** URL: https://tu-app.onrender.com

Ver `DOCKER_README.md` para más detalles.

## 📁 Estructura del Proyecto

```
expogest/
├── src/
│   ├── main/
│   │   ├── java/com/expogest/expogest/
│   │   │   ├── controller/      # Controladores REST
│   │   │   ├── model/           # Modelos/Entidades
│   │   │   ├── repository/      # Repositorios MongoDB
│   │   │   ├── service/         # Lógica de negocio
│   │   │   └── ExpogestApplication.java
│   │   └── resources/
│   │       ├── templates/       # Vistas Thymeleaf
│   │       │   ├── admin/
│   │       │   ├── organizador/
│   │       │   ├── expositor/
│   │       │   ├── visitante/
│   │       │   ├── evaluador/
│   │       │   ├── evento/
│   │       │   └── fragments/   # Componentes reutilizables
│   │       ├── application.properties
│   │       └── application-prod.properties
│   └── test/
├── target/                      # Archivos compilados (ignorados por Git)
├── .gitignore
├── pom.xml                      # Dependencias Maven
├── render.yaml                  # Configuración Render
├── system.properties            # Java version
├── Procfile                     # Comando de inicio
└── README.md

```

## 👥 Roles y Permisos

### 🔧 Administrador
- Gestión completa de usuarios
- Acceso a todas las funcionalidades

### 📋 Organizador
- Gestión de eventos
- Gestión de stands
- Cronogramas
- Revisión de solicitudes

### 🏢 Expositor
- Solicitar stands
- Ver mis solicitudes
- Ver eventos disponibles

### 👤 Visitante
- Inscribirse a eventos
- Ver eventos activos

### ⭐ Evaluador
- Evaluar stands
- Ver eventos

## 🎨 Diseño

El sistema utiliza un esquema de colores profesional:
- **Primario:** Amarillo #FFD600
- **Secundario:** Gris #434343
- **Componentes:** Bootstrap 5.3.2 con personalización
- **Íconos:** Bootstrap Icons 1.11.1

## 📝 Endpoints Principales

### Autenticación
- `GET /login` - Página de login
- `POST /login` - Procesar login
- `GET /registro` - Página de registro
- `GET /logout` - Cerrar sesión

### Administrador
- `GET /admin/panelAdmin` - Panel de administrador
- `GET /admin/usuarios` - Lista de usuarios
- `GET /admin/usuarios/nuevo` - Crear usuario

### Organizador
- `GET /organizador/panelOrganizador` - Panel organizador
- `GET /stands` - Lista de stands
- `GET /eventos` - Lista de eventos

### Eventos
- `GET /eventos` - Listar eventos
- `GET /eventos/nuevo` - Crear evento
- `POST /eventos/guardar` - Guardar evento

### Stands
- `GET /stands` - Listar stands
- `GET /stands/nuevo` - Crear stand
- `POST /stands/guardar` - Guardar stand

## 🔒 Seguridad

- Validación de roles en el backend
- Sesiones seguras
- Validación de formularios
- Protección de endpoints por rol

## 📊 Base de Datos

### Colecciones MongoDB
- `usuarios` - Información de usuarios
- `eventos` - Eventos del sistema
- `stands` - Stands disponibles
- `solicitudes` - Solicitudes de stands
- `evaluaciones` - Evaluaciones realizadas
- `cronogramas` - Cronogramas de eventos
- `participaciones` - Participaciones en eventos

## 🐛 Solución de Problemas

### Error: "Cannot connect to MongoDB"
- Verifica la URI de MongoDB en `application.properties`
- Asegúrate de que tu IP esté permitida en MongoDB Atlas

### Error: "Port 8115 already in use"
- Cambia el puerto en `application.properties`: `server.port=XXXX`

### Error en Render: "Build failed"
- Verifica que Java 21 esté configurado
- Revisa los logs de build en el dashboard de Render

## 📞 Soporte

Para reportar problemas o solicitar características:
- Crea un issue en GitHub
- Contacta al equipo de desarrollo

## 📄 Licencia

Este proyecto es de uso académico para la materia de Desarrollo de Aplicaciones Empresariales.

## 👨‍💻 Autor

Desarrollado por [Tu Nombre] - [Tu Universidad]

---

**Versión:** 0.0.1-SNAPSHOT  
**Fecha:** Noviembre 2025
