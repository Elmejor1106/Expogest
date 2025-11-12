# 📋 Sprint 2 - Módulo de Gestión de Eventos y Stands

## ✅ Implementación Completada

### 🎯 Objetivos Cumplidos

#### 1. **CRUD de Eventos**
- ✅ Creación, edición, visualización y eliminación de eventos
- ✅ Campos mejorados:
  - `fechaInicio` y `fechaFin` (tipo LocalDate)
  - `descripcion` (texto largo)
  - `estado` (enum: PLANIFICACION, ACTIVO, FINALIZADO, CANCELADO)
  - `capacidadMaximaStands` (límite de stands por evento)
  - `standsAsociados` (lista de IDs)

#### 2. **CRUD de Stands**
- ✅ Gestión completa de stands
- ✅ Campos implementados:
  - `numero` (identificador único)
  - `nombre`, `ubicacion`, `dimensiones`
  - `precio` (valor monetario)
  - `estado` (enum: DISPONIBLE, RESERVADO, OCUPADO, MANTENIMIENTO)
  - `eventoId` (relación con evento)
  - `expositorId` (relación con expositor)

#### 3. **Asociación Evento-Stand**
- ✅ Asociar stands a eventos
- ✅ Desasociar stands de eventos
- ✅ Visualización de stands por evento
- ✅ Validación de capacidad máxima

#### 4. **Validaciones Implementadas**
- ✅ No eliminar eventos con stands asociados
- ✅ No eliminar stands asociados a eventos
- ✅ Validar capacidad máxima de stands por evento
- ✅ Validar disponibilidad de stand antes de asociar
- ✅ Validar fechas de evento (inicio < fin)
- ✅ Validar número único de stand

---

## 📁 Estructura de Archivos Creados/Modificados

### Backend

#### Entidades (Actualizadas)
- ✅ `Evento.java` - Con enum EstadoEvento, LocalDate, capacidad
- ✅ `Stand.java` - Con enum EstadoStand, relaciones, precio

#### Servicios (Nuevos)
- ✅ `EventoService.java` - Lógica de negocio para eventos
- ✅ `StandService.java` - Lógica de negocio para stands

#### Repositorios (Actualizados)
- ✅ `EventoRepository.java` - Query methods para estado
- ✅ `StandRepository.java` - Query methods para búsquedas

#### Controladores Web (Actualizados)
- ✅ `EventoController.java` - Endpoints Thymeleaf con servicios
- ✅ `StandController.java` - Endpoints Thymeleaf mejorados

#### Controladores REST (Nuevos)
- ✅ `EventoRestController.java` - API REST completa
- ✅ `StandRestController.java` - API REST completa

### Frontend

#### Vistas de Eventos
- ✅ `templates/evento/form.html` - Formulario mejorado con todos los campos
- ✅ `templates/evento/lista.html` - Lista con estados, badges, capacidad
- ✅ `templates/evento/stands.html` - **NUEVA** - Gestión de stands por evento

#### Vistas de Stands
- ✅ `templates/organizador/stands.html` - Lista mejorada con estados y estadísticas
- ✅ `templates/organizador/nuevoStand.html` - Formulario completo con todos los campos

---

## 🔗 Endpoints Disponibles

### API REST

#### Eventos
```
GET    /api/eventos                    - Listar todos los eventos
GET    /api/eventos/{id}               - Obtener un evento
GET    /api/eventos/activos            - Listar eventos activos
POST   /api/eventos                    - Crear evento
PUT    /api/eventos/{id}               - Actualizar evento
DELETE /api/eventos/{id}               - Eliminar evento
GET    /api/eventos/{id}/stands        - Obtener stands del evento
POST   /api/eventos/{eventoId}/stands/{standId}  - Asociar stand
DELETE /api/eventos/{eventoId}/stands/{standId}  - Desasociar stand
```

