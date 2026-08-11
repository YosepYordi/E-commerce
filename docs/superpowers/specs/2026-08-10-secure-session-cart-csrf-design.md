# Diseño: protección CSRF para el carrito con sesión

## Contexto

El carrito de compras se identifica mediante `HttpSession` y expone operaciones de cambio de estado en `/api/cart/**`. La configuración actual desactiva CSRF para toda la aplicación, por lo que una página externa podría provocar cambios usando la cookie de sesión del navegador.

## Objetivos

- Exigir un token CSRF para las operaciones mutantes del carrito.
- Mantener públicas las lecturas del catálogo y del carrito.
- Mantener las escrituras de productos protegidas por el encabezado JWT y el rol `ADMIN`.
- Permitir que el frontend estático obtenga y envíe el token CSRF.
- Denegar rutas no declaradas por la configuración de seguridad.

## Diseño aprobado

1. Usar `CookieCsrfTokenRepository` con la cookie `XSRF-TOKEN` y el encabezado `X-XSRF-TOKEN`.
2. Ignorar CSRF únicamente en login y en `/api/products/**`, porque esas operaciones usan credenciales explícitas y no dependen de una cookie de autenticación.
3. Mantener `/api/cart/**` público a nivel de autenticación, pero protegido contra falsificación de solicitudes.
4. Añadir al frontend una función pequeña para leer la cookie y adjuntar el encabezado en `POST`, `PUT` y `DELETE` del carrito.
5. Cambiar la regla final de autorización a `denyAll()`.
6. Añadir pruebas de integración para el rechazo sin CSRF, aceptación con CSRF, publicación de la cookie y conservación de la escritura de productos con JWT.

## Fuera de alcance

- No cambiar el modelo de autenticación JWT.
- No modificar el comportamiento de los PR #7 y #8.
- No hacer merge ni aprobar el PR #6.
