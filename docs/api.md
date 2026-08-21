# 📖 Especificación de la API REST (`docs/api.md`)

Esta documentación especifica los endpoints RESTful expuestos por la aplicación Spring Boot para el catálogo de productos y la gestión del carrito de compras.

- **Base URL**: `http://localhost:8080`
- **Formato de intercambio**: `JSON` (`application/json`)
- **Manejo de Sesión**: La API del carrito utiliza la cookie de sesión HTTP `JSESSIONID` manejada automáticamente por navegadores y clientes HTTP como Postman.

---

## 🛍️ Módulo de Productos (`ProductController`)

### 1. Obtener Lista de Productos
Retorna todos los productos del catálogo. Permite filtrar opcionalmente por categoría o término de búsqueda.

- **URL**: `/api/products`
- **Método HTTP**: `GET`
- **Parámetros Query**:
  - `categoria` *(opcional, string)*: Nombre exacto o parcial de la categoría (ej. `Peluches`).
  - `query` *(opcional, string)*: Palabra clave para buscar en el nombre del producto (ej. `Oso`).

#### Request Example
`GET /api/products?categoria=Peluches&query=Oso`

#### Response JSON (`200 OK`)
```json
[
  {
    "id": 1,
    "nombre": "Oso de Peluche Gigante Huggy",
    "descripcion": "Peluche supersuave de 80 cm, hipoalergénico e ideal para abrazos.",
    "precio": 89.90,
    "stock": 25,
    "categoria": "Peluches",
    "imagenUrl": "https://images.unsplash.com/photo-1559454403-b8fb88521f11?w=500&auto=format&fit=crop"
  }
]
```

#### Códigos HTTP Esperados
- `200 OK`: Lista retornada correctamente (puede ser un arreglo vacío si no hay coincidencias).
- `500 Internal Server Error`: Error interno del servidor.

---

### 2. Obtener Producto por ID
Obtiene los detalles completos de un producto específico mediante su identificador.

- **URL**: `/api/products/{id}`
- **Método HTTP**: `GET`
- **Parámetros Path**:
  - `id` *(obligatorio, Long)*: Identificador único del producto (ej. `1`).

#### Request Example
`GET /api/products/1`

#### Response JSON (`200 OK`)
```json
{
  "id": 1,
  "nombre": "Oso de Peluche Gigante Huggy",
  "descripcion": "Peluche supersuave de 80 cm, hipoalergénico e ideal para abrazos.",
  "precio": 89.90,
  "stock": 25,
  "categoria": "Peluches",
  "imagenUrl": "https://images.unsplash.com/photo-1559454403-b8fb88521f11?w=500&auto=format&fit=crop"
}
```

#### Response JSON (`404 Not Found`)
```json
{
  "timestamp": "2026-08-19T13:30:00.000",
  "status": 404,
  "error": "Not Found",
  "message": "Producto no encontrado con ID: 999"
}
```

#### Códigos HTTP Esperados
- `200 OK`: Producto encontrado exitosamente.
- `404 Not Found`: El producto solicitado no existe en la base de datos.

---

### 3. Crear Producto
Registra un nuevo producto en el catálogo.

- **URL**: `/api/products`
- **Método HTTP**: `POST`
- **Headers**: `Content-Type: application/json`

#### Request JSON
```json
{
  "nombre": "Pista de Carreras Gran Turismo",
  "descripcion": "Pista eléctrica de 3 metros con dos controles.",
  "precio": 119.90,
  "stock": 12,
  "categoria": "Vehículos",
  "imagenUrl": "https://images.unsplash.com/photo-1594787318286-3d835c1d207f?w=500&auto=format&fit=crop"
}
```

#### Response JSON (`201 Created`)
```json
{
  "id": 7,
  "nombre": "Pista de Carreras Gran Turismo",
  "descripcion": "Pista eléctrica de 3 metros con dos controles.",
  "precio": 119.90,
  "stock": 12,
  "categoria": "Vehículos",
  "imagenUrl": "https://images.unsplash.com/photo-1594787318286-3d835c1d207f?w=500&auto=format&fit=crop"
}
```

