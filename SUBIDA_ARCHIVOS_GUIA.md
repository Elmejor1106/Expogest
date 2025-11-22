# 📁 Sistema de Subida de Archivos - Material Comercial

## 🎯 Funcionalidad Implementada

Se ha agregado la capacidad de **subir archivos reales** (PDF, PNG, JPG, videos, etc.) al módulo de Material Comercial para expositores.

---

## 📋 Características

### 1. **Tipos de Archivos Soportados**

#### Logo de la Empresa
- **Formatos**: PNG, JPG, JPEG
- **Tamaño máximo**: 5 MB
- **Ubicación**: `uploads/material-comercial/logos/`

#### Catálogos PDF
- **Formato**: PDF únicamente
- **Tamaño máximo**: 10 MB por archivo
- **Múltiples archivos**: Sí
- **Ubicación**: `uploads/material-comercial/catalogos/`

#### Multimedia (Imágenes y Videos)
- **Formatos**: Imágenes (JPG, PNG, GIF, etc.) y Videos (MP4, AVI, MOV, etc.)
- **Tamaño máximo**: 50 MB por archivo
- **Múltiples archivos**: Sí
- **Ubicación**: `uploads/material-comercial/multimedia/`

---

## 🔧 Componentes Implementados

### 1. **FileStorageService**
Servicio para gestionar la subida, almacenamiento y descarga de archivos.

**Ubicación**: `src/main/java/com/expogest/expogest/servicios/FileStorageService.java`

**Métodos principales**:
- `storeFile()`: Almacena archivo con nombre único (UUID)
- `loadFileAsResource()`: Carga archivo para descarga/visualización
- `deleteFile()`: Elimina archivos
- `isValidFileType()`: Valida tipo MIME
- `isValidFileSize()`: Valida tamaño máximo

### 2. **FileController**
Controlador para servir archivos subidos a través de URLs públicas.

**Ubicación**: `src/main/java/com/expogest/expogest/controlador/FileController.java`

**Endpoint**:
```
GET /uploads/{subDir}/{fileName}
```

### 3. **Modificaciones en MaterialComercial**
Se agregaron campos para almacenar rutas de archivos locales:

```java
private String logoPath;           // Ruta del logo subido
private List<String> catalogoPaths;      // Rutas de PDFs subidos
private List<String> multimediaPaths;    // Rutas de multimedia subida
```

### 4. **Actualización de ExpositorController**
Método `guardarMaterialComercial()` ahora procesa:
- `@RequestParam MultipartFile logoFile`
- `@RequestParam List<MultipartFile> catalogoFiles`
- `@RequestParam List<MultipartFile> multimediaFiles`

**Validaciones implementadas**:
- ✅ Tipo de archivo (MIME type)
- ✅ Tamaño máximo
- ✅ Nombres seguros (sin path traversal)
- ✅ Eliminación de archivos antiguos al reemplazar

---

## 🎨 Interfaz de Usuario

### Formulario Actualizado
El formulario en `expositor/materialComercial.html` ahora incluye:

1. **Opciones duales**: Subir archivo O proporcionar URL
2. **Inputs de archivo múltiple** para catálogos y multimedia
3. **Vista previa** de archivos subidos con botones para visualizar/descargar
4. **Encoding**: `enctype="multipart/form-data"` en el formulario

### Ejemplo Visual

```html
<!-- Logo -->
<input type="file" name="logoFile" accept="image/png,image/jpeg">
<input type="url" name="logoUrl" placeholder="https://...">

<!-- Catálogos PDF -->
<input type="file" name="catalogoFiles" accept="application/pdf" multiple>
<textarea name="catalogoUrls">URLs separadas por línea</textarea>

<!-- Multimedia -->
<input type="file" name="multimediaFiles" accept="image/*,video/*" multiple>
<textarea name="multimediaUrls">URLs separadas por línea</textarea>
```

---

## 🔒 Seguridad

### 1. **Permisos de Acceso**
- **AuthFilter** configurado para permitir acceso público a `/uploads/*`
- Necesario para que evaluadores y visitantes vean el material

### 2. **Validaciones de Seguridad**
- ✅ Sanitización de nombres de archivo (`StringUtils.cleanPath`)
- ✅ Prevención de path traversal (`..\..` bloqueado)
- ✅ UUID único para evitar sobrescritura
- ✅ Validación de tipos MIME
- ✅ Límites de tamaño configurables

### 3. **Configuración**
```properties
# application.properties
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=200MB
file.upload-dir=uploads
```

---

## 📁 Estructura de Directorios

