# 🏢 Sistema de Material Comercial por Stand y Evento

## 📋 Flujo Implementado

### Relación de Entidades

```
EVENTO → tiene múltiples → STANDS → asignado a → EXPOSITOR (mediante SolicitudStand APROBADA)
                                                       ↓
                                            MATERIAL COMERCIAL (vinculado al Stand)
```

---

## 🔄 Proceso Completo

### 1. **Expositor Solicita Stand**
- El expositor solicita un stand específico en un evento
- Estado inicial: `PENDIENTE`

### 2. **Organizador Aprueba Solicitud**
- El organizador revisa y aprueba la solicitud
- Estado: `PENDIENTE` → `APROBADA`
- El stand queda asignado al expositor

### 3. **Expositor Gestiona Material Comercial**
- El sistema **automáticamente** detecta el stand asignado
- El expositor sube/configura su material comercial
- El material queda vinculado al stand específico

### 4. **Visitantes Ven Material por Evento**
- Los visitantes navegan eventos
- Ven la lista de stands del evento
- Pueden acceder al material comercial de cada stand

---

## 🎯 Cambios Implementados

### 1. **ExpositorController - Asignación Automática de Stand**

#### Método `verMaterialComercial()`:
```java
// Obtiene solicitudes aprobadas del expositor
List<SolicitudStand> solicitudesAprobadas = solicitudService.obtenerPorExpositor(expositorId)
    .stream()
    .filter(SolicitudStand::estaAprobada)
    .toList();

// Muestra información del stand actual
if (material.getStandId() != null) {
    standService.obtenerPorId(material.getStandId()).ifPresent(stand -> {
        model.addAttribute("standActual", stand);
        eventoService.obtenerPorId(stand.getEventoId()).ifPresent(evento -> 
            model.addAttribute("eventoActual", evento)
        );
    });
}
```

#### Método `guardarMaterialComercial()`:
```java
// Valida que tenga solicitud aprobada
if (solicitudesAprobadas.isEmpty()) {
    return "redirect:/expositor/solicitudStand";
}

// Asigna automáticamente el standId si no existe
if (material.getStandId() == null || material.getStandId().isEmpty()) {
    material.setStandId(solicitudesAprobadas.get(0).getStandId());
}

// Valida que el stand pertenece al expositor
boolean standValido = solicitudesAprobadas.stream()
    .anyMatch(sol -> sol.getStandId().equals(standSeleccionado));
```

**Validaciones**:
- ✅ El expositor debe tener al menos una solicitud APROBADA
- ✅ El stand seleccionado debe pertenecer al expositor
- ✅ Se obtiene automáticamente el evento del stand

---

### 2. **EventoController - Vista Pública de Stands**

#### Método `verStandsDelEvento()`:
```java
// Diferencia entre organizador y visitantes
if ("ORGANIZADOR".equals(rol)) {
    // Vista de gestión de stands
    return "evento/stands";
}

// Para visitantes: vista pública con material comercial
List<Stand> stands = eventoService.obtenerStandsDelEvento(id);

// Obtiene material comercial de cada stand
Map<String, MaterialComercial> materialesPorStand = new HashMap<>();
for (Stand stand : stands) {
    materialService.obtenerPorStand(stand.getId()).ifPresent(material -> 
        materialesPorStand.put(stand.getId(), material)
    );
}

return "evento/stands-publico";
```

**Características**:
- Organizadores: Ven vista de gestión de stands
- Visitantes/Evaluadores: Ven stands con material comercial
- Se carga el material comercial de cada stand automáticamente

---

### 3. **Vistas Actualizadas**

#### `expositor/materialComercial.html`

