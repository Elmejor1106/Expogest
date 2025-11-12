# 📋 Análisis de Funcionalidades Faltantes

## ✅ IMPLEMENTADO (Sprint 2)

### Administrador
- ✅ **Crear y gestionar cuentas de usuario**
  - AdminInitializer crea usuario admin automáticamente
  - Validación de correos únicos
  - Validación de rol antes de guardar
  
- ✅ **Crear eventos empresariales** 
  - EventoController con CRUD completo
  - EventoService con validaciones
  - Campos: nombre, lugar, fechaInicio, fechaFin, descripcion, estado, capacidad

### Organizador
- ✅ **Crear y asignar stands dentro de un evento**
  - StandController con CRUD completo
  - StandService con validaciones
  - Asociación stands ↔ eventos
  - Validación de capacidad máxima
  - Validación de disponibilidad

---

## ❌ PENDIENTE DE IMPLEMENTAR

### 1. **Organizador: Gestión de Cronograma** 🔴 ALTA PRIORIDAD

**Historia de Usuario**: "mantener y modificar actividades del cronograma" para "mantener la programación del evento actualizada"

**Criterios de Aceptación**:
- ✅ Cada actividad debe incluir fecha, hora y descripción (ya existe entidad)
- ❌ **FALTA**: Solo organizadores pueden crear y modificar actividades
- ❌ **FALTA**: No se permiten horarios duplicados en un mismo evento
- ❌ **FALTA**: Controlador web funcional
- ❌ **FALTA**: Vistas HTML para CRUD de cronograma

**Estado Actual**:
```
✅ Entidad: Cronograma.java (completa)
✅ Repository: CronogramaRepository.java
✅ API REST: CronogramaRestController.java (básico)
❌ Controlador Web: NO funcional
❌ Servicio: NO existe
❌ Vistas HTML: Básicas sin funcionalidad completa
❌ Validaciones: NO implementadas
```

**Archivos que Necesitan Trabajo**:
- Crear: `CronogramaService.java` con validaciones
- Actualizar: `CronogramaController.java` (existe pero básico)
- Actualizar: `templates/organizador/cronograma.html`
- Actualizar: `templates/organizador/cronogramaForm.html`

---

### 2. **Expositor: Solicitud de Stand** 🔴 ALTA PRIORIDAD

**Historia de Usuario**: "solicitar un stand disponible" para "presentar mi empresa y productos en el evento"

**Criterios de Aceptación**:
- ❌ **FALTA**: No puede solicitar más de un stand por evento
- ❌ **FALTA**: El sistema valida disponibilidad del stand antes de asignar
- ❌ **FALTA**: Solo se puede solicitar si el evento está activo
- ❌ **FALTA**: Sistema de estados para solicitudes (PENDIENTE, APROBADA, RECHAZADA)

**Estado Actual**:
```
✅ Entidad: SolicitudStand.java (básica, falta estado)
✅ Repository: SolicitudStandRepository.java
⚠️ API REST: SolicitudStandRestController.java (existe)
⚠️ Controlador Web: SolicitudStandController.java (básico)
❌ Servicio: NO existe
❌ Validaciones: NO implementadas
❌ Estados: NO definidos
❌ Vistas: Básicas sin funcionalidad
```

**Necesita**:
- Agregar campo `estado` en SolicitudStand (enum)
- Agregar campo `eventoId` en SolicitudStand
- Agregar campo `fechaSolicitud` en SolicitudStand
- Crear `SolicitudStandService.java` con:
  - Validar 1 solicitud por evento
  - Validar evento activo
  - Validar disponibilidad de stand
  - Cambiar estado de solicitud
- Mejorar controlador web
- Crear vistas funcionales

---

### 3. **Expositor: Subir Material Multimedia** 🟡 MEDIA PRIORIDAD

**Historia de Usuario**: "Subir información comercial y material multimedia" para "mostrar mi oferta a los visitantes y evaluadores"

**Criterios de Aceptación**:
- ❌ **FALTA**: Se permiten archivos PDF, JPG o PNG
- ❌ **FALTA**: Solo se habilita carga tras confirmar stand
- ❌ **FALTA**: Se almacena información en MongoDB de forma segura

**Estado Actual**:
```
❌ Entidad: NO existe (MaterialExpositor)
❌ Repository: NO existe
❌ Service: NO existe
❌ Controlador: NO existe
❌ Vistas: NO existen
❌ Manejo de archivos: NO implementado
```

**Necesita Crear**:
- `MaterialExpositor.java` entity con:
  - id, expositorId, standId, eventoId
  - nombreArchivo, tipoArchivo, urlArchivo
  - descripcion, fechaSubida
- `MaterialExpositorRepository.java`
- `MaterialExpositorService.java` con:
  - Validar stand confirmado
  - Validar tipos de archivo
  - Guardar archivo (GridFS o ruta)
- Controlador web y vistas
- Configuración de subida de archivos

---

### 4. **Visitante: Registro en Evento** 🟢 BAJA PRIORIDAD

