# API REST - ExpoGest - Sprint 1
## Documentación de Endpoints

### Base URL
```
http://localhost:8115/api
```

---

## 1. AUTENTICACIÓN Y GESTIÓN DE USUARIOS

### 1.1 Registro de Usuario
**POST** `/usuarios/registro`

**Body (JSON):**
```json
{
  "nombre": "Juan Pérez",
  "correo": "juan@example.com",
  "contrasena": "123456",
  "rol": "VISITANTE"
}
```

**Roles disponibles:**
- ADMINISTRADOR
- ORGANIZADOR
- EXPOSITOR
- VISITANTE
- EVALUADOR

**Respuesta exitosa:**
```json
{
  "mensaje": "Usuario registrado correctamente",
  "usuario": {
    "id": "674a1234567890abcdef",
    "nombre": "Juan Pérez",
    "correo": "juan@example.com",
    "rol": "VISITANTE"
  }
}
```

---

### 1.2 Login de Usuario
**POST** `/usuarios/login`

**Body (JSON):**
```json
{
  "correo": "juan@example.com",
  "contrasena": "123456"
}
```

**Respuesta exitosa:**
```json
{
  "mensaje": "Login exitoso",
  "usuario": {
    "id": "674a1234567890abcdef",
    "nombre": "Juan Pérez",
    "correo": "juan@example.com",
    "rol": "VISITANTE"
  },
  "rol": "VISITANTE"
}
```

---

### 1.3 Obtener Perfil de Usuario
**GET** `/usuarios/{id}`

**Respuesta:**
```json
{
  "id": "674a1234567890abcdef",
  "nombre": "Juan Pérez",
  "correo": "juan@example.com",
  "rol": "VISITANTE"
}
```

---

### 1.4 Actualizar Perfil
**PUT** `/usuarios/{id}`

**Body (JSON):**
```json
{
  "nombre": "Juan Pérez Actualizado",
  "correo": "juan.nuevo@example.com",
  "contrasena": "nuevapass",
  "rol": "VISITANTE"
}
```

---

## 2. CRUD DE USUARIOS (Solo Admin)

### 2.1 Listar Todos los Usuarios
**GET** `/admin/usuarios`

**Respuesta:**
```json
[
  {
    "id": "674a1234567890abcdef",
    "nombre": "Juan Pérez",
    "correo": "juan@example.com",
    "rol": "VISITANTE"
  },
  {
    "id": "674a9876543210fedcba",
    "nombre": "María García",
    "correo": "maria@example.com",
    "rol": "ADMINISTRADOR"
  }
]
```

---

### 2.2 Crear Usuario (Admin)
**POST** `/admin/usuarios`

**Body (JSON):**
```json
{
  "nombre": "Nuevo Usuario",
  "correo": "nuevo@example.com",
  "contrasena": "password123",
  "rol": "ORGANIZADOR"
}
```

---

### 2.3 Obtener Usuario por ID
**GET** `/admin/usuarios/{id}`

---

### 2.4 Editar Usuario
**PUT** `/admin/usuarios/{id}`

**Body (JSON):**
```json
{
  "nombre": "Usuario Editado",
  "correo": "editado@example.com",
  "contrasena": "newpass",
  "rol": "EXPOSITOR"
}
```

---

### 2.5 Eliminar Usuario
**DELETE** `/admin/usuarios/{id}`

**Respuesta:**
```json
{
  "mensaje": "Usuario eliminado correctamente"
}
```

---

### 2.6 Filtrar Usuarios por Rol
**GET** `/admin/usuarios/rol/{rol}`

Ejemplo: `/admin/usuarios/rol/VISITANTE`

---

## 3. GESTIÓN DE EVENTOS

### 3.1 Listar Eventos
**GET** `/eventos`

---

### 3.2 Obtener Evento por ID
**GET** `/eventos/{id}`

---

### 3.3 Crear Evento
**POST** `/eventos`

**Body (JSON):**
```json
{
  "nombre": "ExpoTech 2025",
  "fechas": "15-20 de Noviembre 2025",
  "lugar": "Centro de Convenciones"
}
```

---

### 3.4 Editar Evento
**PUT** `/eventos/{id}`

---

### 3.5 Eliminar Evento
**DELETE** `/eventos/{id}`

---

## 4. GESTIÓN DE STANDS

