# Diseño: API REST de productos

## Contexto

La rama `main` ya contiene un CRUD inicial de productos, pero el Issue #2
requiere separar las entradas de escritura de las respuestas, validar los
datos, conservar los filtros existentes y cubrir el comportamiento con
pruebas. La configuración JWT pertenece a la rama base de otra tarea y no se
duplicará aquí.

## Alcance

- GET `/api/products` y GET `/api/products/{id}` permanecen públicos.
- POST `/api/products`, PUT `/api/products/{id}` y DELETE `/api/products/{id}`
  quedan preparados para la autorización ADMIN de la configuración JWT del
  proyecto, sin modificar esa configuración en esta rama.
- POST y PUT reciben `ProductRequest`; el `id` no forma parte de la entrada.
- Las respuestas continúan usando `ProductDTO`.
- Se mantienen los filtros opcionales `categoria` y `query`.

## Validaciones

- `nombre`: obligatorio y de hasta 120 caracteres.
- `descripcion`: opcional y de hasta 1000 caracteres.
- `precio`: obligatorio y mayor o igual que cero.
- `stock`: obligatorio y mayor o igual que cero.
- `categoria`: obligatorio.
- `imagenUrl`: opcional, pero debe ser una URL válida cuando se envíe.

La validación de entrada ocurrirá en el controlador con `@Valid`. Los errores
de validación se convertirán al formato común del proyecto. Las reglas de
dominio que también protegen llamadas internas permanecerán en el servicio.

## Arquitectura y flujo

El controlador valida y traduce la entrada; el servicio aplica las reglas de
negocio y mapea entre `ProductRequest`, `Product` y `ProductDTO`; el repositorio
mantiene las consultas por categoría y nombre. La creación siempre descarta
cualquier identificador que pudiera llegar por reflexión o compatibilidad y
persiste una entidad nueva. La actualización busca primero el producto y
modifica únicamente sus campos editables.

## Errores

- Producto inexistente: HTTP 404.
- Entrada inválida: HTTP 400.
- El cuerpo de error conserva `timestamp`, `status`, `error`, `message` y
  añade `path` para cumplir el contrato del proyecto.

## Pruebas

Se agregarán pruebas de servicio y controlador para:

- listar, filtrar por categoría y buscar por nombre;
- obtener un producto existente y rechazar uno inexistente;
- crear sin aceptar `id`;
- actualizar y eliminar;
- rechazar campos inválidos;
- verificar los códigos HTTP y el formato de error.

Las pruebas se ejecutarán con H2 y no dependerán de MySQL ni de la rama de
seguridad JWT.
