# 🧹 Limpiar Usuarios Duplicados en MongoDB

## ⚠️ Problema
Tienes múltiples usuarios con el correo `julian@correo.com` en la base de datos, lo que causa el error:
```
Query returned non unique result
```

---

## 🔧 Solución 1: Desde MongoDB Atlas (Recomendado)

### Paso 1: Acceder a MongoDB Atlas
1. Ve a https://cloud.mongodb.com
2. Inicia sesión
3. Selecciona tu cluster
4. Haz clic en "Browse Collections"
5. Selecciona la base de datos `expogest`
6. Selecciona la colección `usuarios`

### Paso 2: Buscar Duplicados
En el filtro de búsqueda, escribe:
```json
{ "correo": "julian@correo.com" }
```

### Paso 3: Eliminar Duplicados
- Verás todos los usuarios con ese correo
- **DEJA SOLO UNO** (el más reciente o el que tenga los datos correctos)
- Elimina los demás haciendo clic en el icono de basura 🗑️

### Paso 4: Verificar otros duplicados
Busca si hay otros correos duplicados ejecutando esta consulta en MongoDB:
```javascript
db.usuarios.aggregate([
  { $group: { 
      _id: "$correo", 
      count: { $sum: 1 },
      ids: { $push: "$_id" }
  }},
  { $match: { count: { $gt: 1 } } }
])
```

---

## 🔧 Solución 2: Desde el Admin Panel

### Opción Rápida
1. Ve a `http://localhost:8115/admin/usuarios`
2. Busca todos los usuarios con correo `julian@correo.com`
3. Elimina los duplicados manualmente
4. Deja solo UNO

---

## 🔧 Solución 3: Script de Limpieza Automática

Si quieres crear un endpoint temporal para limpiar duplicados:

### Crear AdminRestController temporal

Agrega este método en `AdminRestController.java`:

```java
@GetMapping("/limpiar-duplicados")
public ResponseEntity<Map<String, Object>> limpiarDuplicados() {
    Map<String, Object> response = new HashMap<>();
    List<String> correosLimpiados = new ArrayList<>();
    
    // Obtener todos los usuarios
    List<Usuario> todosLosUsuarios = usuarioRepository.findAll();
    
    // Agrupar por correo
    Map<String, List<Usuario>> usuariosPorCorreo = todosLosUsuarios.stream()
        .collect(Collectors.groupingBy(Usuario::getCorreo));
    
    // Eliminar duplicados (dejar solo el primero)
    for (Map.Entry<String, List<Usuario>> entry : usuariosPorCorreo.entrySet()) {
        List<Usuario> usuarios = entry.getValue();
        if (usuarios.size() > 1) {
            correosLimpiados.add(entry.getKey());
            // Eliminar todos excepto el primero
            for (int i = 1; i < usuarios.size(); i++) {
                usuarioRepository.deleteById(usuarios.get(i).getId());
            }
        }
    }
    
    response.put("mensaje", "Duplicados eliminados");
    response.put("correos_afectados", correosLimpiados);
    response.put("total", correosLimpiados.size());
    
    return ResponseEntity.ok(response);
}
```

Luego ejecuta:
```
GET http://localhost:8115/api/admin/limpiar-duplicados
```

---

## ✅ Verificar que funciona

Después de limpiar duplicados:

1. **Reinicia la aplicación** (Spring DevTools lo hará automáticamente)
2. El índice único se creará automáticamente
3. Intenta hacer login con el usuario expositor
4. Debería funcionar correctamente

---

## 🛡️ Prevención de Futuros Duplicados

Ya se implementó:
- ✅ `@Indexed(unique = true)` en el campo `correo`
- ✅ `spring.data.mongodb.auto-index-creation=true` en application.properties
- ✅ Validación en `AdminController.guardarUsuario()` que verifica duplicados

MongoDB rechazará automáticamente cualquier intento de insertar un correo duplicado.

---

## 🔍 Verificar Índice Creado

En MongoDB Atlas, ejecuta:
```javascript
db.usuarios.getIndexes()
```

Deberías ver algo como:
```json
[
  { "v": 2, "key": { "_id": 1 }, "name": "_id_" },
  { "v": 2, "key": { "correo": 1 }, "name": "correo", "unique": true }
]
```

---

## 🚨 Si el problema persiste

1. Detén la aplicación completamente
2. Limpia los duplicados en MongoDB
3. Elimina el folder `target/`
4. Ejecuta: `.\mvnw.cmd clean package`
5. Reinicia la aplicación

---

**¡Problema resuelto!** El error de "non unique result" no volverá a ocurrir. 🎉
