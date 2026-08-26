# 🛠️ Guía Detallada de Configuración del Proyecto (`docs/project-setup.md`)

Esta guía proporciona un instructivo completo, estructurado y académico para la preparación del entorno de desarrollo local, configuración de base de datos, ejecución de pruebas unitarias/integración y simulación del pipeline de Integración Continua (CI).

---

## 1. 📥 Clonado del Repositorio

Para obtener una copia del proyecto en tu máquina local:

```bash
# Mediante HTTPS
git clone https://github.com/tu-usuario/E-commerce.git

# Mediante SSH (si tienes llaves configuradas)
git clone git@github.com:tu-usuario/E-commerce.git

# Ingresar al directorio del proyecto
cd E-commerce
```

---

## 2. ⚙️ Configuración del Entorno Local

### A. Instalación de Java Development Kit (JDK 21)
El proyecto requiere **Java 21 (LTS)**.

1. **Descarga**: Obtén JDK 21 desde [Eclipse Temurin (Adoptium)](https://adoptium.net/) o Oracle JDK 21.
2. **Configuración de Variables de Entorno**:
   - En **Windows**:
     - Crear la variable de sistema `JAVA_HOME` apuntando al directorio de instalación:  
       `C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot\`
     - Agregar `%JAVA_HOME%\bin` a la variable `Path`.
   - En **Linux / macOS**:
     - Agregar al archivo `~/.bashrc` o `~/.zshrc`:
       ```bash
       export JAVA_HOME=/path/to/jdk-21
       export PATH=$JAVA_HOME/bin:$PATH
       ```
3. **Verificación**:
   ```bash
   java -version
   ```
   *Debe retornar algo similar a `openjdk version "21.0.x"`.*

---

## 3. 📦 Maven Wrapper (`mvnw` / `mvnw.cmd`)

El proyecto incluye **Maven Wrapper**, lo que elimina la necesidad de instalar Apache Maven de forma independiente en el sistema operativo.

### Comandos Principales con Maven Wrapper

- **Limpiar artefactos anteriores y compilar**:
  - Windows: `mvnw.cmd clean compile`
  - Linux/macOS: `./mvnw clean compile`

- **Ejecutar servidor de desarrollo**:
  - Windows: `mvnw.cmd spring-boot:run`
  - Linux/macOS: `./mvnw spring-boot:run`

- **Empaquetar la aplicación en archivo `.jar` executable**:
  - Windows: `mvnw.cmd clean package`
  - Linux/macOS: `./mvnw clean package`

---

## 4. 📝 Configuración de `application.properties`

El archivo de propiedades [`src/main/resources/application.properties`](../src/main/resources/application.properties) permite cambiar entre la base de datos de desarrollo en memoria (H2) y una base de datos relacional MySQL.

### A. Perfil de Desarrollo (H2 Database en memoria - Por Defecto)
```properties
spring.application.name=Ecommerce
server.port=8080

spring.datasource.url=jdbc:h2:mem:ecommerce_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### B. Configuración para MySQL (Opcional)
Para cambiar a MySQL, desmarca las líneas de MySQL en `application.properties` y comenta la sección de H2:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
```

---

## 5. 🧪 Cómo Ejecutar las Pruebas

El proyecto utiliza **JUnit 5** y **Spring Boot Test**.

### A. Ejecución de Pruebas Automáticas desde Terminal
```bash
# En Linux/macOS
./mvnw clean test

# En Windows
mvnw.cmd clean test
```

### B. Estructura de Salida Esperada
Al finalizar las pruebas, Maven Surefire genera el reporte:

```text
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.example.demo.EcommerceApplicationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 13.31 s -- in com.example.demo.EcommerceApplicationTests
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## 6. 🤖 Cómo Ejecutar y Probar GitHub Actions

El archivo [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) define las acciones automáticas de integración continua.

### A. Ejecución Automática en GitHub
1. Al subir cambios a las ramas `main`, `master` o `develop`, o al abrir un Pull Request hacia ellas, GitHub Actions disparará el pipeline automáticamente.
2. Puedes visualizar la ejecución en la pestaña **Actions** de tu repositorio en GitHub.

### B. Simulación Local con la Herramienta `act` (Opcional)
Si deseas probar el workflow de GitHub Actions en tu máquina local sin subir commits a GitHub, puedes instalar la herramienta libre `act`:

1. Instala `act` (requiere Docker instalado):
   ```bash
   # En Windows vía Chocolatey
   choco install act-cli

   # En macOS vía Homebrew
   brew install act
   ```

2. Ejecutar el workflow en local:
   ```bash
   act pull_request
   ```
