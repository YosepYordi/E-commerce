# 📬 Guía de Ejecución de Colección Postman (`docs/testing/postman/README.md`)

Este directorio contiene la colección oficial de pruebas de API para el proyecto **E-commerce**: [`Ecommerce.postman_collection.json`](file:///d:/trabajo%20antigravity%20ide/E-commerce/docs/testing/postman/Ecommerce.postman_collection.json).

---

## 📌 Pruebas Incluidas en la Colección

La colección valida el comportamiento de la API REST tanto para casos de éxito (200 OK) como para manejo de errores de cliente (400 Bad Request) y recursos inexistentes (404 Not Found):

1. **Obtener productos** (`GET /api/products`)
2. **Obtener producto inexistente** (`GET /api/products/999`)
3. **Agregar al carrito** (`POST /api/cart/add`)
4. **Actualizar cantidad** (`PUT /api/cart/update`)
5. **Validar cantidades negativas** (`POST /api/cart/add` con `cantidad: -1`)
6. **Validar producto inexistente** (`POST /api/cart/add` con `productId: 999`)

---

## 🧪 Afirmaciones (Asserts) en Scripts de Test

Cada solicitud HTTP incluye un script automatizado en la pestaña **Tests** que ejecuta tres categorías de comprobaciones:

```javascript
// 1. Validación de Código de Estado HTTP
pm.test("Status code is 200 OK", function () {
    pm.response.to.have.status(200);
});

// 2. Validación de Tiempo de Respuesta (<2000ms)
pm.test("Response time is less than 2000ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(2000);
});

// 3. Validación de Estructura de Payload JSON
pm.test("Cart summary structure is valid", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property("items");
    pm.expect(jsonData).to.have.property("total");
    pm.expect(jsonData).to.have.property("totalItems");
});
```

---

## 🚀 Cómo Ejecutar la Colección

### Opción A: Desde la Aplicación Gráfica (Postman Desktop App)

1. Abre la aplicación **Postman**.
2. Haz clic en el botón **Import** (esquina superior izquierda).
3. Selecciona o arrastra el archivo [`Ecommerce.postman_collection.json`](file:///d:/trabajo%20antigravity%20ide/E-commerce/docs/testing/postman/Ecommerce.postman_collection.json).
4. Asegúrate de que tu servidor Spring Boot esté ejecutándose localmente (`http://localhost:8080`).
5. Abre la colección importada **Ecommerce API Test Collection**.
6. Haz clic derecho sobre el nombre de la colección y selecciona **Run collection**.
7. Presiona el botón **Run Ecommerce API Test Collection**.
8. Observa los resultados del reporte (todas las pruebas deben marcar en **Pass**).

---

### Opción B: Desde la Línea de Comandos (Newman CLI)

**Newman** es el ejecutor de colecciones de Postman para terminal e integración continua (CI/CD).

1. Instala Newman globalmente o utilízalo con `npx`:
   ```bash
   npx newman run docs/testing/postman/Ecommerce.postman_collection.json
   ```

2. Para personalizar la variable `baseUrl` en un entorno distinto a `http://localhost:8080`:
   ```bash
   npx newman run docs/testing/postman/Ecommerce.postman_collection.json --env-var "baseUrl=http://localhost:8081"
   ```

3. Ejemplo de Salida Esperada:
   ```text
   ┌─────────────────────────┬──────────┬──────────┐
   │                         │ executed │   failed │
   ├─────────────────────────┼──────────┼──────────┤
   │ me.iterations           │        1 │        0 │
   │ me.requests             │        6 │        0 │
   │ me.testScripts          │        6 │        0 │
   │ me.prerequestScripts    │        0 │        0 │
   │ me.assertions           │       18 │        0 │
   └─────────────────────────┴──────────┴──────────┘
   ```