```
expogest/
├── uploads/                          # Directorio raíz de archivos
│   └── material-comercial/
│       ├── logos/                    # Logos de empresas
│       │   └── {uuid}.png
│       ├── catalogos/                # PDFs de catálogos
│       │   ├── {uuid}.pdf
│       │   └── {uuid}.pdf
│       └── multimedia/               # Imágenes y videos
│           ├── {uuid}.jpg
│           ├── {uuid}.mp4
│           └── {uuid}.png
```

---

## 🚀 Uso para el Expositor

### Paso 1: Acceder al Material Comercial
1. Iniciar sesión como **EXPOSITOR**
2. Ir al panel del expositor
3. Clic en "Mi material comercial"

### Paso 2: Subir Archivos
1. **Logo**: Seleccionar imagen PNG/JPG (máx 5MB)
2. **Catálogos**: Seleccionar uno o varios PDFs (máx 10MB c/u)
3. **Multimedia**: Seleccionar imágenes/videos (máx 50MB c/u)

### Paso 3: Alternativa - URLs
Si prefieres usar archivos alojados en otros servicios:
- Pega las URLs en los campos correspondientes
- Funciona con YouTube, Vimeo, Google Drive, etc.

### Paso 4: Vista Previa
Después de guardar, verás:
- ✅ Miniatura del logo
- ✅ Botones para descargar catálogos
- ✅ Enlaces para ver multimedia

---

## 🌐 Acceso Público

### Visitantes y Evaluadores
Los archivos subidos son accesibles públicamente a través de:

```
http://localhost:8115/uploads/material-comercial/logos/{uuid}.png
http://localhost:8115/uploads/material-comercial/catalogos/{uuid}.pdf
http://localhost:8115/uploads/material-comercial/multimedia/{uuid}.mp4
```

**Nota**: Los UUIDs previenen adivinación de nombres, pero los archivos son públicos para facilitar el acceso.

---

## 🔄 Gestión de Archivos

### Actualización
- Al subir un nuevo logo, el anterior se **elimina automáticamente**
- Catálogos y multimedia se **acumulan** (no se eliminan los antiguos)

### Eliminación Manual
Actualmente no hay interfaz para eliminar archivos individuales. Los archivos se eliminan:
1. Al reemplazar el logo
2. Manualmente desde el sistema de archivos

---

## 📊 Limitaciones y Configuración

| Tipo | Tamaño Máximo | Múltiples | Formatos |
|------|---------------|-----------|----------|
| Logo | 5 MB | No | PNG, JPG |
| Catálogos | 10 MB c/u | Sí | PDF |
| Multimedia | 50 MB c/u | Sí | Imágenes, Videos |

**Request máximo total**: 200 MB (configurable en `application.properties`)

---

## 🐛 Solución de Problemas

### Error: "El archivo supera el tamaño permitido"
- Verifica que el archivo esté dentro de los límites
- Comprime el archivo si es necesario

### Error: "Tipo de archivo no válido"
- Verifica que el formato sea el correcto
- Para catálogos, solo se aceptan PDFs
- Para logos, solo PNG/JPG

### Los archivos no se visualizan
- Verifica que el directorio `uploads/` exista
- Asegúrate de que `/uploads/*` esté en rutas públicas del AuthFilter

---

## ✅ Estado de Implementación

- ✅ FileStorageService creado
- ✅ FileController para servir archivos
- ✅ Entidad MaterialComercial actualizada
- ✅ ExpositorController con procesamiento de archivos
- ✅ Formulario HTML actualizado con inputs de archivo
- ✅ Vista previa de archivos subidos
- ✅ Validaciones de seguridad
- ✅ Configuración de Spring Boot
- ✅ AuthFilter permite acceso a /uploads
- ✅ Directorio uploads/ creado
- ✅ Compilación exitosa

---

## 🎓 Notas Técnicas

### Nombres de Archivo Únicos
Se usa UUID para generar nombres únicos:
```java
String newFileName = UUID.randomUUID().toString() + fileExtension;
// Ejemplo: a3f5c8d2-9b7e-4f1a-8c3d-2e9f7b1a5c8d.pdf
```

### Almacenamiento
Los archivos se almacenan en el sistema de archivos local, no en MongoDB. MongoDB solo guarda las rutas como strings.

### Escalabilidad
Para producción, considera:
- Usar almacenamiento en la nube (AWS S3, Azure Blob, Google Cloud Storage)
- Implementar CDN para mejor rendimiento
- Agregar compresión automática de imágenes

---

## 📞 Soporte

Funcionalidad lista y probada en:
- ✅ Windows (PowerShell)
- ✅ Spring Boot 3.5.7
- ✅ Java 25
- ✅ MongoDB Atlas

**Última actualización**: 21 de noviembre de 2025
