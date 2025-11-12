# 🚀 Sprint 2 - Guía de Uso Rápida

## ✅ Estado del Sistema

- **Aplicación**: ✅ Corriendo en http://localhost:8115
- **Base de Datos**: ✅ MongoDB Atlas conectado
- **Admin**: ✅ Usuario admin@expogest.com / admin123

---

## 📱 Acceso Rápido

### Gestión de Eventos
```
Lista:    http://localhost:8115/eventos
Nuevo:    http://localhost:8115/eventos/nuevo
```

### Gestión de Stands
```
Lista:    http://localhost:8115/stands
Nuevo:    http://localhost:8115/stands/nuevo
```

---

## 🧪 Flujo de Prueba Completo

### 1️⃣ Crear un Evento

**Navegación**: http://localhost:8115/eventos → "Nuevo Evento"

**Datos de ejemplo**:
```
Nombre:                ExpoTech 2025
Lugar:                 Centro de Convenciones Nacional
Fecha Inicio:          2025-12-01
Fecha Fin:             2025-12-05
Descripción:           Feria de tecnología e innovación
Capacidad Máx Stands:  10
Estado:                PLANIFICACION
```

---

### 2️⃣ Crear Varios Stands

**Navegación**: http://localhost:8115/stands → "Nuevo Stand"

**Stand 1**:
```
Número:      ST-001
Nombre:      Stand Premium A
Ubicación:   Pabellón 1 - Sección Norte
Dimensiones: 6x6 metros
Precio:      5000
Estado:      DISPONIBLE
```

**Stand 2**:
```
Número:      ST-002
Nombre:      Stand Estándar B
Ubicación:   Pabellón 1 - Sección Sur
Dimensiones: 3x3 metros
Precio:      2000
Estado:      DISPONIBLE
```

**Stand 3**:
```
Número:      ST-003
Nombre:      Stand VIP
Ubicación:   Entrada Principal
Dimensiones: 10x10 metros
Precio:      15000
Estado:      DISPONIBLE
```

---

### 3️⃣ Asociar Stands al Evento

**Navegación**: 
1. Ir a http://localhost:8115/eventos
2. Click en botón "Ver" (🗃️) en la columna "Stands" del evento ExpoTech 2025
3. Verás la gestión de stands del evento

**Resultado esperado**:
```
✅ Card superior: Información del evento (Estado, Capacidad)
✅ Tabla 1: Stands Asociados (vacía inicialmente)
✅ Tabla 2: Stands Disponibles (ST-001, ST-002, ST-003)
```

**Acción**: Click en "Asociar" para cada stand

**Efecto**:
- Stand pasa de tabla "Disponibles" a "Asociados"
- Estado cambia: DISPONIBLE → RESERVADO
- Contador aumenta: 1/10, 2/10, 3/10...
- Badge de capacidad: "Disponible" (verde)

---

### 4️⃣ Validar Capacidad Máxima

**Prueba**:
1. Crear 10 stands adicionales
2. Asociarlos hasta llegar a 10/10
3. Intentar asociar el stand 11

**Resultado esperado**:
```
❌ Alerta roja: "El evento ha alcanzado su capacidad máxima de stands"
✅ Stand no se asocia
✅ Badge cambia a "Completo" (rojo)
```

---

### 5️⃣ Desasociar un Stand

**Prueba**:
1. En gestión de stands del evento
2. Click "Desasociar" en un stand
3. Confirmar acción

**Resultado esperado**:
```
✅ Stand vuelve a tabla "Disponibles"
✅ Estado: RESERVADO → DISPONIBLE
✅ Contador: 10/10 → 9/10
✅ eventoId: "673e..." → null
```

---

### 6️⃣ Validar Eliminación con Stands

**Prueba 1**: Con stands asociados
1. Ir a http://localhost:8115/eventos
2. Click "Eliminar" en evento con stands
3. Resultado: ❌ "No se puede eliminar un evento con stands asociados"

**Prueba 2**: Sin stands
1. Desasociar todos los stands del evento
2. Intentar eliminar de nuevo
3. Resultado: ✅ Evento eliminado

---

## 🌐 Pruebas de API REST

### Crear Evento (POST)
```bash
curl -X POST http://localhost:8115/api/eventos \
-H "Content-Type: application/json" \
-d '{
  "nombre": "ExpoAgro 2025",
  "lugar": "Recinto Ferial",
  "fechaInicio": "2025-06-15",
  "fechaFin": "2025-06-20",
  "descripcion": "Feria agropecuaria",
  "capacidadMaximaStands": 20,
  "estado": "PLANIFICACION"
}'
```

**Respuesta esperada**:
```json
{
  "id": "673e4a2f8b9c1234567890ab",
  "nombre": "ExpoAgro 2025",
  "lugar": "Recinto Ferial",
  "fechaInicio": "2025-06-15",
  "fechaFin": "2025-06-20",
  "descripcion": "Feria agropecuaria",
  "estado": "PLANIFICACION",
  "capacidadMaximaStands": 20,
  "standsAsociados": []
}
```

---

### Listar Eventos Activos (GET)
```bash
curl http://localhost:8115/api/eventos/activos
```

---

### Crear Stand (POST)
```bash
curl -X POST http://localhost:8115/api/stands \
-H "Content-Type: application/json" \
-d '{
  "numero": "ST-100",
  "nombre": "Stand Corporativo",
  "ubicacion": "Pabellón Central",
  "dimensiones": "5x5 metros",
  "precio": 3500,
  "estado": "DISPONIBLE"
}'
```