**Historia de Usuario**: "registrarme en un evento activo" para "acceder a las conferencias y actividades programadas"

**Criterios de Aceptación**:
- ❌ **FALTA**: Solo se permite una inscripción por evento
- ❌ **FALTA**: Se valida si el usuario ya está inscrito
- ❌ **FALTA**: Recibe confirmación de inscripción exitosa

**Estado Actual**:
```
⚠️ Entidad: Participacion.java (existe básica)
✅ Repository: ParticipacionRepository.java
❌ Servicio: NO existe
❌ Validaciones: NO implementadas
❌ Confirmación: NO implementada
```

**Necesita**:
- Agregar campos en Participacion:
  - estado (PENDIENTE, CONFIRMADA, CANCELADA)
  - fechaInscripcion
  - tipoParticipante (VISITANTE, EVALUADOR, etc)
- Crear `ParticipacionService.java` con:
  - Validar inscripción única por evento
  - Validar evento activo
  - Generar confirmación
- Mejorar vistas existentes

---

## 📊 Resumen de Prioridades

### 🔴 ALTA PRIORIDAD (Funcionalidad Core)
1. **Cronograma Completo** - Para organizadores
2. **Sistema de Solicitudes de Stand** - Para expositores

### 🟡 MEDIA PRIORIDAD (Funcionalidad Importante)
3. **Subida de Material Multimedia** - Para expositores

### 🟢 BAJA PRIORIDAD (Funcionalidad Nice-to-Have)
4. **Sistema de Inscripción Mejorado** - Para visitantes
5. **Sistema de Evaluación** - Para evaluadores (no mencionado en tabla pero existe en el código)

---

## 🎯 Plan de Implementación Sugerido

### Sprint 3: Solicitudes y Cronograma
1. **Semana 1**: Sistema de Solicitudes de Stand
   - Mejorar entidad SolicitudStand
   - Crear servicio con todas las validaciones
   - Implementar controlador y vistas
   - Sistema de aprobación/rechazo

2. **Semana 2**: Sistema de Cronograma Completo
   - Crear servicio con validaciones
   - Implementar gestión de actividades
   - Validar horarios duplicados
   - Vincular con eventos

### Sprint 4: Material y Inscripciones
3. **Semana 1**: Sistema de Material Multimedia
   - Configurar subida de archivos
   - Crear entidad y servicio
   - Implementar almacenamiento
   - Crear visualización

4. **Semana 2**: Mejorar Sistema de Inscripciones
   - Agregar validaciones
   - Sistema de confirmaciones
   - Reportes de participación

---

## 📝 Validaciones Críticas Faltantes

### Solicitudes de Stand
```java
// FALTA IMPLEMENTAR:
- validarEventoActivo(eventoId)
- validarStandDisponible(standId)
- validarSolicitudUnicaPorEvento(expositorId, eventoId)
- aprobarSolicitud(solicitudId) 
- rechazarSolicitud(solicitudId)
- notificarExpositor(solicitudId)
```

### Cronograma
```java
// FALTA IMPLEMENTAR:
- validarHorarioDisponible(eventoId, fecha, hora)
- validarActividadEnRangoEvento(eventoId, fecha)
- obtenerActividadesPorEvento(eventoId)
- validarNoSolapamiento(eventoId, fecha, horaInicio, horaFin)
```

### Material Multimedia
```java
// FALTA IMPLEMENTAR:
- validarStandConfirmado(expositorId)
- validarTipoArchivo(archivo)
- guardarArchivo(archivo, expositorId)
- obtenerMaterialPorExpositor(expositorId)
- eliminarMaterial(materialId)
```

---

## 🔧 Estado de Validaciones Actuales

### ✅ Ya Implementadas (Sprint 2)
- Validación de capacidad de evento
- Validación de disponibilidad de stand
- Validación de fechas de evento
- Validación de número único de stand
- Validación de correo único de usuario
- Validación de rol de usuario

### ❌ Pendientes
- Validación de horarios duplicados en cronograma
- Validación de solicitud única por evento
- Validación de stand confirmado para subir material
- Validación de inscripción única por evento
- Validación de evento activo para solicitudes
- Validación de tipos de archivo permitidos

---

## 💡 Recomendaciones

1. **Priorizar Solicitudes de Stand**: Es la funcionalidad más crítica para el flujo de negocio
2. **Implementar Estados**: Agregar enums para estados de solicitudes
3. **Crear Servicios**: Mover validaciones de controladores a servicios
4. **Agregar Notificaciones**: Sistema de alertas cuando se aprueba/rechaza solicitud
5. **Mejorar UI**: Dashboards para organizadores ver solicitudes pendientes
6. **Testing**: Agregar tests unitarios para nuevas validaciones

---

**Última Actualización**: 12 de Noviembre de 2025
**Estado Sprint Actual**: Sprint 2 Completado ✅
**Siguiente Sprint**: Sprint 3 - Solicitudes y Cronograma 📋