### 4.1 Listar Stands
**GET** `/stands`

---

### 4.2 Obtener Stand por ID
**GET** `/stands/{id}`

---

### 4.3 Crear Stand
**POST** `/stands`

**Body (JSON):**
```json
{
  "numero": "A-101",
  "nombre": "Stand Principal",
  "ubicacion": "Pabellón A",
  "dimensiones": "3x3 metros"
}
```

---

### 4.4 Editar Stand
**PUT** `/stands/{id}`

---

### 4.5 Eliminar Stand
**DELETE** `/stands/{id}`

---

### 4.6 Listar Stands Disponibles
**GET** `/stands/disponibles`

---

## 5. GESTIÓN DE CRONOGRAMA

### 5.1 Listar Cronogramas
**GET** `/cronogramas`

---

### 5.2 Obtener Cronograma por ID
**GET** `/cronogramas/{id}`

---

### 5.3 Crear Actividad
**POST** `/cronogramas`

**Body (JSON):**
```json
{
  "actividad": "Conferencia inaugural",
  "fecha": "2025-11-15",
  "hora": "10:00:00",
  "eventoId": "674a1234567890abcdef"
}
```

---

### 5.4 Editar Actividad
**PUT** `/cronogramas/{id}`

---

### 5.5 Eliminar Actividad
**DELETE** `/cronogramas/{id}`

---

### 5.6 Listar Actividades por Evento
**GET** `/cronogramas/evento/{eventoId}`

---

## 6. PANEL DEL ORGANIZADOR

### 6.1 Dashboard
**GET** `/organizador/dashboard`

**Respuesta:**
```json
{
  "totalEventos": 5,
  "totalStands": 20,
  "totalActividades": 15
}
```

---

### 6.2 Mis Eventos
**GET** `/organizador/eventos`

---

### 6.3 Mis Stands
**GET** `/organizador/stands`

---

### 6.4 Mis Cronogramas
**GET** `/organizador/cronogramas`

---

## VALIDACIONES IMPLEMENTADAS

### Usuario
- ✅ Correo único (no se permiten correos duplicados)
- ✅ Rol obligatorio antes de guardar
- ✅ Validación de rol válido

### Evento
- ✅ Nombre obligatorio
- ✅ Fechas obligatorias
- ✅ Lugar obligatorio
- ✅ Validación antes de eliminar (no se puede eliminar con expositores activos)

### Stand
- ✅ Número obligatorio
- ✅ Ubicación obligatoria
- ✅ Validación de disponibilidad

### Cronograma
- ✅ Actividad/descripción obligatoria
- ✅ Fecha obligatoria
- ✅ Hora obligatoria
- ✅ No permitir horarios duplicados en el mismo evento

---

## PRUEBAS CON POSTMAN O THUNDER CLIENT

### Paso 1: Registrar un usuario administrador
```
POST http://localhost:8115/api/usuarios/registro
Body:
{
  "nombre": "Admin",
  "correo": "admin@expogest.com",
  "contrasena": "admin123",
  "rol": "ADMINISTRADOR"
}
```

### Paso 2: Login
```
POST http://localhost:8115/api/usuarios/login
Body:
{
  "correo": "admin@expogest.com",
  "contrasena": "admin123"
}
```

### Paso 3: Crear un evento
```
POST http://localhost:8115/api/eventos
Body:
{
  "nombre": "ExpoTech 2025",
  "fechas": "15-20 de Noviembre 2025",
  "lugar": "Centro de Convenciones"
}
```

### Paso 4: Crear un stand
```
POST http://localhost:8115/api/stands
Body:
{
  "numero": "A-101",
  "nombre": "Stand Principal",
  "ubicacion": "Pabellón A",
  "dimensiones": "3x3 metros"
}
```

---

## PRÓXIMOS PASOS (Sprints Futuros)

1. Implementar autenticación JWT real
2. Agregar middleware de autorización por roles
3. Implementar solicitudes de stands por expositores
4. Sistema de evaluaciones
5. Reportes y estadísticas
6. Notificaciones y confirmaciones

---

## NOTAS TÉCNICAS

- Base de datos: MongoDB
- Puerto: 8115
- Todas las respuestas están en formato JSON
- IDs de MongoDB son de tipo String
- Las contraseñas se guardan en texto plano (implementar hash en producción)
