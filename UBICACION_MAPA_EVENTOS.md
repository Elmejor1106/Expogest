# 📍 Ubicación de Eventos mediante Mapa

## Resumen de Cambios

Se ha implementado un sistema de ubicación de eventos exclusivamente mediante mapa interactivo. El campo de texto "lugar" ha sido eliminado completamente y ahora **solo se utilizan coordenadas geográficas** (latitud y longitud) para definir la ubicación de los eventos.

---

## ✅ Cambios Realizados

### 1. **Modelo de Datos (Evento.java)**
- ❌ **Eliminado**: Campo `String lugar`
- ✅ **Mantenido**: Campos `Double latitud` y `Double longitud` (ahora obligatorios)
- Los eventos ahora se identifican por sus coordenadas geográficas exactas

### 2. **Servicio (EventoService.java)**
Se agregaron validaciones para garantizar la integridad de las coordenadas:
- Las coordenadas son **obligatorias** al crear o editar un evento
- Validación de rango de latitud: -90° a 90°
- Validación de rango de longitud: -180° a 180°
- Mensajes de error descriptivos si falta la ubicación

### 3. **Formulario de Evento (form.html)**
- ❌ **Eliminado**: Campo de texto "Lugar"
- ✅ **Mantenido**: Mapa interactivo con Leaflet
- El mapa es obligatorio y permite:
  - Hacer clic para seleccionar ubicación
  - Arrastrar el marcador para ajustar
  - Usar ubicación actual del dispositivo
  - Visualización en tiempo real de las coordenadas seleccionadas

### 4. **Vistas Actualizadas**
Todos los archivos que mostraban el campo "lugar" ahora muestran las coordenadas:

#### `evento/lista.html`
- Columna "Lugar" → "Ubicación"
- Muestra: `📍 4.6097, -74.0817`

#### `evento/stands.html`
- Campo "Lugar" → "Ubicación"
- Muestra coordenadas con icono de pin

#### `solicitud/form.html`
- Campo "Lugar" → "Ubicación"
- Muestra coordenadas del evento

#### `solicitud/porEvento.html`
- Campo "Lugar" → "Ubicación"
- Muestra coordenadas con formato

#### `expositor/nuevaSolicitudStand.html`
- Campo "Lugar" → "Ubicación"
- Muestra coordenadas del evento

---

## 🗺️ Funcionamiento del Mapa

### Tecnología
- **Leaflet.js**: Biblioteca de mapas interactivos de código abierto
- **OpenStreetMap**: Proveedor de mapas base
- **Marcador personalizado**: Icono rojo arrastrable

### Características
1. **Selección de ubicación**: Click en cualquier punto del mapa
2. **Ajuste de ubicación**: Arrastrar el marcador
3. **Geolocalización**: Botón para usar ubicación actual del navegador
4. **Validación en frontend**: No permite enviar formulario sin ubicación
5. **Validación en backend**: Verifica coordenadas válidas

### Almacenamiento en MongoDB
```json
{
  "_id": "123abc",
  "nombre": "Feria Tecnológica 2025",
  "descripcion": "Evento de tecnología e innovación",
  "fechaInicio": "2025-03-15",
  "fechaFin": "2025-03-17",
  "latitud": 4.609715,
  "longitud": -74.081749,
  "estado": "ACTIVO",
  "capacidadMaximaStands": 50
}
```

---

## 📊 Formato de Visualización

En todas las vistas, las coordenadas se muestran con el formato:
```
📍 4.6097, -74.0817
```

Si no hay coordenadas (eventos antiguos):
```
⚠️ Sin ubicación
```

---

## 🔧 Validaciones Implementadas

### Frontend (JavaScript)
```javascript
// Validar que se haya seleccionado una ubicación
if (!lat || !lng) {
    alert('Por favor, selecciona la ubicación del evento en el mapa');
    return false;
}
```

### Backend (Java)
```java
// Validar coordenadas obligatorias
if (evento.getLatitud() == null || evento.getLongitud() == null) {
    throw new IllegalArgumentException("Las coordenadas son obligatorias");
}

// Validar rangos
if (evento.getLatitud() < -90 || evento.getLatitud() > 90) {
    throw new IllegalArgumentException("Latitud inválida");
}
if (evento.getLongitud() < -180 || evento.getLongitud() > 180) {
    throw new IllegalArgumentException("Longitud inválida");
}
```

---

## 🚀 Beneficios

1. **Precisión exacta**: Coordenadas GPS en lugar de texto libre
2. **Integración con mapas**: Fácil integración con Google Maps, Waze, etc.
3. **Búsquedas geográficas**: Posibilidad de búsquedas por proximidad en el futuro
4. **Experiencia mejorada**: Interfaz visual e intuitiva
5. **Estandarización**: Formato consistente en toda la aplicación

---

## 📱 Compatibilidad

- ✅ Desktop (Chrome, Firefox, Edge, Safari)
- ✅ Móvil (navegadores modernos)
- ✅ Geolocalización (requiere permisos del navegador)
- ✅ MongoDB (soporta tipos Double nativamente)

---

## 🔮 Futuras Mejoras Posibles

1. Búsqueda de eventos por cercanía geográfica
2. Visualización de todos los eventos en un mapa general
3. Cálculo de distancias entre ubicaciones
4. Integración con APIs de direcciones (geocoding reverso)
5. Exportar ubicación a aplicaciones de navegación

---

**Fecha de implementación**: 13 de noviembre de 2025  
**Estado**: ✅ Completado y compilado exitosamente