#### Stands
```
GET    /api/stands                     - Listar todos los stands
GET    /api/stands/{id}                - Obtener un stand
GET    /api/stands/numero/{numero}     - Buscar por número
GET    /api/stands/disponibles         - Listar disponibles
GET    /api/stands/evento/{eventoId}   - Stands de un evento
POST   /api/stands                     - Crear stand
PUT    /api/stands/{id}                - Actualizar stand
DELETE /api/stands/{id}                - Eliminar stand
POST   /api/stands/{standId}/asignar-expositor/{expositorId}  - Asignar expositor
POST   /api/stands/{standId}/liberar   - Liberar stand
```

### Web (Thymeleaf)

#### Eventos
```
GET  /eventos                           - Lista de eventos
GET  /eventos/nuevo                     - Formulario nuevo evento
POST /eventos/guardar                   - Guardar evento
GET  /eventos/editar/{id}               - Editar evento
GET  /eventos/eliminar/{id}             - Eliminar evento
GET  /eventos/{id}/stands               - Gestión de stands del evento
POST /eventos/{eventoId}/stands/{standId}/asociar    - Asociar stand
POST /eventos/{eventoId}/stands/{standId}/desasociar - Desasociar stand
```

#### Stands
```
GET  /stands                            - Lista de stands
GET  /stands/nuevo                      - Formulario nuevo stand
POST /stands/guardar                    - Guardar stand
GET  /stands/editar/{id}                - Editar stand
GET  /stands/eliminar/{id}              - Eliminar stand
GET  /stands/disponibles                - Lista de stands disponibles
POST /stands/{standId}/asignar-expositor/{expositorId}  - Asignar expositor
POST /stands/{standId}/liberar          - Liberar stand
```

---

## 🎨 Características de la UI

### Lista de Eventos
- **Badges de Estado**: Colores según estado (Planificación, Activo, Finalizado, Cancelado)
- **Contador de Stands**: Muestra cantidad asociada vs capacidad máxima
- **Botón Ver Stands**: Acceso directo a gestión de stands
- **Mensajes Flash**: Alertas de éxito/error con Bootstrap

### Gestión de Stands por Evento
- **Card de Información**: Resumen del evento con estado y capacidad
- **Tabla de Stands Asociados**: Con estados, expositor, acciones
- **Tabla de Disponibles**: Botón para asociar rápidamente
- **Validación Visual**: Badge de capacidad disponible/completo

### Lista de Stands
- **Estados con Colores**: Verde (Disponible), Amarillo (Reservado), Azul (Ocupado), Gris (Mantenimiento)
- **Información Completa**: Número, nombre, ubicación, dimensiones, precio
- **Relaciones Visibles**: Evento y expositor asignados
- **Estadísticas Dashboard**: Cards con contadores por estado
- **Botón Liberar**: Para stands ocupados

### Formularios
- **Validación HTML5**: Campos requeridos marcados con *
- **Inputs Especializados**: Date picker, number, textarea
- **Tooltips Informativos**: Ayuda contextual
- **Selectores de Estado**: Dropdown con todos los estados

---

## 🔐 Validaciones de Negocio

### Eventos
1. ✅ Fecha inicio < Fecha fin
2. ✅ No eliminar si tiene stands asociados
3. ✅ Validar capacidad máxima al asociar stands
4. ✅ Campos obligatorios: nombre, lugar, fechas

### Stands
1. ✅ Número único (constraint en BD)
2. ✅ No eliminar si está asociado a evento
3. ✅ Solo asociar stands DISPONIBLES
4. ✅ Estado correcto al asociar (DISPONIBLE → RESERVADO)
5. ✅ Estado correcto al asignar expositor (→ OCUPADO)

### Asociaciones
1. ✅ Verificar capacidad disponible del evento
2. ✅ Verificar estado del stand
3. ✅ Actualizar contador de stands del evento
4. ✅ Cambiar estado del stand automáticamente
5. ✅ Al desasociar: liberar stand (DISPONIBLE)

---

## 📊 Enumeraciones

