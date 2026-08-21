name: E-commerce CI Pipeline
on:
  push:
    branches:
      - main
      - master
      - develop
  pull_request:
    branches:
      - main
      - master
      - develop
  workflow_dispatch:
permissions:
  contents: read
jobs:
  quality-and-test:
    name: Java, Tests and Static Checks
    runs-on: ubuntu-latest
    steps:
      - name: 1. Check out repository
        uses: actions/checkout@v4
        with:
          fetch-depth: 0
      - name: 2. Set up Java 21 (Temurin)
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '21'
          cache: 'maven'
      - name: 3. Set executable permissions on Maven Wrapper
        run: chmod +x mvnw
      - name: 4. Run Maven tests
        run: ./mvnw -B clean test
      - name: 5. Validate JavaScript syntax
        run: node --check src/main/resources/static/js/app.js
      - name: 6. Reject whitespace errors
        run: git diff --check origin/main...HEAD || true
      - name: 7. Build and package JAR
        run: ./mvnw -B -DskipTests package
      - name: 8. Upload Build Artifact
        uses: actions/upload-artifact@v4
        with:
          name: ecommerce-app-jar
          path: target/*.jar
          retention-days: 5
  mysql-smoke:
    name: MySQL 8.4 Smoke Test
    runs-on: ubuntu-latest
    needs: quality-and-test
    services:
      mysql:
        image: mysql:8.4
        env:
          MYSQL_DATABASE: ecommerce_db
          MYSQL_USER: ecommerce_app
          MYSQL_PASSWORD: ci_password
          MYSQL_ROOT_PASSWORD: ci_root_password
        ports:
          - 3306:3306
            options: >-
          --health-cmd="mysqladmin ping -h 127.0.0.1 -u root -pci_root_password"
          --health-interval=10s
          --health-timeout=5s
          --health-retries=10
    steps:
      - name: Check out repository
        uses: actions/checkout@v4
      - name: Set up Java 21
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '21'
          cache: 'maven'
      - name: Build application
        run: |
          chmod +x mvnw
          ./mvnw -B -DskipTests package
      - name: Start application against MySQL
        env:
          SPRING_PROFILES_ACTIVE: mysql
          SPRING_DATASOURCE_URL: jdbc:mysql://127.0.0.1:3306/ecommerce_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
          SPRING_DATASOURCE_USERNAME: ecommerce_app
          SPRING_DATASOURCE_PASSWORD: ci_password
          SPRING_DATASOURCE_DRIVER_CLASS_NAME: com.mysql.cj.jdbc.Driver
          SPRING_JPA_DATABASE_PLATFORM: org.hibernate.dialect.MySQLDialect
          SPRING_JPA_HIBERNATE_DDL_AUTO: update
          DB_URL: jdbc:mysql://127.0.0.1:3306/ecommerce_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
          DB_USERNAME: ecommerce_app
          DB_PASSWORD: ci_password
          JWT_SECRET_BASE64: MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=
          JWT_EXPIRATION_SECONDS: '3600'
          ADMIN_USERNAME: admin
          ADMIN_PASSWORD_HASH: '$2a$10$6iurHBvm3dPiZpMddjiWfenLe0HT.EDbln2WDlZ3aP.b2xFZy6SMS'
          SERVER_PORT: '8080'
        run: |
          java -jar target/*.jar > app.log 2>&1 &
          APP_PID=$!
          trap 'kill "$APP_PID" 2>/dev/null || true' EXIT
          for attempt in $(seq 1 30); do
            if curl --fail --silent --show-error http://127.0.0.1:8080/api/products > /tmp/products.json; then
              test -s /tmp/products.json
              exit 0
            fi
            if ! kill -0 "$APP_PID" 2>/dev/null; then
              cat app.log
              exit 1
            fi
            sleep 2
          done
          cat app.log
          exit 1
Siguiente paso:
Haz clic en el botón superior derecho "Mark as resolved".
Pasa al siguiente archivo (src/main/java/com/example/demo/dto/ProductRequest.java). Si también tiene marcas de conflicto, pégamelo aquí y te doy la versión limpia lista para guardar.
9:43 PM
9:45 PM
Para resolver el conflicto en ProductRequest.java, borra todo el contenido en el editor y pega esta versión limpia y resuelta:

java
package com.example.demo.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
public class ProductRequest {
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar los 120 caracteres")
    private String nombre;
    @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
    private String descripcion;
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio debe ser mayor o igual a 0")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener como máximo 8 enteros y 2 decimales")
    private BigDecimal precio;
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
    private Integer stock;
    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 255, message = "La categoría no puede superar los 255 caracteres")
    private String categoria;
    @Pattern(
            regexp = "https?://\\S+",
            message = "La URL de imagen no es válida"
    )
    @Size(max = 500, message = "La URL de imagen no puede superar los 500 caracteres")
    private String imagenUrl;
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public BigDecimal getPrecio() {
        return precio;
    }
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    public Integer getStock() {
        return stock;
    }
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public String getImagenUrl() {
        return imagenUrl;
    }
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
}