#### Response JSON (`400 Bad Request`)
```json
{
  "timestamp": "2026-08-19T13:30:00.000",
  "status": 400,
  "error": "Bad Request",
  "message": "El nombre del producto es obligatorio"
}
```

#### Códigos HTTP Esperados
- `201 Created`: Producto creado con éxito.
- `400 Bad Request`: Datos de producto inválidos (nombre vacío, precio o stock negativo).

---

### 4. Actualizar Producto
Modifica la información existente de un producto por su ID.

- **URL**: `/api/products/{id}`
- **Método HTTP**: `PUT`
- **Parámetros Path**: `id` *(Long)*

#### Request JSON
```json
{
  "nombre": "Oso de Peluche Gigante Huggy Deluxe",
  "descripcion": "Edición especial con lazo rojo y pelaje premium.",
  "precio": 99.90,
  "stock": 30,
  "categoria": "Peluches",
  "imagenUrl": "https://images.unsplash.com/photo-1559454403-b8fb88521f11?w=500&auto=format&fit=crop"
}
```

#### Response JSON (`200 OK`)
```json
{
  "id": 1,
  "nombre": "Oso de Peluche Gigante Huggy Deluxe",
  "descripcion": "Edición especial con lazo rojo y pelaje premium.",
  "precio": 99.90,
  "stock": 30,
  "categoria": "Peluches",
  "imagenUrl": "https://images.unsplash.com/photo-1559454403-b8fb88521f11?w=500&auto=format&fit=crop"
}
```

#### Códigos HTTP Esperados
- `200 OK`: Producto actualizado exitosamente.
- `400 Bad Request`: Datos inválidos en la petición.
- `404 Not Found`: No existe el producto con el ID especificado.

---

### 5. Eliminar Producto
Elimina un producto del catálogo.

- **URL**: `/api/products/{id}`
- **Método HTTP**: `DELETE`
- **Parámetros Path**: `id` *(Long)*

#### Response Body (`204 No Content`)
*(Sin contenido en la respuesta)*

#### Códigos HTTP Esperados
- `204 No Content`: Eliminación completada correctamente.
- `404 Not Found`: Producto no encontrado.

---

## 🛒 Módulo del Carrito (`CartController`)

### 1. Obtener Carrito Actual
Consulta el estado actual del carrito de compras asociado a la sesión HTTP.

- **URL**: `/api/cart`
- **Método HTTP**: `GET`

#### Response JSON (`200 OK`)
```json
{
  "items": [
    {
      "product": {
        "id": 1,
        "nombre": "Oso de Peluche Gigante Huggy",
        "descripcion": "Peluche supersuave de 80 cm, hipoalergénico e ideal para abrazos.",
        "precio": 89.90,
        "stock": 25,
        "categoria": "Peluches",
        "imagenUrl": "https://images.unsplash.com/photo-1559454403-b8fb88521f11?w=500&auto=format&fit=crop"
      },
      "cantidad": 2,
      "subtotal": 179.80
    }
  ],
  "total": 179.80,
  "totalItems": 2
}
```

#### Códigos HTTP Esperados
- `200 OK`: Resumen del carrito retornado exitosamente (si está vacío, `items` es `[]`, `total` es `0` y `totalItems` es `0`).

---

### 2. Agregar Producto al Carrito
Agrega una cantidad especificada de un producto al carrito activo de la sesión.

- **URL**: `/api/cart/add`
- **Método HTTP**: `POST`
- **Headers**: `Content-Type: application/json`

#### Request JSON
```json
{
  "productId": 1,
  "cantidad": 2
}
```

#### Response JSON (`200 OK`)
```json
{
  "items": [
    {
      "product": {
        "id": 1,
        "nombre": "Oso de Peluche Gigante Huggy",
        "descripcion": "Peluche supersuave de 80 cm, hipoalergénico e ideal para abrazos.",
        "precio": 89.90,
        "stock": 25,
        "categoria": "Peluches",
        "imagenUrl": "https://images.unsplash.com/photo-1559454403-b8fb88521f11?w=500&auto=format&fit=crop"
      },
      "cantidad": 2,
      "subtotal": 179.80
    }
  ],
  "total": 179.80,
  "totalItems": 2
}
```

