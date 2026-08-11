# Products API Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Completar la API REST de productos con entradas validadas, CRUD probado, filtros existentes y errores HTTP consistentes.

**Architecture:** El controlador recibe `ProductRequest` y devuelve `ProductDTO`; el servicio conserva las reglas de negocio y mapea entre DTO y entidad; el repositorio mantiene consultas derivadas para categoría y nombre. La autorización JWT queda fuera de esta rama y será consumida por la configuración de seguridad del proyecto cuando se integre.

**Tech Stack:** Java 21, Spring Boot 4.1, Spring MVC, Spring Data JPA, Jakarta Bean Validation, H2 y Maven Wrapper.

## Global Constraints

- GET `/api/products` y GET `/api/products/{id}` permanecen públicos.
- POST, PUT y DELETE deben quedar separados de las lecturas para que la rama JWT los proteja como ADMIN al integrarse.
- La entrada de POST/PUT no acepta `id`.
- Las pruebas usan H2 y no dependen de MySQL.
- No modificar la configuración JWT ni el frontend.

---

### Task 1: Add request validation contract

**Files:**
- Modify: `pom.xml`
- Create: `src/main/java/com/example/demo/dto/ProductRequest.java`
- Test: `src/test/java/com/example/demo/dto/ProductRequestValidationTest.java`

**Interfaces:**
- Produces `ProductRequest` with getters/setters for `nombre`, `descripcion`, `precio`, `stock`, `categoria` and `imagenUrl`, without an `id` field.
- Validation annotations produce violations for blank/oversized names, oversized descriptions, null/negative prices, null/negative stock, blank categories and malformed image URLs.

- [ ] **Step 1: Write the failing validation test**

Create a Jakarta Validator test that builds an invalid `ProductRequest` and asserts the expected property names are present in the violations. Add a valid request case with zero price and stock and a valid HTTPS image URL.

- [ ] **Step 2: Run the test to verify it fails**

Run: `./mvnw.cmd -Dtest=ProductRequestValidationTest test`

Expected: compilation or test failure because `ProductRequest` does not exist yet.

- [ ] **Step 3: Add validation dependency and minimal request class**

Add `spring-boot-starter-validation` to `pom.xml`. Define `ProductRequest` with `@NotBlank`, `@Size`, `@NotNull`, `@DecimalMin(value = "0.0")`, `@Min(0)` and an optional standard `@Pattern` constraint for `imagenUrl` that accepts HTTP and HTTPS URLs.

- [ ] **Step 4: Run the validation test to verify it passes**

Run: `./mvnw.cmd -Dtest=ProductRequestValidationTest test`

Expected: PASS.

- [ ] **Step 5: Commit**

```powershell
git add pom.xml src/main/java/com/example/demo/dto/ProductRequest.java src/test/java/com/example/demo/dto/ProductRequestValidationTest.java
git commit -m "feat: add validated product request"
```

### Task 2: Complete service behavior

**Files:**
- Modify: `src/main/java/com/example/demo/service/ProductService.java`
- Modify: `src/main/java/com/example/demo/service/impl/ProductServiceImpl.java`
- Test: `src/test/java/com/example/demo/service/ProductServiceImplTest.java`

**Interfaces:**
- `ProductService.createProduct(ProductRequest request)` returns `ProductDTO`.
- `ProductService.updateProduct(Long id, ProductRequest request)` returns `ProductDTO`.
- Existing read and delete signatures remain unchanged.

- [ ] **Step 1: Write failing service tests**

Test that creation maps a request to a new entity with a null id, update preserves the requested path id while replacing editable fields, missing ids throw `ResourceNotFoundException`, filters call the corresponding repository queries, and deletion rejects missing ids.

- [ ] **Step 2: Run service tests to verify the expected failures**

Run: `./mvnw.cmd -Dtest=ProductServiceImplTest test`

Expected: compilation failure because service methods still receive `ProductDTO`.

- [ ] **Step 3: Implement minimal request mapping and domain rules**

Change write signatures to `ProductRequest`, map only request fields, remove the BigDecimal `doubleValue()` comparison, use `compareTo(BigDecimal.ZERO)`, and retain not-found behavior. Keep read filtering behavior unchanged.

- [ ] **Step 4: Run service tests to verify they pass**

Run: `./mvnw.cmd -Dtest=ProductServiceImplTest test`

Expected: PASS.

- [ ] **Step 5: Commit**

```powershell
git add src/main/java/com/example/demo/service/ProductService.java src/main/java/com/example/demo/service/impl/ProductServiceImpl.java src/test/java/com/example/demo/service/ProductServiceImplTest.java
git commit -m "feat: complete product service CRUD"
```

### Task 3: Wire controller validation and error paths

**Files:**
- Modify: `src/main/java/com/example/demo/controller/ProductController.java`
- Modify: `src/main/java/com/example/demo/exception/GlobalExceptionHandler.java`
- Test: `src/test/java/com/example/demo/controller/ProductControllerTest.java`

**Interfaces:**
- POST and PUT accept `@Valid ProductRequest` and delegate to the request-based service methods.
- GET endpoints retain their current response shapes and optional `categoria`/`query` parameters.
- Validation errors return HTTP 400 with `timestamp`, `status`, `error`, `message` and `path`.

- [ ] **Step 1: Write failing MVC tests**

Cover GET list, GET by id, POST 201, PUT 200, DELETE 204, invalid POST 400, missing product 404, and verify that the controller does not expose an id field in the write contract.

- [ ] **Step 2: Run MVC tests to verify failures**

Run: `./mvnw.cmd -Dtest=ProductControllerTest test`

Expected: compilation failure until the controller accepts `ProductRequest` and validation errors are handled.

- [ ] **Step 3: Implement controller and error handling**

Add `@Valid` to POST/PUT bodies, update service calls, add a `MethodArgumentNotValidException` handler, and obtain the request path from `HttpServletRequest` for all handled error responses.

- [ ] **Step 4: Run MVC tests to verify they pass**

Run: `./mvnw.cmd -Dtest=ProductControllerTest test`

Expected: PASS.

- [ ] **Step 5: Commit**

```powershell
git add src/main/java/com/example/demo/controller/ProductController.java src/main/java/com/example/demo/exception/GlobalExceptionHandler.java src/test/java/com/example/demo/controller/ProductControllerTest.java
git commit -m "feat: validate product API requests"
```

### Task 4: Run the complete verification suite

**Files:**
- Modify only if required by test failures.

- [ ] **Step 1: Run all tests**

Run: `./mvnw.cmd test`

Expected: all tests pass with no compilation errors.

- [ ] **Step 2: Inspect the diff and branch state**

Run: `git diff origin/main...HEAD --stat; git status --short --branch`

Confirm that only the product API, validation dependency, tests and planning documentation changed; no JWT configuration or frontend files changed.

- [ ] **Step 3: Commit any required test-only correction**

```powershell
git add <files-that-were-corrected>
git commit -m "test: stabilize products API verification"
```
