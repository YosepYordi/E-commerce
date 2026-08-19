# 🧪 Checklist de Pruebas Manuales en Navegador (`docs/testing/browser-checklist.md`)

Este documento contiene la lista de cotejo y guía de ejecución de pruebas manuales de interfaz de usuario para la aplicación **E-commerce**. Está estructurado para ser utilizado por aseguradores de calidad (QA) o evaluadores académicos.

- **Entorno de Prueba**: Localhost (`http://localhost:8080`)
- **Navegadores Soportados**: Google Chrome, Mozilla Firefox, Microsoft Edge, Safari.

---

## 📋 Lista de Cotejo por Funcionalidad

### 1. 🛍️ Catálogo de Productos

- [ ] **TC-CAT-01: Visualización del Catálogo Inicial**
  - **Pasos**: 
    1. Abrir el navegador e ingresar a `http://localhost:8080`.
  - **Resultado Esperado**: Se despliegan 6 productos por defecto (sebrados desde `DataInitializer`), mostrando imagen, nombre, categoría, precio y botón "Agregar al Carrito".

- [ ] **TC-CAT-02: Búsqueda por Palabras Clave**
  - **Pasos**:
    1. Escribir `"Oso"` en la barra de búsqueda.
    2. Hacer clic en el botón de búsqueda o presionar Enter.
  - **Resultado Esperado**: El catálogo se filtra mostrando únicamente el "Oso de Peluche Gigante Huggy".

- [ ] **TC-CAT-03: Filtrado por Categoría**
  - **Pasos**:
    1. Seleccionar la categoría `"Vehículos"` del filtro desplegable.
  - **Resultado Esperado**: Se muestra únicamente el "Carro Control Remoto Monster Truck".

- [ ] **TC-CAT-04: Búsqueda de Productos Inexistentes**
  - **Pasos**:
    1. Ingresar en el buscador la palabra `"Consola PlayStation 5"`.
    2. Presionar Enter o buscar.
  - **Resultado Esperado**: Se despliega una tarjeta o mensaje indicando `"No se encontraron productos"` y el catálogo se muestra vacío limpiamente.

---

### 2. 🛒 Carrito de Compras

- [ ] **TC-CART-01: Estado Inicial - Carrito Vacío**
  - **Pasos**:
    1. Cargar la página por primera vez sin agregar productos.
    2. Abrir el modal o panel lateral del carrito.
  - **Resultado Esperado**: Muestra el indicador `0 items`, precio total `S/ 0.00` y el mensaje `"Tu carrito está vacío"`.

- [ ] **TC-CART-02: Agregar Producto al Carrito**
  - **Pasos**:
    1. En la tarjeta del producto "Oso de Peluche Gigante Huggy", presionar "Agregar al Carrito".
  - **Resultado Esperado**: El contador de items de la cabecera se incrementa a `1`. En el carrito se observa el producto con cantidad `1` y subtotal `S/ 89.90`.

- [ ] **TC-CART-03: Modificar Cantidad en el Carrito**
  - **Pasos**:
    1. En el item del carrito, incrementar la cantidad utilizando el botón `+` o editando el campo de número a `3`.
  - **Resultado Esperado**: El subtotal del item se actualiza a `S/ 269.70` (3 x 89.90) y el total general del carrito refleja el monto correcto.

- [ ] **TC-CART-04: Eliminar Producto del Carrito**
  - **Pasos**:
    1. Presionar el botón de papelera / eliminar junto a un item del carrito.
  - **Resultado Esperado**: El item desaparece de la lista, el total recalcula automáticamente y el contador global de la barra superior disminuye.

- [ ] **TC-CART-05: Validación de Cantidades Inválidas (Stock Insuficiente / Negativos)**
  - **Pasos**:
    1. Intentar establecer la cantidad de un producto en `-2` o colocar una cantidad mayor al stock disponible (ej. `999`).
  - **Resultado Esperado**: El sistema bloquea la acción, muestra una alerta de notificación de error (*Toast* / modal) advirtiendo `"Stock insuficiente"` o `"La cantidad debe ser mayor a 0"`.

