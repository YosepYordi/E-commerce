# 📬 Guía de Ejecución de Colección Postman (`docs/testing/postman/README.md`)

Este directorio contiene la colección oficial de pruebas de API para el proyecto **E-commerce**: [`Ecommerce.postman_collection.json`](Ecommerce.postman_collection.json) y su archivo de entorno correspondiente [`ecommerce.postman_environment.json`](ecommerce.postman_environment.json).

---

## 📌 Estructura y Casos de Prueba de la Colección

La colección se encuentra organizada en **5 módulos principales** que validan tanto flujos de éxito como el manejo de errores HTTP standard (400, 401, 403, 404, 409):

### 1. Autenticación (`/api/auth`)
- **Login Exitoso (Guardar Token)**: Autentica credenciales administrativas (`admin` / `password`), verifica la respuesta `200 OK` y almacena automáticamente el JWT devuelto en la variable de entorno `token`.

### 2. Catálogo Público (`/api/products`)
- **Obtener Productos Catálogo**: Consulta `GET /api/products`, extrae el ID del primer producto en la variable `productId` y captura la cookie de sesión `XSRF-TOKEN` en la variable `xsrfToken`.
- **Obtener Producto por ID**: Consulta pública `GET /api/products/{{productId}}`.

### 3. CRUD Protegido de Productos (`/api/products`)
- **Crear Producto (JWT Required)**: Operación `POST /api/products` adjuntando `Authorization: Bearer {{token}}`. Guarda el ID del producto creado.
- **Actualizar Producto (JWT Required)**: Operación `PUT /api/products/{{productId}}` con encabezado JWT.

### 4. Validación de Errores y Respuestas HTTP
- **Error 400 Bad Request (Datos Inválidos)**: Intento de creación con campos requeridos vacíos o valores negativos.
- **Error 401 Unauthorized (Sin Token)**: Intento de escritura en productos sin encabezado de autorización JWT.
- **Error 403 Forbidden (CSRF Requerido sin Token)**: Intento de mutación de carrito (`POST /api/cart/add`) omitiendo el encabezado `X-XSRF-TOKEN`. Verifica de forma estricta el código de respuesta `403 Forbidden`.
- **Error 404 Not Found (Producto Inexistente)**: Consulta a ID inexistente (`GET /api/products/999999`).
- **Error 400/409 (Exceder Stock en Carrito)**: Intento de agregar una cantidad mayor al stock disponible.

### 5. Carrito de Compras y Checkout (`/api/cart`)
*Todas las operaciones mutativas del carrito incluyen la cabecera `X-XSRF-TOKEN: {{xsrfToken}}` obtenida automáticamente en las peticiones GET previas.*
- **Ver Carrito de Compras**: Consulta `GET /api/cart`.
- **Agregar Producto al Carrito**: `POST /api/cart/add` con `X-XSRF-TOKEN`.
- **Actualizar Cantidad en Carrito**: `PUT /api/cart/update` con `X-XSRF-TOKEN`.
- **Procesar Checkout**: `POST /api/cart/checkout` reduciendo stock con `X-XSRF-TOKEN`.
- **Eliminar Producto del Carrito**: `DELETE /api/cart/items/{{productId}}` con `X-XSRF-TOKEN`.
- **Vaciar Carrito**: `DELETE /api/cart` con `X-XSRF-TOKEN`.
- **Eliminar Producto Creado (Cleanup)**: `DELETE /api/products/{{productId}}` con JWT de Admin.

---

## 🔒 Manejo de Seguridad CSRF y JWT en Postman

1. **JWT (JSON Web Token)**: Las peticiones administrativas de productos extraen el token durante el login y lo reenvían usando la cabecera `Authorization: Bearer {{token}}`.
2. **CSRF (Cross-Site Request Forgery)**: Spring Security publica una cookie `XSRF-TOKEN` en respuestas `GET`. Los scripts en Postman guardan esta cookie automáticamente y las peticiones mutativas de carrito (`POST`, `PUT`, `DELETE`) la incluyen en el encabezado `X-XSRF-TOKEN: {{xsrfToken}}`.

---

## 🚀 Cómo Ejecutar la Colección

### Opción A: Desde Postman Desktop App

1. Abre **Postman**.
2. Haz clic en **Import** y selecciona `Ecommerce.postman_collection.json` y `ecommerce.postman_environment.json`.
3. Selecciona el entorno **Ecommerce Local Environment** en la esquina superior derecha.
4. Asegúrate de que el servidor Spring Boot esté corriendo localmente (`http://localhost:8080`).
5. Haz clic derecho en la colección **E-Commerce API Test Suite** -> **Run collection**.
6. Haz clic en **Run E-Commerce API Test Suite** y verifica que todos los tests estén en verde (**Pass**).

---

### Opción B: Desde Terminal con Newman CLI

Ejecuta la colección junto con su archivo de entorno usando **Newman**:

```bash
npx newman run docs/testing/postman/Ecommerce.postman_collection.json -e docs/testing/postman/ecommerce.postman_environment.json
```

Resumen esperado de ejecución:
```text
┌─────────────────────────┬──────────┬──────────┐
│                         │ executed │   failed │
├─────────────────────────┼──────────┼──────────┤
│ iterations              │        1 │        0 │
│ requests                │       15 │        0 │
│ testScripts             │       15 │        0 │
│ assertions              │       30 │        0 │
└─────────────────────────┴──────────┴──────────┘
```
