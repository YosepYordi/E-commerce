## 📌 Descripción de los Cambios
<!-- Proporciona un resumen claro y conciso de los cambios realizados y la motivación detrás de ellos. -->

- **Responsable**: `@kiba450parodi-ux` (o usuario correspondiente)
- **Rama origen**: `qa/docs-ci-kiba` -> **Rama destino**: `main`
- **Ticket / Issue abordado**: Fixes #5 (Fase 5 - QA, Postman, documentación y CI)
- **Módulo(s) afectado(s)**: `docs`, `ci`, `postman`, `README`, `CONTRIBUTING`

---

## 🏷️ Tipo de Cambio
<!-- Marca con una [x] la opción que aplique -->

- [ ] 🐛 **Bugfix** (Cambio que corrige un problema o fallo en el código)
- [ ] ✨ **Nueva Funcionalidad** (Añade una característica sin romper la compatibilidad)
- [ ] ♻️ **Refactorización** (Mejora del código existente sin cambiar comportamiento externo)
- [ ] 📝 **Documentación** (Actualización de README, CONTRIBUTING, API docs o diagramas)
- [ ] 🧪 **Pruebas** (Añadido o actualización de tests unitarios, checklist de navegador o colección Postman)
- [ ] ⚙️ **CI/CD / Configuración** (Ajustes en GitHub Actions, Maven, pom.xml o Docker)

---

## 🖼️ Evidencias / Capturas de Pantalla
<!-- Adjunta capturas de pantalla de la colección de Postman ejecutada y el checklist de navegador. -->

| Escenario / Prueba | Evidencia / Captura / Log |
| :--- | :--- |
| **Ejecución Colección Postman** | *(Adjuntar captura de runner de Postman o Newman)* |
| **Prueba en Navegador (UI)** | *(Adjuntar captura de catálogo / carrito / checkout)* |
| **Pipeline GitHub Actions CI** | *(Adjuntar captura o enlace al Run con checks en verde)* |

---

## 🧪 Pruebas Realizadas

- [ ] **Pruebas Unitarias / Integración Spring**: Se ejecutó `.\mvnw.cmd test` obteniendo `BUILD SUCCESS` (44/44 tests pasando).
- [ ] **Sintaxis JavaScript**: Se ejecutó `node --check src/main/resources/static/js/app.js`.
- [ ] **Pruebas API con Postman**: Se ejecutó la colección `ecommerce.postman_collection.json` validando login JWT, catálogo, CRUD y códigos 400, 401, 403, 404 y 409.
- [ ] **Checklist de Navegador**: Se completó la matriz de pruebas en `docs/testing/browser-checklist.md`.

---

## ✅ Checklist de Validación Final

Antes de solicitar la revisión de este Pull Request, confirma que has completado los siguientes pasos:

- [ ] Mi código y documentación siguen la guía de estilo del proyecto.
- [ ] He realizado una auto-revisión meticulosa de mis cambios.
- [ ] `pom.xml` utiliza la versión oficial estable de Spring Boot (3.4.0) resolviendo descargas en Maven Central.
- [ ] Se actualizaron `README.md`, `CONTRIBUTING.md` y `docs/api.md` con la información del proyecto desde cero.
- [ ] El pipeline de Integración Continua (GitHub Actions) en Java 21 con H2 y MySQL 8.4 pasa en **verde**.
