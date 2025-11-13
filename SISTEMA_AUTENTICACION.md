# Sistema de Autenticación y Autorización

## 📋 Descripción General

Se ha implementado un **sistema completo de autenticación basado en sesiones HTTP** con control de acceso por roles. Este sistema previene que los usuarios accedan a rutas que no les corresponden según su rol.

## 🔐 Componentes Implementados

### 1. **AuthFilter.java**
Filtro de seguridad que intercepta todas las peticiones HTTP y valida:
- ✅ Si el usuario tiene una sesión activa
- ✅ Si el rol del usuario tiene permiso para acceder a la ruta solicitada
- ✅ Redirige a `/login` si no hay sesión
- ✅ Redirige a `/acceso-denegado` si no tiene permisos

### 2. **Rutas Públicas (Sin autenticación requerida)**
```
- /
- /login
- /registro
- /css/*
- /js/*
- /images/*
- /static/*
```

### 3. **Permisos por Rol**

#### 🔴 ADMINISTRADOR
- ✅ **Acceso total** a todas las rutas del sistema

#### 🟡 ORGANIZADOR
Rutas permitidas:
```
- /organizador/*
- /eventos/*
- /stands/*
- /cronogramas/*
- /solicitudes/* (excepto /mis-solicitudes y /nueva)
  - Puede aprobar/rechazar solicitudes
```

#### 🟢 EXPOSITOR
Rutas permitidas:
```
- /expositor/*
- /solicitudes/nueva/*
- /solicitudes/mis-solicitudes
- /solicitudes/guardar
- /solicitudes/{id}/cancelar
```

#### 🔵 EVALUADOR
Rutas permitidas:
```
- /evaluador/*
- /evaluaciones/*
```

#### 🟣 VISITANTE
Rutas permitidas:
```
- /visitante/*
- /participaciones/*
```

## 🔄 Flujo de Autenticación

### Login
```
1. Usuario ingresa correo y contraseña en /login
2. UsuarioController valida credenciales
3. Si es correcto:
   - Crea sesión HTTP con session.setAttribute("usuario", usuario)
   - Guarda también: usuarioId, usuarioRol
   - Redirige al panel correspondiente según el rol
4. Si es incorrecto:
   - Muestra mensaje de error
```

### Validación de Acceso
```
1. Usuario intenta acceder a una ruta protegida
2. AuthFilter intercepta la petición
3. Verifica si hay sesión activa
   - NO → Redirige a /login
4. Obtiene el rol del usuario de la sesión
5. Verifica si el rol tiene permiso para esa ruta
   - NO → Redirige a /acceso-denegado
   - SÍ → Permite continuar
```

### Logout
```
1. Usuario accede a /logout
2. UsuarioController invalida la sesión: session.invalidate()
3. Redirige a /login
```

## 🛡️ Seguridad Implementada

### 1. **Control de Sesiones**
- Las sesiones se crean solo en login exitoso
- Se almacena el objeto Usuario completo en la sesión
- Se almacena el ID y rol del usuario para acceso rápido

### 2. **Protección de Controladores**
Todos los controladores sensibles ahora usan `HttpSession`:

**ExpositorController.java**
```java
@GetMapping("/expositor/solicitudStand")
public String solicitudStand(HttpSession session, Model model) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) {
        return "redirect:/login";
    }
    model.addAttribute("solicitudes", 
        solicitudService.obtenerPorExpositor(usuario.getId()));
    return "expositor/solicitudStand";
}
```

**SolicitudStandController.java**
```java
@PostMapping("/guardar")
public String guardarSolicitud(@ModelAttribute SolicitudStand solicitud, 
                              HttpSession session, 
                              RedirectAttributes redirectAttributes) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) {
        return "redirect:/login";
    }
    solicitud.setExpositorId(usuario.getId());
    // ... resto del código
}
```

### 3. **Eliminación de IDs Hardcodeados**
✅ Antes:
```java
String expositorId = "expositor-test-id"; // ❌ No seguro
```

✅ Ahora:
```java
Usuario usuario = (Usuario) session.getAttribute("usuario");
String expositorId = usuario.getId(); // ✅ Seguro
```

## 🎨 Página de Acceso Denegado

Se creó `acceso-denegado.html` con:
- 🎨 Diseño consistente con el tema corporativo (amarillo/gris)
- 📱 Responsive
- 🔙 Botón "Volver" para regresar
- 🚪 Botón "Cerrar Sesión" para logout

## 📝 Registro de Cambios

### Archivos Creados
```
✅ AuthFilter.java - Filtro de autenticación y autorización
✅ acceso-denegado.html - Página de error de permisos
```

### Archivos Modificados
```
✅ SecrurityConfig.java - Registro del filtro de autenticación
✅ UsuarioController.java - Gestión de sesiones en login/logout
✅ ExpositorController.java - Uso de sesiones
✅ SolicitudStandController.java - Uso de sesiones en todos los métodos
```

## 🧪 Pruebas Recomendadas

### Test 1: Login y Acceso Correcto
1. Login como EXPOSITOR
2. Acceder a `/expositor/panelExpositor` → ✅ OK
3. Acceder a `/expositor/solicitudes/nueva` → ✅ OK

### Test 2: Acceso Denegado
1. Login como EXPOSITOR
2. Intentar acceder a `/eventos` (ruta de organizador) → ❌ Acceso Denegado
3. Intentar acceder a `/admin/panelAdmin` → ❌ Acceso Denegado

### Test 3: Sin Sesión
1. No hacer login
2. Intentar acceder a `/expositor/panelExpositor` → 🔀 Redirige a `/login`
3. Intentar acceder a `/organizador/panelOrganizador` → 🔀 Redirige a `/login`

### Test 4: Logout
1. Login como cualquier usuario
2. Acceder a `/logout` → ✅ Sesión cerrada
3. Intentar acceder a cualquier ruta protegida → 🔀 Redirige a `/login`

## ⚙️ Configuración

El filtro está configurado en `SecrurityConfig.java`:
```java
@Bean
public FilterRegistrationBean<AuthFilter> authFilter() {
    FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new AuthFilter());
    registrationBean.addUrlPatterns("/*"); // Aplica a todas las rutas
    registrationBean.setOrder(1); // Primera prioridad
    return registrationBean;
}
```

## 🔧 Mantenimiento

### Agregar Nuevas Rutas Protegidas
Editar `AuthFilter.java`, método `tienePermiso()`:
```java
// NUEVO_ROL
if ("NUEVO_ROL".equals(rol)) {
    return uri.startsWith("/nuevo-rol") ||
           uri.startsWith("/nueva-ruta");
}
```

### Agregar Rutas Públicas
Editar `AuthFilter.java`, método `isPublicRoute()`:
```java
return uri.equals("/") || 
       uri.equals("/login") || 
       uri.equals("/nueva-ruta-publica") || // Nueva ruta
       uri.startsWith("/css");
```

## 📊 Commit
```
Commit: 761f2fb
Mensaje: "feat: Implementar sistema de autenticación basado en sesiones con control de acceso por roles"
Archivos modificados: 6
Líneas agregadas: 247
Líneas eliminadas: 25
```

---

## ✅ Estado Final

🟢 **Sistema completamente funcional**
- Login con gestión de sesiones ✅
- Control de acceso por roles ✅  
- Protección de rutas backend ✅
- IDs de usuario desde sesión ✅
- Página de acceso denegado ✅
- Logout con invalidación de sesión ✅

🔒 **Seguridad mejorada**
- No más IDs hardcodeados ✅
- Validación en cada petición ✅
- Separación real de roles ✅
