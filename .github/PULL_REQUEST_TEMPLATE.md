## 📌 Descripción de los Cambios
<!-- Proporciona un resumen claro y conciso de los cambios realizados y la motivación detrás de ellos. -->

- **Ticket / Problema abordado**: Fixes #
- **Módulo(s) afectado(s)**: `controller`, `service`, `repository`, `dto`, `docs`, `ci`

---

## 🏷️ Tipo de Cambio
<!-- Marca con una [x] la opción que aplique -->

- [ ] 🐛 **Bugfix** (Cambio que corrige un problema o fallo en el código)
- [ ] ✨ **Nueva Funcionalidad** (Añade una característica sin romper la compatibilidad)
- [ ] ♻️ **Refactorización** (Mejora del código existente sin cambiar comportamiento externo)
- [ ] 📝 **Documentación** (Actualización de README, API docs o diagramas)
- [ ] 🧪 **Pruebas** (Añadido o actualización de tests unitarios o colecciones Postman)
- [ ] ⚙️ **CI/CD / Configuración** (Ajustes en GitHub Actions, Maven o propiedades del sistema)

---

## 🖼️ Evidencias / Capturas de Pantalla
<!-- Si aplicaste cambios visuales en el frontend o probaste endpoints en Postman, adjunta aquí capturas de pantalla, GIFs o fragmentos de logs. -->

| Escenario / Endpoint | Captura de Pantalla / Log Evidencia |
| :--- | :--- |
| **Prueba en Postman / Navegador** | *(Adjuntar imagen aquí)* |

---

## 🧪 Pruebas Realizadas
<!-- Describe las pruebas ejecutadas para verificar que tus cambios funcionan correctamente. -->

- [ ] **Pruebas Unitarias / Contexto Spring**: Se ejecutó `./mvnw test` obteniendo `BUILD SUCCESS`.
- [ ] **Pruebas de API con Postman**: Se probaron los endpoints modificados enviando payloads válidos e inválidos.
- [ ] **Pruebas Manuales en Navegador**: Se comprobó la interfaz web en Chrome / Firefox.

---

## ✅ Checklist de Validación Final

Antes de solicitar la revisión de este Pull Request, confirma que has completado los siguientes pasos:

- [ ] Mi código sigue la guía de estilo del proyecto y cumple con los estándares de Java 21 / Spring Boot.
- [ ] He realizado una auto-revisión meticulosa de mi propio código.
- [ ] No modifiqué archivos ni código fuera del alcance asignado a esta tarea.
- [ ] He actualizado la documentación correspondiente en la carpeta `docs/` o `README.md` si aplica.
- [ ] Mis cambios no generan nuevas advertencias o warnings en el proceso de compilación (`./mvnw clean compile`).
- [ ] He añadido o actualizado los tests necesarios y todos pasan correctamente.
- [ ] El pipeline de Integración Continua (GitHub Actions) ha finalizado en verde.
