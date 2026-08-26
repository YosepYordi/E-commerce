# 📖 Especificación de la API REST - E-Commerce

Documentación completa de los servicios web RESTful del sistema de **E-commerce**, incluyendo autenticación JWT, esquemas de datos, códigos de estado HTTP y ejemplos de uso en cURL.

---

## 🌐 Configuración General

- **URL Base**: `http://localhost:8080` (en entorno local)
- **Formato de Comunicación**: JSON (`Content-Type: application/json; charset=UTF-8`)

---

## 🔐 Autenticación y Autorización

La API implementa autenticación basada en **JSON Web Tokens (JWT)** para la gestión y administración del catálogo de productos.

### Encabezado de Autenticación
Para acceder a endpoints protegidos (operaciones `POST`, `PUT`, `DELETE` en productos), se debe incluir el token devuelto por el servicio de Login en el encabezado HTTP:

```http
Authorization: Bearer <tu_token_jwt>
```

---

## 🛠️ Esquemas de Datos (DTOs)

### `LoginRequest`
```json
{
  "username": "admin",
  "password": "password"
}
```

### `TokenResponse`
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresInSeconds": 3600
}
```

### `ProductDTO`
```json
{
  "id": 1,
  "nombre": "Laptop Gamer Pro",
  "descripcion": "Intel i9, 32GB RAM, RTX 4080",
  "precio": 1999.99,
  "stock": 10,
  "categoria": "Laptops",
  "imagenUrl": "https://example.com/laptop.jpg"
}
```

### `ProductRequest`
```json
{
  "nombre": "Teclado Mecánico RGB",
  "descripcion": "Switches Blue táctiles",
  "precio": 89.99,
  "stock": 15,
  "categoria": "Periféricos",
  "imagenUrl": "https://example.com/teclado.jpg"
}
```

### `AddToCartRequest`
```json
{
  "productId": 1,
  "cantidad": 2
}
```

### `UpdateCartRequest`
```json
{
  "productId": 1,
  "cantidad": 5
}
```

### `CartSummaryDTO`
```json
{
  "items": [
    {
      "productId": 1,
      "nombreProducto": "Laptop Gamer Pro",
      "precioUnitario": 1999.99,
      "cantidad": 2,
      "subtotal": 3999.98,
      "imagenUrl": "https://example.com/laptop.jpg"
    }
  ],
  "total": 3999.98,
  "totalItems": 2
}
```

### `ApiError` (Estructura de Errores Standard)
```json
{
  "timestamp": "2026-08-25T18:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "La cantidad solicitada supera el stock disponible",
  "path": "/api/cart/add"
}
```

---

## 📌 Endpoints de la API

### 1. Autenticación (`/api/auth`)

#### `POST /api/auth/login`
Autentica un usuario administrativo y genera un JWT válido.

- **Acceso**: Público
- **Cuerpo HTTP**: `LoginRequest`
- **Respuestas**:
  - `200 OK`: Retorna `TokenResponse`.
  - `401 Unauthorized`: Credenciales inválidas.

**Ejemplo cURL**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin", "password":"password"}'
```

---

### 2. Catálogo de Productos (`/api/products`)

#### `GET /api/products`
Obtiene la lista de productos del catálogo. Permite filtrado opcional.

- **Acceso**: Público
- **Parámetros Query**:
  - `categoria` (string, opcional): Filtra por categoría exacta.
  - `query` (string, opcional): Búsqueda por coincidencia parcial en nombre o descripción.
- **Respuestas**:
  - `200 OK`: Arreglo de `ProductDTO`.

**Ejemplo cURL**:
```bash
curl -X GET "http://localhost:8080/api/products?categoria=Perifericos"
```

#### `GET /api/products/{id}`
Obtiene el detalle de un producto específico por su ID.

- **Acceso**: Público
- **Respuestas**:
  - `200 OK`: Objeto `ProductDTO`.
  - `404 Not Found`: Si el producto no existe.

**Ejemplo cURL**:
```bash
curl -X GET http://localhost:8080/api/products/1
```