#### Response JSON (`400 Bad Request`)
```json
{
  "timestamp": "2026-08-19T13:30:00.000",
  "status": 400,
  "error": "Bad Request",
  "message": "La cantidad debe ser mayor a 0"
}
```

#### Códigos HTTP Esperados
- `200 OK`: Producto agregado o cantidad incrementada correctamente.
- `400 Bad Request`: `productId` nulo, cantidad <= 0 o stock insuficiente.
- `404 Not Found`: El `productId` no corresponde a ningún producto existente.

---

### 3. Actualizar Cantidad en Carrito
Establece la cantidad exacta de un producto en el carrito. Si la cantidad enviada es `0`, el item se elimina automáticamente.

- **URL**: `/api/cart/update`
- **Método HTTP**: `PUT`
- **Headers**: `Content-Type: application/json`

#### Request JSON
```json
{
  "productId": 1,
  "cantidad": 5
}
```

#### Response JSON (`200 OK`)
```json
{
  "items": [
    {
      "product": {
        "id": 1,
        "nombre": "Oso de Peluche Gigante Huggy",
        "descripcion": "Peluche supersuave de 80 cm, hipoalergénico e ideal para abrazos.",
        "precio": 89.90,
        "stock": 25,
        "categoria": "Peluches",
        "imagenUrl": "https://images.unsplash.com/photo-1559454403-b8fb88521f11?w=500&auto=format&fit=crop"
      },
      "cantidad": 5,
      "subtotal": 449.50
    }
  ],
  "total": 449.50,
  "totalItems": 5
}
```

#### Códigos HTTP Esperados
- `200 OK`: Cantidad actualizada exitosamente.
- `400 Bad Request`: `productId` nulo o la cantidad solicitada excede el stock disponible.
- `404 Not Found`: Producto no existente en la base de datos.

---

### 4. Eliminar Item Específico del Carrito
Remueve por completo un producto del carrito actual sin importar su cantidad.

- **URL**: `/api/cart/items/{productId}`
- **Método HTTP**: `DELETE`
- **Parámetros Path**: `productId` *(Long)*

#### Response JSON (`200 OK`)
```json
{
  "items": [],
  "total": 0.00,
  "totalItems": 0
}
```

#### Códigos HTTP Esperados
- `200 OK`: Item removido y carrito actualizado retornado.

---

### 5. Vaciar Carrito Completo
Elimina todos los elementos guardados en la sesión del carrito.

- **URL**: `/api/cart`
- **Método HTTP**: `DELETE`

#### Response Body (`204 No Content`)
*(Sin contenido)*

#### Códigos HTTP Esperados
- `204 No Content`: Carrito vaciado exitosamente.

---

### 6. Procesar Compra (Checkout)
Procesa la compra del carrito actual, descuenta el stock de los productos en la base de datos y vacía la sesión.

- **URL**: `/api/cart/checkout`
- **Método HTTP**: `POST`

#### Response JSON (`200 OK`)
```json
{
  "items": [
    {
      "product": {
        "id": 1,
        "nombre": "Oso de Peluche Gigante Huggy",
        "descripcion": "Peluche supersuave de 80 cm, hipoalergénico e ideal para abrazos.",
        "precio": 89.90,
        "stock": 23,
        "categoria": "Peluches",
        "imagenUrl": "https://images.unsplash.com/photo-1559454403-b8fb88521f11?w=500&auto=format&fit=crop"
      },
      "cantidad": 2,
      "subtotal": 179.80
    }
  ],
  "total": 179.80,
  "totalItems": 2
}
```

#### Response JSON (`400 Bad Request`)
```json
{
  "timestamp": "2026-08-19T13:30:00.000",
  "status": 400,
  "error": "Bad Request",
  "message": "El carrito está vacío, no se puede procesar la compra."
}
```

#### Códigos HTTP Esperados
- `200 OK`: Compra procesada correctamente y stock actualizado en base de datos.
- `400 Bad Request`: Carrito vacío o stock insuficiente al momento del checkout.