**Nuevo Header Informativo**:
```html
<!-- Muestra información del stand asignado -->
<div th:if="${standActual != null && eventoActual != null}">
    <div class="alert alert-info">
        <h5><i class="bi bi-info-circle"></i>Stand Asignado</h5>
        <p>Evento: <span th:text="${eventoActual.nombre}"></span></p>
        <p>Stand: <span th:text="${standActual.numero}"></span></p>
        <p>Ubicación: <span th:text="${standActual.ubicacion}"></span></p>
    </div>
</div>

<!-- Advertencia si no tiene stand -->
<div th:if="${solicitudesAprobadas == null || solicitudesAprobadas.isEmpty()}">
    <div class="alert alert-warning">
        <strong>No tienes un stand asignado.</strong>
        <a th:href="@{/expositor/solicitudes/nueva}">Solicitar Stand</a>
    </div>
</div>
```

**Campo StandId**:
- Ahora es `hidden` (oculto)
- Se asigna automáticamente en el backend
- Ya no es editable por el usuario

---

#### `evento/stands-publico.html` (NUEVO)

Vista pública para visitantes que muestra:

1. **Header del Evento**
   - Nombre del evento
   - Fechas de inicio/fin
   - Descripción

2. **Cards de Stands**
   - Información básica del stand
   - Estado (OCUPADO, DISPONIBLE, etc.)
   - Logo del expositor (si existe)
   - Descripción resumida del material comercial
   - Botón "Ver Material Completo"

3. **Indicador Sin Material**
   - Mensaje cuando el stand no tiene material comercial

**Código clave**:
```html
<!-- Material Comercial del Stand -->
<div th:if="${materialesPorStand.containsKey(stand.id)}">
    <div th:with="material=${materialesPorStand.get(stand.id)}">
        <!-- Logo -->
        <img th:src="@{'/uploads/' + ${material.logoPath}}" />
        
        <!-- Descripción -->
        <p th:text="${#strings.abbreviate(material.descripcion, 100)}"></p>
        
        <!-- Botón -->
        <a th:href="@{/material-comercial/ver/{standId}(standId=${stand.id})}">
            Ver Material Completo
        </a>
    </div>
</div>
```

---

#### `evento/lista.html`

**Botón para Ver Stands**:
```html
<!-- Visitantes/Evaluadores pueden ver stands públicos -->
<a th:if="${session.rol != 'ORGANIZADOR' && evento.cantidadStandsAsociados > 0}" 
   th:href="@{/eventos/{id}/stands(id=${evento.id})}" 
   class="btn btn-sm btn-outline-primary" 
   title="Ver stands">
    <i class="bi bi-eye"></i>
</a>
```

---

## 🔄 Flujo de Usuario

### **Expositor**

1. ✅ **Solicitar Stand**
   - Va a "Solicitudes de Stand"
   - Selecciona evento
   - Solicita stand específico

2. ⏳ **Esperar Aprobación**
   - El organizador revisa
   - Aprueba o rechaza

3. 📝 **Gestionar Material Comercial**
   - Va a "Mi Material Comercial"
   - Ve el stand asignado automáticamente
   - Sube logo, catálogos, multimedia
   - Guarda

4. ✅ **Material Vinculado**
   - El material queda asociado al stand
   - Visible para visitantes en ese evento

---

### **Visitante**

1. 🔍 **Buscar Eventos**
   - Ve lista de eventos disponibles
   - Filtra por fecha, estado, etc.

2. 🏢 **Ver Stands del Evento**
   - Clic en icono de ojo junto a "Stands"
   - Ve todos los stands del evento

3. 📄 **Ver Material Comercial**
   - Cada stand muestra preview
   - Clic en "Ver Material Completo"
   - Accede a catálogos, videos, etc.

---

## 🎨 Interfaz Visual

### Material Comercial (Expositor)
```
┌─────────────────────────────────────────┐
│ 🏪 Mi Material Comercial                │
├─────────────────────────────────────────┤
│ ℹ️ Stand Asignado                       │
│ Evento: Feria Tecnológica 2025         │
│ Stand: A-101 - Tech Corner              │
│ Ubicación: Pabellón A, Planta Baja     │
├─────────────────────────────────────────┤
│ [Formulario de subida de archivos]     │
│ - Logo                                  │
│ - Catálogos PDF                         │
│ - Multimedia                            │
└─────────────────────────────────────────┘
```