#### `POST /api/products`
Crea un nuevo producto en el catálogo.

- **Acceso**: Protegido (`ROLE_ADMIN`)
- **Headers**: `Authorization: Bearer <token>`
- **Cuerpo HTTP**: `ProductRequest`
- **Respuestas**:
  - `201 Created`: Producto creado exitosamente (`ProductDTO`).
  - `400 Bad Request`: Datos de producto inválidos o incompletos.
  - `401 Unauthorized`: Encabezado JWT ausente.
  - `403 Forbidden`: Token JWT inválido o con permisos insuficientes.

**Ejemplo cURL**:
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <tu_token_jwt>" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Teclado RGB",
    "descripcion": "Teclado mecánico",
    "precio": 89.99,
    "stock": 20,
    "categoria": "Periféricos",
    "imagenUrl": "https://example.com/teclado.jpg"
  }'
```

#### `PUT /api/products/{id}`
Actualiza la información de un producto existente.

- **Acceso**: Protegido (`ROLE_ADMIN`)
- **Headers**: `Authorization: Bearer <token>`
- **Cuerpo HTTP**: `ProductRequest`
- **Respuestas**:
  - `200 OK`: Producto actualizado (`ProductDTO`).
  - `400 Bad Request`: Datos inválidos.
  - `401 Unauthorized` / `403 Forbidden`: Error de autenticación.
  - `404 Not Found`: Si el ID de producto no existe.

**Ejemplo cURL**:
```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer <tu_token_jwt>" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Teclado RGB Pro",
    "descripcion": "Teclado mecánico switches red",
    "precio": 99.99,
    "stock": 25,
    "categoria": "Periféricos",
    "imagenUrl": "https://example.com/teclado-pro.jpg"
  }'
