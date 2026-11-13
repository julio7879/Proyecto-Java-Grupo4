# Módulo 00 — Setup & Configuración del Proyecto

## 1. Identificación
- **Rama asociada:** `feature/project-setup`
- **Agente especialista:** `@agent-setup`
- **Dependencias:** Ninguna (base del proyecto)
- **Requerimientos no funcionales clave:** RNF-01, RNF-02, RNF-07, RNF-14, RNF-17, RNF-22, RNF-23

---

## 2. Alcance Técnico
1. Estructura Maven estándar Java 17 (`pom.xml`).
2. Dependencias mínimas:
   - Driver JDBC MySQL 8 (`mysql-connector-j`).
   - Logging (`java.util.logging` o SLF4J/Logback sin sobrecargar).
   - Pruebas unitarias JUnit 5.
3. Patrón Creacional **Singleton**: `ConnectionFactory` que carga `config.properties` una sola vez y expone `getConnection()` retornando una **nueva** conexión en cada llamada (vital para transacciones RNF-08).
4. Configuración externa de credenciales:
   - `config.properties.example` versionado en Git.
   - `config.properties` ignorado en `.gitignore`.
5. Estructura de paquetes por responsabilidad:
   - `com.carmotors.sgt.model`
   - `com.carmotors.sgt.view`
   - `com.carmotors.sgt.controller`
   - `com.carmotors.sgt.service`
   - `com.carmotors.sgt.repository`
   - `com.carmotors.sgt.config`
   - `com.carmotors.sgt.util`

---

## 3. Criterios de Aceptación
- [ ] El proyecto compila y empaqueta limpiamente con `mvn clean compile`.
- [ ] La clase de prueba de conexión valida la comunicación con MySQL 8 usando `ConnectionFactory`.
- [ ] No existen credenciales ni cadenas de conexión "hardcodeadas" en clases Java.
- [ ] El `.gitignore` cubre `target/`, `.idea/`, `.vscode/`, `config.properties`, logs y archivos temporales.