### EstadoEvento
```java
PLANIFICACION  - Evento en preparación
ACTIVO         - Evento en curso
FINALIZADO     - Evento terminado
CANCELADO      - Evento cancelado
```

### EstadoStand
```java
DISPONIBLE     - Listo para asociar
RESERVADO      - Asociado a evento, sin expositor
OCUPADO        - Con expositor asignado
MANTENIMIENTO  - No disponible temporalmente
```

---

## 🧪 Cómo Probar

### 1. Crear un Evento
```
1. Ir a http://localhost:8115/eventos
2. Click "Nuevo Evento"
3. Llenar: nombre, lugar, fechas, descripción, capacidad (ej: 5 stands)
4. Estado: PLANIFICACION
5. Guardar
```

### 2. Crear Stands
```
1. Ir a http://localhost:8115/stands
2. Click "Nuevo Stand"
3. Llenar: número (ST-001), nombre, ubicación, dimensiones, precio
4. Estado: DISPONIBLE
5. Repetir para crear varios stands
```

### 3. Asociar Stands a Evento
```
1. En lista de eventos, click "Ver" en columna Stands
2. Ver stands asociados (vacío inicialmente)
3. En tabla "Asociar Nuevo Stand", click "Asociar"
4. Verificar que aparece en lista de asociados
5. Ver que estado cambió a RESERVADO
6. Ver que contador aumentó (1/5)
```

### 4. Validar Capacidad
```
1. Asociar stands hasta llegar a capacidad máxima
2. Intentar asociar uno más
3. Debe mostrar mensaje de error: "capacidad máxima alcanzada"
```

### 5. Desasociar Stand
```
1. En gestión de stands del evento
2. Click "Desasociar" en un stand
3. Verificar que se libera (estado DISPONIBLE)
4. Contador disminuye
```

### 6. Validar Eliminación
```
1. Intentar eliminar evento con stands → Error
2. Desasociar todos los stands
3. Ahora sí se puede eliminar el evento
```

---

## 📝 Métodos de Utilidad

### En Evento.java
```java
getCantidadStandsAsociados()    - Retorna cantidad de stands
tieneCapacidadDisponible()      - Verifica si acepta más stands
```

### En Stand.java
```java
estaDisponible()                - Verifica si estado es DISPONIBLE
estaOcupado()                   - Verifica si OCUPADO o RESERVADO
```

---

## 🚀 Mejoras Implementadas

1. **Arquitectura en Capas**: Controller → Service → Repository
2. **Mensajes Flash**: RedirectAttributes para feedback al usuario
3. **Validaciones Robustas**: Try-catch con mensajes claros
4. **UI/UX Mejorado**: Bootstrap 5, iconos, colores semánticos
5. **API REST Completa**: Para posible integración con frontend moderno
6. **Estadísticas en Tiempo Real**: Dashboard de estados de stands
7. **Relaciones Bidireccionales**: Evento ↔ Stand

---

## 📦 Compilación Exitosa

```bash
mvnw.cmd clean compile
# BUILD SUCCESS
```

---

## 🎓 Próximos Pasos (Sprint 3)

- [ ] Módulo de solicitudes de stands por expositores
- [ ] Sistema de aprobación de solicitudes
- [ ] Historial de asignaciones
- [ ] Reportes y estadísticas
- [ ] Notificaciones por email
- [ ] Dashboard con gráficos

---

## 🛠️ Stack Tecnológico

- **Backend**: Spring Boot 3.5.7, Java 25
- **Frontend**: Thymeleaf, Bootstrap 5, Bootstrap Icons
- **Base de Datos**: MongoDB Atlas
- **Build Tool**: Maven
- **Estado**: ✅ COMPLETADO Y COMPILANDO

---

**Fecha de Implementación**: 12 de Noviembre de 2025  
**Desarrollador**: GitHub Copilot (Claude Sonnet 4.5)  
**Estado del Proyecto**: ✅ Sprint 2 Completado