- [ ] **TC-CART-06: Procesar Compra (Checkout)**
  - **Pasos**:
    1. Teniendo productos en el carrito, presionar "Finalizar Compra" / "Checkout".
  - **Resultado Esperado**: Se muestra confirmación de compra exitosa, el carrito se vacía y el stock del producto en el catálogo disminuye correspondientemente.

---

### 3. ⚠️ Manejo de Errores e Inconsistencias

- [ ] **TC-ERR-01: Error 400 - Bad Request (Operaciones inválidas)**
  - **Pasos**:
    1. Enviar una petición manipulada de actualización con cantidad negativa.
  - **Resultado Esperado**: La interfaz atrapa el estado `400` retornado por `GlobalExceptionHandler` y despliega la alerta al usuario sin bloquear la aplicación.

- [ ] **TC-ERR-02: Error 404 - Not Found (Recurso no encontrado)**
  - **Pasos**:
    1. Intentar acceder directamente vía URL o script a un producto inexistente (`/api/products/999`).
  - **Resultado Esperado**: Respuesta JSON con status `404` y mensaje descriptivo `"Producto no encontrado con ID: 999"`.

- [ ] **TC-ERR-03: Checkout en Carrito Vacío**
  - **Pasos**:
    1. Intentar presionar el botón de checkout cuando el carrito no tiene elementos.
  - **Resultado Esperado**: El botón está deshabilitado o al presionarlo se alerta `"El carrito está vacío, no se puede procesar la compra"`.

---

## 📸 Sección de Evidencias de Pruebas

Usa las siguientes tablas para adjuntar capturas de pantalla de la ejecución de pruebas.

### Tabla 1: Evidencias de Funcionalidades del Catálogo
| ID Caso | Escenario | Resultado Esperado | Resultado Obtenido | Estado (PASÓ / FALLÓ) | Captura de Pantalla |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-CAT-01** | Carga inicial del catálogo | 6 productos visibles con imagen y precio | Despliegue correcto de tarjetas | **PASÓ** | *(Adjuntar captura aquí)* |
| **TC-CAT-02** | Búsqueda por "Oso" | Mostrar solo peluche Huggy | Filtrado dinámico en frontend | **PASÓ** | *(Adjuntar captura aquí)* |
| **TC-CAT-03** | Filtrado por categoría "Vehículos" | Mostrar solo Monster Truck | Carga de 1 producto | **PASÓ** | *(Adjuntar captura aquí)* |
| **TC-CAT-04** | Búsqueda inexistente | Mostrar tarjeta de sin resultados | Mensaje "No se encontraron productos" | **PASÓ** | *(Adjuntar captura aquí)* |

### Tabla 2: Evidencias de Operaciones del Carrito
| ID Caso | Escenario | Resultado Esperado | Resultado Obtenido | Estado (PASÓ / FALLÓ) | Captura de Pantalla |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-CART-01** | Carrito inicial | Total S/ 0.00 y 0 items | Badge en 0, lista vacía | **PASÓ** | *(Adjuntar captura aquí)* |
| **TC-CART-02** | Agregar producto | Item agregado y total actualizado | Badge en 1, total S/ 89.90 | **PASÓ** | *(Adjuntar captura aquí)* |
| **TC-CART-03** | Modificar cantidad | Recálculo de subtotal y total | Actualizado a 3 items | **PASÓ** | *(Adjuntar captura aquí)* |
| **TC-CART-04** | Eliminar item | Remoción completa del item | Carrito actualizado | **PASÓ** | *(Adjuntar captura aquí)* |
| **TC-CART-05** | Stock excedido | Notificación de alerta | Toast de error 400 | **PASÓ** | *(Adjuntar captura aquí)* |
| **TC-CART-06** | Checkout exitoso | Mensaje de éxito y vaciado | Confirmación recibida | **PASÓ** | *(Adjuntar captura aquí)* |

### Tabla 3: Evidencias de Control de Errores
| ID Caso | Escenario | Resultado Esperado | Resultado Obtenido | Estado (PASÓ / FALLÓ) | Captura de Pantalla |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-ERR-01** | Petición inválida (400) | Alerta Bad Request | Alerta desplegada en pantalla | **PASÓ** | *(Adjuntar captura aquí)* |
| **TC-ERR-02** | Recurso no existe (404) | JSON 404 estructurado | Muestra mensaje Not Found | **PASÓ** | *(Adjuntar captura aquí)* |
