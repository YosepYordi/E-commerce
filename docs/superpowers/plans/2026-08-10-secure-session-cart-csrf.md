# Plan: proteger el carrito basado en sesión contra CSRF

## Objetivo

Corregir el hallazgo de seguridad del PR #6 sin cambiar su contrato JWT: las operaciones mutantes del carrito deben requerir CSRF, las operaciones administrativas de productos deben seguir requiriendo un JWT con rol `ADMIN` y las rutas no declaradas no deben quedar públicas.

## Alcance

- `src/main/java/com/example/demo/security/SecurityConfig.java`
- `src/main/resources/static/js/app.js`
- `src/test/java/com/example/demo/security/SecurityIntegrationTest.java`
- Documentación de diseño y este plan.

## Pasos de implementación

1. Añadir una prueba de regresión que reproduzca la vulnerabilidad: una petición mutante a `/api/cart/**` sin token CSRF debe devolver `403`.
2. Añadir pruebas de control: una operación del carrito con `csrf()` debe conservar su respuesta válida, la lectura del carrito debe publicar `XSRF-TOKEN` y una escritura de producto con JWT de administrador debe seguir devolviendo `201`.
3. Configurar `CookieCsrfTokenRepository` y un filtro pequeño que fuerce la materialización de la cookie en respuestas de lectura.
4. Ignorar CSRF solo para login y `/api/products/**`, y permitir explícitamente el encabezado `X-XSRF-TOKEN` en CORS.
5. Actualizar las peticiones mutantes del carrito en `app.js` para enviar el token de la cookie.
6. Reemplazar `anyRequest().permitAll()` por `anyRequest().denyAll()`.
7. Ejecutar primero las pruebas de seguridad, luego toda la suite Maven y `git diff --check`.
8. Revisar el diff final, confirmar que no hay cambios fuera del alcance, hacer commit y actualizar la rama del PR #6.

## Criterios de aceptación

- El exploit CSRF del carrito deja de reproducirse.
- Las operaciones legítimas del carrito siguen funcionando con token.
- El JWT de administrador conserva el acceso a la escritura de productos.
- El frontend envía el token requerido.
- Las pruebas relevantes pasan y el diff no contiene errores de whitespace.
