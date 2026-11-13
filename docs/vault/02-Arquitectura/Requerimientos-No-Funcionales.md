# Requerimientos No Funcionales (RNF-01 a RNF-25)

Arquitectura, capas, persistencia y condiciones de entrega. Son la base del [[Definition-of-Done]] y de la rúbrica.

> [!warning] Documento derivado (no editar a mano)
> Generado desde [[00-Documento-Maestro-v1.2]] por `scripts/sync-vault-from-master.ps1`.
> Cualquier corrección se hace en el maestro y se regenera: así no hay dos versiones de la verdad.

### 5.1 Arquitectura y código

| ID | Requerimiento |
|---|---|
| RNF-01 | Java 17, Maven y JDBC con MySQL 8. No se utilizan frameworks ORM de persistencia. |
| RNF-02 | Paquetes separados por responsabilidad: `model`, `view`, `controller`, `service`, `repository` (DAO), `config` y `util`. Se pueden crear subpaquetes por módulo. |
| RNF-03 | La vista captura y muestra información por consola. No contiene cálculos ni reglas de negocio. |
| RNF-04 | El controlador recibe la petición de la vista y llama a servicios. Nunca accede directamente a un DAO. |
| RNF-05 | Toda la lógica de negocio definida en RN-01 a RN-22 se implementa en la capa `service`. |
| RNF-06 | Un DAO por entidad o agregado, definido mediante interfaz e implementación JDBC. Incluye CRUD y consultas específicas necesarias. |
| RNF-07 | Todas las consultas y actualizaciones parametrizables usan `PreparedStatement`. No se concatena entrada del usuario en sentencias SQL. |
| RNF-08 | Las operaciones que modifican varias tablas —por ejemplo ingreso de lote, creación de orden, completar, cancelar y facturar— usan una transacción JDBC con `commit` y `rollback`. |
| RNF-09 | Se implementan al menos cinco patrones, incluidos obligatoriamente Singleton y DAO. La sección 5.3 distingue los cinco patrones base de los dos opcionales propuestos. |
| RNF-10 | Se aplican principios SOLID donde aporten claridad y se documentan ejemplos concretos en el README. |
| RNF-11 | Se utilizan Lambdas y Stream API en procesamiento de colecciones para búsquedas, ordenamientos, agrupaciones o transformaciones de reportes. SQL se utiliza primero para reducir los datos recuperados. |
| RNF-12 | Excepciones propias, por ejemplo `StockInsuficienteException` y `TransicionEstadoInvalidaException`, se capturan en el controlador y se muestran como mensajes claros. Un dato incorrecto no debe cerrar la aplicación. |
| RNF-13 | Se validan entradas de consola: números, fechas `yyyy-MM-dd`, emails y campos obligatorios. La validación de formato se centraliza; las reglas de negocio se vuelven a comprobar en `service`. |
| RNF-14 | URL y credenciales de la base de datos se leen desde `config.properties` fuera del código fuente. Se incluye `config.properties.example` sin secretos reales. |
| RNF-15 | Los montos se manejan con `BigDecimal`, nunca con `double`. Se guardan con dos decimales y se aplica `HALF_UP` de forma uniforme. |

### 5.2 Repositorio y documentación

| ID | Requerimiento |
|---|---|
| RNF-16 | Repositorio privado en GitHub. La entrega incluye URL del repositorio y hash del último commit antes del límite indicado por el profesor. |
| RNF-17 | `.gitignore` desde el primer commit, incluyendo `/target/`, `*.class`, `.idea/`, `.vscode/`, `*.iml`, `*.log` y `config.properties`. |
| RNF-18 | Git Flow: `main` (estable), `develop` (integración) y `feature/*` creadas desde `develop`. |
| RNF-19 | Commits con Conventional Commits en inglés: `feat:`, `fix:`, `docs:`, `refactor:`, `test:` y `chore:`. |
| RNF-20 | README con instrucciones de instalación y ejecución, tecnologías, arquitectura, diagrama E-R, estructura de paquetes, patrones, principios SOLID, configuración y datos de prueba. |
| RNF-21 | Carpeta `/database` con `schema.sql` para crear la estructura desde cero y `data.sql` con datos de prueba coherentes y relacionados. |
| RNF-22 | El proyecto debe compilar e iniciar con `mvn clean compile exec:java` en una máquina limpia siguiendo únicamente las instrucciones del README. |

### 5.3 Requisitos complementarios derivados de la rúbrica

| ID | Requerimiento |
|---|---|
| RNF-23 | La aplicación debe registrar eventos relevantes y errores mediante `java.util.logging` o una alternativa estándar. Debe existir un archivo de log dentro de la carpeta definida para logs. Se registran errores técnicos, contexto suficiente y operaciones críticas; nunca contraseñas ni secretos. Los mensajes para el administrador deben ser comprensibles y no mostrar trazas internas completas. |
| RNF-24 | La interfaz de consola debe mantener menús consistentes, opciones numeradas, instrucciones claras, validación sin pérdida de flujo, confirmación antes de cancelaciones o desactivaciones y mensajes explícitos cuando una consulta no arroje resultados. |
| RNF-25 | `data.sql` debe contener un conjunto de datos representativo que cubra relaciones y escenarios positivos, negativos y límite: varios estados de órdenes, lotes vencidos y próximos a vencer, stock bajo, proveedor sin evaluar, técnicos sin carga y órdenes preventivas entregadas. El conjunto recomendado aparece en la sección 9. |