---

### Asociar Stand a Evento (POST)
```bash
# Reemplazar {eventoId} y {standId} con IDs reales
curl -X POST http://localhost:8115/api/eventos/{eventoId}/stands/{standId}
```

**Respuesta exitosa**:
```json
{
  "mensaje": "Stand asociado exitosamente"
}
```

**Respuesta de error (capacidad)**:
```json
{
  "error": "El evento ha alcanzado su capacidad máxima de stands"
}
```

---

### Listar Stands Disponibles (GET)
```bash
curl http://localhost:8115/api/stands/disponibles
```

---

### Buscar Stand por Número (GET)
```bash
curl http://localhost:8115/api/stands/numero/ST-001
```

---

## 📊 Visualización de Datos

### Dashboard de Stands
**URL**: http://localhost:8115/stands

**Características**:
- ✅ Tabla con todos los stands
- ✅ Estados con colores:
  - 🟢 Verde: DISPONIBLE
  - 🟡 Amarillo: RESERVADO
  - 🔵 Azul: OCUPADO
  - ⚫ Gris: MANTENIMIENTO
- ✅ Cards de estadísticas (contadores por estado)
- ✅ Información de evento y expositor asignados

---

### Gestión de Evento Individual
**URL**: http://localhost:8115/eventos/{id}/stands

**Componentes**:

**1. Card de Información**
```
📅 Nombre del Evento
📍 Lugar
⚡ Estado (badge con color)
🗃️ Stands: 5/10
✅ Capacidad: Disponible
```

**2. Tabla de Stands Asociados**
- Número, Nombre, Ubicación
- Dimensiones, Precio
- Estado actual
- Expositor asignado
- Botón "Desasociar"

**3. Tabla de Stands Disponibles**
- Solo aparece si hay capacidad
- Botón "Asociar" por cada stand
- Desaparece cuando evento está lleno

---

## 🎯 Casos de Uso Avanzados

### Escenario 1: Cambiar Estado de Evento
```
1. Evento en PLANIFICACION
2. Asociar todos los stands necesarios
3. Editar evento → cambiar a ACTIVO
4. Resultado: Evento listo para comenzar
```

### Escenario 2: Stand en Mantenimiento
```
1. Stand ocupado con expositor
2. Editar stand → cambiar a MANTENIMIENTO
3. No aparecerá en lista de disponibles
4. Volver a DISPONIBLE cuando se repare
```

### Escenario 3: Reasignar Stand
```
1. Desasociar stand de evento A
2. Stand vuelve a DISPONIBLE
3. Asociar a evento B
4. Stand ahora pertenece a evento B (RESERVADO)
```

---

## 🔍 Verificaciones en MongoDB

**Conectar a MongoDB Atlas**:
```
mongodb+srv://juandavidduranmalaver_db_user:Uts2025.@serverjuan.mdl6ggs.mongodb.net/expogest
```

**Queries útiles**:

```javascript
// Ver todos los eventos
db.eventos.find({})

// Ver evento específico con sus stands
db.eventos.findOne({nombre: "ExpoTech 2025"})

// Ver stands de un evento
db.stands.find({eventoId: "673e..."})

// Contar stands por estado
db.stands.aggregate([
  { $group: { _id: "$estado", count: { $sum: 1 } } }
])
```

---

## ⚠️ Problemas Comunes

### Error: "Ya existe un stand con el número X"
- **Causa**: Número de stand duplicado
- **Solución**: Usar número diferente (ej: ST-101 en vez de ST-001)

### Error: "No se puede eliminar evento con stands"
- **Causa**: Hay stands asociados
- **Solución**: Desasociar todos los stands primero

### Error: "El stand no está disponible"
- **Causa**: Stand en estado RESERVADO, OCUPADO o MANTENIMIENTO
- **Solución**: Liberar el stand o cambiar estado a DISPONIBLE

### No aparece tabla "Asociar Nuevo Stand"
- **Causa**: Evento ha llegado a capacidad máxima
- **Solución**: Aumentar capacidad del evento o desasociar algún stand

---

## 📝 Checklist de Funcionalidades

### Eventos
- [x] Crear evento con todos los campos
- [x] Editar evento existente
- [x] Eliminar evento (solo sin stands)
- [x] Ver lista de eventos
- [x] Ver stands del evento
- [x] Asociar stands al evento
- [x] Desasociar stands del evento
- [x] Validar capacidad máxima
- [x] Validar fechas (inicio < fin)

### Stands
- [x] Crear stand con todos los campos
- [x] Editar stand existente
- [x] Eliminar stand (solo si no asociado)
- [x] Ver lista de stands
- [x] Ver stands disponibles
- [x] Ver stands por evento
- [x] Cambiar estado de stand
- [x] Liberar stand ocupado
- [x] Validar número único

### UI/UX
- [x] Mensajes de éxito/error
- [x] Badges de estado con colores
- [x] Estadísticas en tiempo real
- [x] Formularios con validación
- [x] Confirmaciones de eliminación
- [x] Responsive design (Bootstrap 5)

---

## 🎓 Próximas Mejoras

1. **Filtros**: Buscar eventos por fecha o estado
2. **Ordenamiento**: Ordenar stands por precio o número
3. **Reportes**: Generar PDF con lista de stands
4. **Notificaciones**: Email cuando stand es asignado
5. **Historial**: Ver cambios de estado de stands
6. **Dashboard**: Gráficos de ocupación y estadísticas

---

**¡Sprint 2 Completado y Funcionando! 🎉**
