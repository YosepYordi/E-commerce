# 🧪 Lista de Verificación de Pruebas de Navegador (Browser Checklist)

Este documento detalla la matriz de pruebas manuales para la interfaz de usuario web (`index.html`) del sistema de **E-commerce**.

---

## 📋 Resumen del Entorno de Pruebas

- **URL de la Aplicación**: `http://localhost:8080`
- **Navegadores Compatibles**: Google Chrome, Mozilla Firefox, Microsoft Edge, Safari.
- **Dispositivos**: Desktop (1920x1080) y Mobile (375x812 responsive).

---

## 📑 Matriz de Casos de Prueba

| ID | Área / Funcionalidad | Pasos de Ejecución | Resultado Esperado | Estado | Evidencia / Notas |
| :--- | :--- | :--- | :--- | :---: | :--- |
| **TC-UI-01** | **Carga Inicial del Catálogo** | 1. Navegar a `http://localhost:8080`. <br> 2. Observar la cuadrícula de productos. | Se despliega el catálogo de productos con imagen, título, categoría, precio y botón "Agregar al Carrito". | **PASÓ** | Productos cargados desde `/api/products`. |
| **TC-UI-02** | **Filtrado por Categoría** | 1. Seleccionar una categoría en el dropdown (ej: "Periféricos"). | La lista se actualiza mostrando únicamente productos pertenecientes a la categoría seleccionada. | **PASÓ** | Petición `GET /api/products?categoria=Perifericos`. |
| **TC-UI-03** | **Búsqueda por Texto** | 1. Escribir "Teclado" en la barra de búsqueda. <br> 2. Presionar Buscar o Enter. | Se filtran los productos coincidiendo con el nombre o descripción introducida. | **PASÓ** | Petición `GET /api/products?query=Teclado`. |
| **TC-UI-04** | **Agregar Producto al Carrito** | 1. Hacer clic en "Agregar al Carrito" en un producto disponible. | El ícono/badge del carrito incrementa su contador y el producto aparece en el panel lateral/modal del carrito. | **PASÓ** | Petición `POST /api/cart/add`. |
| **TC-UI-05** | **Modificar Cantidad en Carrito** | 1. Abrir el carrito. <br> 2. Cambiar la cantidad de un ítem de 1 a 3. | El subtotal del producto y el total general del carrito se recalculan automáticamente. | **PASÓ** | Petición `PUT /api/cart/update`. |
| **TC-UI-06** | **Eliminar Ítem del Carrito** | 1. Abrir el carrito. <br> 2. Hacer clic en el botón "Eliminar" (ícono de papelera). | El producto se remueve del carrito y el total se actualiza. | **PASÓ** | Petición `DELETE /api/cart/items/{id}`. |
| **TC-UI-07** | **Exceder Stock Disponible** | 1. Intentar agregar una cantidad superior al stock máximo del producto. | Se muestra una notificación/alerta de error indicando que la cantidad supera el stock disponible. | **PASÓ** | Manejo de respuesta 400 Bad Request en la UI. |
| **TC-UI-08** | **Proceso de Checkout** | 1. Abrir el carrito con productos. <br> 2. Hacer clic en "Finalizar Compra" / "Checkout". | Se muestra mensaje de confirmación de compra exitosa, el stock se deduce y el carrito queda vacío. | **PASÓ** | Petición `POST /api/cart/checkout`. |
| **TC-UI-09** | **Persistencia de Sesión** | 1. Agregar ítems al carrito. <br> 2. Recargar la página (`F5`). | Los elementos agregados al carrito se mantienen gracias a la sesión HTTP activa. | **PASÓ** | Petición `GET /api/cart` al cargar. |
| **TC-UI-10** | **Diseño Responsive** | 1. Abrir DevTools y simular pantalla móvil (375px). | El diseño se adapta fluidamente, el menú y el carrito son accesibles en pantallas pequeñas. | **PASÓ** | CSS flexbox y media queries verificados. |

---

## 📸 Guía para Adjuntar Evidencias

Al ejecutar las pruebas manualmente antes de liberar un release o abrir un PR:
1. Capturar pantalla de los casos de prueba ejecutados.
2. Guardar la imagen en `docs/testing/screenshots/` con el nombre del caso de prueba (ej: `TC-UI-04-agregar-carrito.png`).
3. Enlazar la captura en el reporte de PR en la sección de Evidencias de Navegador.
