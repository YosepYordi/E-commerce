# 🤝 Guía de Contribución (CONTRIBUTING.md)

¡Gracias por contribuir al proyecto **E-commerce**! Este documento establece las pautas, flujo de trabajo de Git, convenciones de nombres y estándares de calidad requeridos para mantener la integridad del código fuente.

---

## 🌿 Flujo de Trabajo (GitFlow)

Utilizamos una adaptación estándar del modelo **GitFlow** para gestionar el desarrollo colaborativo:

```text
main (producción / código estable)
  │
  └── develop (rama principal de integración)
        │
        ├── feature/101-catalogo-filtro
        ├── bugfix/202-error-stock-checkout
        └── docs/303-actualizar-api
```

### Ramas Principales
- **`main`**: Contiene únicamente código probado y listo para producción. Todas las entregas oficiales provienen de esta rama.
- **`develop`**: Rama de integración continua donde se consolidan los desarrollos finalizados.

### Ramas de Soporte
- **`feature/*`**: Utilizadas para el desarrollo de nuevas características. Se originan en `develop` y se reincorporan a `develop` mediante un Pull Request (PR).
- **`bugfix/*`**: Utilizadas para corregir errores o fallos detectados durante el desarrollo o pruebas.
- **`hotfix/*`**: Correcciones críticas que deben aplicarse directamente sobre `main` y luego fusionarse con `develop`.
- **`docs/*`**: Creación o actualización de documentación, diagramas o instructivos.

---

## 🏷️ Convención para Nombres de Ramas

Los nombres de las ramas deben ser descriptivos, en minúsculas y usar guiones (`-`) para separar palabras. Se recomienda incluir el identificador de la tarea o ticket si aplica:

- **Características**: `feature/<id-tarea>-<descripcion-corta>`  
  *Ejemplo*: `feature/CART-12-agregar-descuentos`
- **Correcciones**: `bugfix/<id-tarea>-<descripcion-corta>`  
  *Ejemplo*: `bugfix/PROD-05-validar-precio-negativo`
- **Documentación**: `docs/<descripcion-corta>`  
  *Ejemplo*: `docs/actualizar-especificacion-api`
- **Refactorización / Pruebas**: `refactor/<descripcion-corta>` o `test/<descripcion-corta>`

---

## 💬 Convención de Commits

Adoptamos la especificación **Conventional Commits** para garantizar un historial de control de versiones legible y automatizable.

### Estructura del Mensaje
```text
<tipo>(<alcance opcional>): <descripción corta en imperativo>

[cuerpo opcional explicativo]
```

### Tipos Permitidos
- **`feat`**: Una nueva funcionalidad para el usuario final.
- **`fix`**: Corrección de un error o bug en el código.
- **`docs`**: Cambios exclusivamente en la documentación.
- **`style`**: Formato de código, espacios en blanco, punto y coma (sin cambios en lógica).
- **`refactor`**: Reorganización de código sin añadir funcionalidades ni corregir errores.
- **`test`**: Añadir o corregir pruebas unitarias o de integración.
- **`chore`**: Tareas de mantenimiento, actualización de dependencias o scripts del build Maven.

### Ejemplos Correctos
```bash
git commit -m "feat(cart): agregar metodo de limpieza total del carrito"
git commit -m "fix(product): corregir excepcion al buscar producto inexistente por id"
git commit -m "docs(api): incluir ejemplos de request y response JSON para checkout"
git commit -m "test(service): incluir pruebas unitarias para validacion de stock"
```

---

## 🔀 Cómo Crear Pull Requests (PR)

1. **Sincroniza tu rama local con `develop`**:
   ```bash
   git checkout develop
   git pull origin develop
   git checkout feature/tu-rama
   git rebase develop
   ```

2. **Publica tu rama en el repositorio remoto**:
   ```bash
   git push origin feature/tu-rama
   ```

3. **Abre el Pull Request en GitHub**:
   - Asigna como rama destino (**Base branch**): `develop`.
   - Selecciona tu rama (**Compare branch**): `feature/tu-rama`.
   - Completa todos los campos obligatorios requeridos por la plantilla [`.github/PULL_REQUEST_TEMPLATE.md`](.github/PULL_REQUEST_TEMPLATE.md).
   - Asigna al menos **un revisor** del equipo.

---

## 👀 Buenas Prácticas de Revisión de Código

Tanto autores como revisores deben seguir estas recomendaciones:

- **Revisiones pequeñas**: Los PRs deben enfocarse en un solo propósito y tener un tamaño manejable (menos de 400 líneas modificadas).
- **Feedback constructivo**: Explicar el *por qué* de una sugerencia de cambio de manera respetuosa y técnica.
- **Respuesta a observaciones**: El autor debe resolver las conversaciones abiertas o proporcionar justificación antes de solicitar una nueva aprobación.
- **Aprobaciones mínimas**: Todo PR requiere al menos **1 aprobación** de un revisor y que el pipeline de GitHub Actions pase exitosamente (`CI / build & test`).

---

## ✅ Checklist Antes de Hacer Merge

Antes de confirmar la fusión (Merge) de un Pull Request a `develop` o `main`:

- [ ] **Compilación limpia**: El proyecto compila sin errores ni advertencias (`./mvnw clean compile`).
- [ ] **Pruebas automáticas**: Todos los tests de JUnit pasan al 100% (`./mvnw test`).
- [ ] **Sin conflictos**: No existen conflictos de integración con la rama base.
- [ ] **Documentación actualizada**: Se actualizaron las especificaciones en `docs/` o `README.md` si hubo cambios en endpoints o configuraciones.
- [ ] **Evidencias adjuntas**: Se incluyeron capturas de pantalla o logs de Postman en el cuerpo del PR.
- [ ] **Revisión aprobada**: Al menos un miembro del equipo ha aprobado los cambios.
- [ ] **Estrategia de Merge**: Usar preferencia **Squash and Merge** o **Rebase and Merge** para mantener un historial lineal.