```

#### `DELETE /api/products/{id}`
Elimina un producto del catálogo.

- **Acceso**: Protegido (`ROLE_ADMIN`)
- **Headers**: `Authorization: Bearer <token>`
- **Respuestas**:
  - `204 No Content`: Eliminación exitosa.
  - `401 Unauthorized` / `403 Forbidden`: Error de autenticación.
  - `404 Not Found`: Si el producto no existe.

**Ejemplo cURL**:
```bash
curl -X DELETE http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer <tu_token_jwt>"
```

---

### 3. Carrito de Compras (`/api/cart`)

El carrito está vinculado a la sesión HTTP del cliente. 

> **Protección CSRF (Cross-Site Request Forgery)**:
> Por configuración de seguridad en `SecurityConfig`, las solicitudes de modificación (`POST`, `PUT`, `DELETE`) sobre `/api/cart/**` requieren la cookie de sesión `XSRF-TOKEN` (emitida en respuestas `GET`) y la cabecera HTTP `X-XSRF-TOKEN`. Si una mutación no reenvía esta cabecera, el servidor responderá con HTTP `403 Forbidden`.

#### `GET /api/cart`
Obtiene el resumen actual del carrito de compras y emite la cookie `XSRF-TOKEN`.

- **Acceso**: Público
- **Respuestas**:
  - `200 OK`: Objeto `CartSummaryDTO`.

**Ejemplo cURL (Obtener cookie XSRF-TOKEN)**:
```bash
curl -i -c cookies.txt -X GET http://localhost:8080/api/cart
```

#### `POST /api/cart/add`
Agrega un producto al carrito o incrementa su cantidad.

- **Acceso**: Público (Requiere token CSRF)
- **Headers**: `Content-Type: application/json`, `X-XSRF-TOKEN: <valor_cookie_XSRF-TOKEN>`
- **Cuerpo HTTP**: `AddToCartRequest`
- **Respuestas**:
  - `200 OK`: Resumen del carrito actualizado (`CartSummaryDTO`).
  - `400 Bad Request`: Si la cantidad solicitada supera el stock disponible o es `<= 0`.
  - `403 Forbidden`: Token CSRF ausente o inválido.
  - `404 Not Found`: Si el `productId` no existe.

**Ejemplo cURL**:
```bash
# Extrar token XSRF-TOKEN guardado en cookies.txt:
# XSRF_TOKEN=$(grep XSRF-TOKEN cookies.txt | awk '{print $7}')

curl -X POST http://localhost:8080/api/cart/add \
  -b cookies.txt \
  -H "X-XSRF-TOKEN: $XSRF_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "cantidad": 2}'
```

#### `PUT /api/cart/update`
Actualiza la cantidad específica de un ítem en el carrito.

- **Acceso**: Público (Requiere token CSRF)
- **Headers**: `Content-Type: application/json`, `X-XSRF-TOKEN: <valor_cookie_XSRF-TOKEN>`
- **Cuerpo HTTP**: `UpdateCartRequest`
- **Respuestas**:
  - `200 OK`: `CartSummaryDTO`.
  - `400 Bad Request`: Si supera el stock o la cantidad es inválida.
  - `403 Forbidden`: Token CSRF ausente.

**Ejemplo cURL**:
```bash
curl -X PUT http://localhost:8080/api/cart/update \
  -b cookies.txt \
  -H "X-XSRF-TOKEN: $XSRF_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "cantidad": 5}'
```

#### `DELETE /api/cart/items/{productId}`
Remueve un producto específico del carrito.

- **Acceso**: Público (Requiere token CSRF)
- **Headers**: `X-XSRF-TOKEN: <valor_cookie_XSRF-TOKEN>`
- **Respuestas**:
  - `200 OK`: `CartSummaryDTO` actualizado.
  - `403 Forbidden`: Token CSRF ausente.

**Ejemplo cURL**:
```bash
curl -X DELETE http://localhost:8080/api/cart/items/1 \
  -b cookies.txt \
  -H "X-XSRF-TOKEN: $XSRF_TOKEN"
```

#### `POST /api/cart/checkout`
Procesa la compra de los artículos presentes en el carrito, reduciendo el stock correspondiente y vaciando el carrito.

- **Acceso**: Público (Requiere token CSRF)
- **Headers**: `X-XSRF-TOKEN: <valor_cookie_XSRF-TOKEN>`
- **Respuestas**:
  - `200 OK`: `CartSummaryDTO` resultante de la transacción.
  - `400 Bad Request`: Carrito vacío o stock insuficiente.
  - `403 Forbidden`: Token CSRF ausente.
  - `409 Conflict`: Conflicto de datos / concurrencia en la actualización de stock.

**Ejemplo cURL**:
```bash
curl -X POST http://localhost:8080/api/cart/checkout \
  -b cookies.txt \
  -H "X-XSRF-TOKEN: $XSRF_TOKEN"
```

#### `DELETE /api/cart`
Vacía completamente el carrito de compras.

- **Acceso**: Público (Requiere token CSRF)
- **Headers**: `X-XSRF-TOKEN: <valor_cookie_XSRF-TOKEN>`
- **Respuestas**:
  - `204 No Content`: Carrito vaciado.
  - `403 Forbidden`: Token CSRF ausente.

**Ejemplo cURL**:
```bash
curl -X DELETE http://localhost:8080/api/cart \
  -b cookies.txt \
  -H "X-XSRF-TOKEN: $XSRF_TOKEN"
```

---

## 🚦 Matriz de Códigos de Estado HTTP

| Código | Estado | Descripción |
| :---: | :--- | :--- |
| **200** | `OK` | La solicitud fue procesada correctamente. |
| **201** | `Created` | El recurso (producto) fue creado exitosamente. |
| **204** | `No Content` | Solicitud procesada con éxito sin cuerpo de respuesta. |
| **400** | `Bad Request` | Error de validación en la solicitud (campos faltantes, stock excedido). |
| **401** | `Unauthorized` | Requiere autenticación JWT o credenciales inválidas. |
| **403** | `Forbidden` | No posee los permisos o roles requeridos (`ROLE_ADMIN`). |
| **404** | `Not Found` | El recurso solicitado no existe. |
| **409** | `Conflict` | Conflicto de integridad o concurrencia de stock. |
| **500** | `Internal Server Error` | Excepción no controlada en el servidor. |
