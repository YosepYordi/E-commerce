# 🛒 E-commerce Spring Boot API & Web Application

Sistema de comercio electrónico completo desarrollado con **Java 21**, **Spring Boot 3**, **Spring Data JPA**, base de datos **H2** / **MySQL** y frontend estático interactivo en HTML5, CSS3 y JavaScript.

---

## 📋 Tabla de Contenidos

1. [Descripción del Proyecto](#-descripción-del-proyecto)
2. [Tecnologías Utilizadas](#%EF%B8%8F-tecnologías-utilizadas)
3. [Requisitos Previos](#-requisitos-previos)
4. [Instalación y Configuración](#-instalación-y-configuración)
   - [Variables de Entorno](#variables-de-entorno)
   - [Ejecución con Maven Wrapper](#ejecución-con-maven-wrapper)
   - [Ejecución con Docker Compose](#ejecución-con-docker-compose)
5. [Estructura del Proyecto](#-estructura-del-proyecto)
6. [Resumen de Endpoints API & JWT](#-resumen-de-endpoints-api--jwt)
7. [Ejecución de Pruebas](#-ejecución-de-pruebas)
   - [Pruebas Automatizadas (JUnit 5)](#pruebas-unitarias-y-de-integración)
   - [Pruebas de API con Postman](#colección-de-postman)
   - [Checklist de Navegador](#checklist-de-navegador)
8. [Flujo de Desarrollo y Pull Requests](#-flujo-de-desarrollo-y-pull-requests)

---

## 📝 Descripción del Proyecto

**E-commerce** es una solución de comercio electrónico orientada a la gestión eficiente de un catálogo de productos, autenticación segura de administradores mediante JWT y flujo dinámico de carrito de compras por sesión HTTP.

### Principales Funcionalidades:
- **Catálogo de Productos**: Consulta pública, filtrado dinámico por categorías y búsqueda textual por coincidencia parcial.
- **Administración Protegida (CRUD)**: Creación, modificación y eliminación de productos restringida por token JWT (`ROLE_ADMIN`).
- **Carrito de Compras por Sesión**: Agregar ítems, modificar cantidades, eliminar productos y cálculo en tiempo real de subtotales y totales.
- **Checkout & Validación de Stock**: Transacciones atómicas de compra con deducción de stock y prevención de sobreventas.
- **Manejo Centralizado de Excepciones**: Respuestas de error estructuradas (`ApiError`) con códigos HTTP adecuados (400, 401, 403, 404, 409).

---

## 🛠️ Tecnologías Utilizadas

- **Backend**: Java 21, Spring Boot 3.4.0, Spring Data JPA, Hibernate, Spring Security, JJWT (io.jsonwebtoken 0.12.6).
- **Base de Datos**: H2 Database (desarrollo/pruebas) y MySQL 8.4 (producción/smoke tests).
- **Frontend**: HTML5, CSS3 Vanilla, JavaScript ES6 (Fetch API).
- **Calidad & Automatización**: JUnit 5, Mockito, GitHub Actions CI, Postman.
- **Contenedores**: Docker & Docker Compose.

---

## 🔑 Requisitos Previos

- **Java JDK 21** o superior instalado.
- **Git** instalado.
- **Docker** & **Docker Compose** (opcional, para despliegue en contenedores).

Verificar instalación:
```bash
java -version
git --version
docker --version
```

---

## ⚙️ Instalación y Configuración

### 1. Clonar el Repositorio
```bash
git clone https://github.com/YosepYordi/E-commerce.git
cd E-commerce
```

### 2. Variables de Entorno
Copia el archivo `.env.example` para crear tu configuración local `.env`:
```bash
cp .env.example .env
```

Parámetros clave en `.env`:
```env
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=h2
JWT_SECRET_BASE64=MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=
JWT_EXPIRATION_SECONDS=3600
ADMIN_USERNAME=admin
ADMIN_PASSWORD_HASH=$2a$10$6iurHBvm3dPiZpMddjiWfenLe0HT.EDbln2WDlZ3aP.b2xFZy6SMS
```

### 3. Ejecución con Maven Wrapper
Para compilar e iniciar la aplicación localmente:

**En Linux / MacOS**:
```bash
./mvnw spring-boot:run
```

**En Windows (PowerShell / CMD)**:
```cmd
.\mvnw.cmd spring-boot:run
```

Una vez iniciada, abre tu navegador en: `http://localhost:8080`

### 4. Ejecución con Docker Compose
Para desplegar la aplicación junto con una base de datos MySQL 8.4:
```bash
docker-compose up -d --build
```
Para detener los contenedores:
```bash
docker-compose down
```

---

## 📂 Estructura del Proyecto

```text
E-commerce/
├── .github/
│   ├── PULL_REQUEST_TEMPLATE.md
│   └── workflows/
│       └── ci.yml
├── docs/
│   ├── api.md
│   └── testing/
│       ├── browser-checklist.md
│       └── postman/
│           ├── ecommerce.postman_collection.json
│           └── ecommerce.postman_environment.json
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   └── service/
│   │   └── resources/
│   │       ├── static/          # HTML, CSS, JS frontend
│   │       └── application.properties
│   └── test/
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## 🌐 Resumen de Endpoints API & JWT

Para ver la documentación completa y detallada, consulta [`docs/api.md`](file:///c:/Users/HP/E-commerce/docs/api.md).

| Método | Endpoint | Descripción | Autenticación |
| :---: | :--- | :--- | :---: |
| `POST` | `/api/auth/login` | Iniciar sesión y obtener JWT | Público |
| `GET` | `/api/products` | Listar catálogo (filtro por `categoria`, `query`) | Público |
| `GET` | `/api/products/{id}` | Detalle de producto por ID | Público |
| `POST` | `/api/products` | Crear nuevo producto | `Bearer JWT` (Admin) |
| `PUT` | `/api/products/{id}` | Actualizar producto existente | `Bearer JWT` (Admin) |
| `DELETE` | `/api/products/{id}` | Eliminar producto por ID | `Bearer JWT` (Admin) |
| `GET` | `/api/cart` | Obtener carrito de compras actual | Sesión HTTP |
| `POST` | `/api/cart/add` | Agregar producto al carrito | Sesión HTTP |
| `PUT` | `/api/cart/update` | Modificar cantidad de ítem en carrito | Sesión HTTP |
| `DELETE` | `/api/cart/items/{id}` | Eliminar ítem del carrito | Sesión HTTP |
| `POST` | `/api/cart/checkout` | Procesar checkout y deducir stock | Sesión HTTP |
| `DELETE` | `/api/cart` | Vaciar carrito de compras | Sesión HTTP |

---

## 🧪 Ejecución de Pruebas

### Pruebas Unitarias y de Integración
Ejecuta la suite completa de 44 pruebas con JUnit 5:
```bash
.\mvnw.cmd clean test
```

### Colección de Postman
La colección completa de pruebas automatizadas de la API se encuentra en:
- Colección: [`docs/testing/postman/ecommerce.postman_collection.json`](file:///c:/Users/HP/E-commerce/docs/testing/postman/ecommerce.postman_collection.json)
- Entorno: [`docs/testing/postman/ecommerce.postman_environment.json`](file:///c:/Users/HP/E-commerce/docs/testing/postman/ecommerce.postman_environment.json)

Puedes importarla en Postman o ejecutarla en consola con Newman:
```bash
newman run docs/testing/postman/ecommerce.postman_collection.json -e docs/testing/postman/ecommerce.postman_environment.json
```

### Checklist de Navegador
Revisa la matriz de pruebas manuales de UI en [`docs/testing/browser-checklist.md`](file:///c:/Users/HP/E-commerce/docs/testing/browser-checklist.md).

---

## 🔀 Flujo de Desarrollo y Pull Requests

1. Crea tu rama a partir de `main` siguiendo la convención:
   - `feature/nombre-funcionalidad`
   - `qa/docs-ci-nombre`
   - `bugfix/nombre-arreglo`
2. Asegúrate de que los tests locales pasen (`.\mvnw.cmd test`).
3. Verifica la sintaxis JavaScript (`node --check src/main/resources/static/js/app.js`).
4. Abre el Pull Request usando la plantilla estandarizada [`PULL_REQUEST_TEMPLATE.md`](file:///c:/Users/HP/E-commerce/.github/PULL_REQUEST_TEMPLATE.md).
5. Confirma que todos los checks de GitHub Actions CI queden en **verde** antes de solicitar revisión.