### Stands Públicos (Visitante)
```
┌─────────────────────────────────────────┐
│ 📅 Stands del Evento: Feria Tech 2025  │
├─────────────────────────────────────────┤
│ ┌─────────┐  ┌─────────┐  ┌─────────┐ │
│ │Stand A-1│  │Stand A-2│  │Stand A-3│ │
│ │  Logo   │  │  Logo   │  │Sin Mat. │ │
│ │Descrip..│  │Descrip..│  │Comercial│ │
│ │[Ver más]│  │[Ver más]│  │         │ │
│ └─────────┘  └─────────┘  └─────────┘ │
└─────────────────────────────────────────┘
```

---

## 🔒 Seguridad y Validaciones

### Backend (ExpositorController)
1. ✅ Valida que el usuario sea EXPOSITOR
2. ✅ Verifica solicitud aprobada antes de permitir guardar
3. ✅ Valida que el stand pertenece al expositor
4. ✅ No permite editar stands de otros expositores
5. ✅ Mantiene integridad referencial Stand → MaterialComercial

### Frontend
1. ✅ Oculta campo standId (no editable)
2. ✅ Muestra advertencia si no tiene stand asignado
3. ✅ Redirige a solicitud de stand si es necesario
4. ✅ Muestra información clara del stand asignado

---

## 📊 Beneficios de esta Implementación

### 1. **Automatización**
- ❌ Antes: Expositor debía ingresar manualmente el ID del stand
- ✅ Ahora: El sistema lo detecta automáticamente de las solicitudes aprobadas

### 2. **Trazabilidad**
- Evento → Stand → Expositor → Material Comercial
- Relación clara y verificable en cada paso

### 3. **Experiencia de Usuario**
- **Expositor**: Ve claramente su stand asignado
- **Visitante**: Navega eventos → stands → material
- **Organizador**: Control total sobre asignaciones

### 4. **Integridad de Datos**
- No se puede asignar material a stand no existente
- No se puede ver material de stand no asignado
- Validaciones en cada paso del flujo

---

## 🧪 Casos de Prueba

### Caso 1: Expositor sin Stand Aprobado
**Entrada**: Expositor intenta guardar material sin solicitud aprobada
**Esperado**: Advertencia + redirección a solicitud de stand

### Caso 2: Expositor con Stand Aprobado
**Entrada**: Expositor con solicitud aprobada accede a material comercial
**Esperado**: Ve información del stand + puede guardar material

### Caso 3: Visitante Ve Stands de Evento
**Entrada**: Visitante accede a eventos/{id}/stands
**Esperado**: Ve lista de stands con material comercial disponible

### Caso 4: Múltiples Stands por Expositor
**Entrada**: Expositor tiene stands en múltiples eventos
**Esperado**: Se usa el primer stand aprobado por defecto

---

## 📁 Archivos Modificados

1. ✅ `ExpositorController.java` - Lógica de asignación automática
2. ✅ `EventoController.java` - Vista pública de stands
3. ✅ `expositor/materialComercial.html` - UI mejorada con info del stand
4. ✅ `evento/stands-publico.html` - Nueva vista para visitantes
5. ✅ `evento/lista.html` - Botón para ver stands públicos

---

## 🎓 Conclusión

El sistema ahora implementa correctamente la relación:

**Evento → Stand → Material Comercial**

- ✅ Asignación automática de stand
- ✅ Validaciones completas
- ✅ Vista pública para visitantes
- ✅ Trazabilidad total
- ✅ Experiencia de usuario mejorada

Los visitantes pueden explorar eventos, ver sus stands y acceder al material comercial de cada expositor de manera intuitiva y organizada.

**Última actualización**: 21 de noviembre de 2025
