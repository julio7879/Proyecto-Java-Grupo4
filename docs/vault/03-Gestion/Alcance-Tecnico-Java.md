---
tags:
  - gestion
  - java
  - alcance
  - curso
---
# Alcance Técnico de Java (20 Sesiones del Curso)

> [!info] Navegación en Bóveda
> Volver a [[00-Indice-General]] · Ver [[Definition-of-Done]] · [[Principios-SOLID]] · [[Patrones-Diseno]]
> Skills que validan esta guía: [[carmotors-rules-validator]] · [[carmotors-pr-reviewer]] · [[carmotors-qmd-consultant]]

Todo el código Java de **CarMotors SGT (`com.carmotors.sgt`)** debe mantenerse en un nivel **básico–intermedio**, utilizando exclusivamente herramientas y construcciones vistas en las **20 sesiones (6 módulos) del curso**, para que cualquiera de los 4 integrantes pueda leerlo, mantenerlo y sustentarlo con facilidad.

---

## 📚 Temario de Referencia (6 Módulos · 20 Sesiones)

| Módulo | Sesiones cubiertas | Temas vistos en clase |
|---|---|---|
| **Módulo 1: Fundamentos con Java** | 1 – 4 | Configuración inicial y variables básicas; operadores y estructuras de decisión (`if / else`, `switch`); estructuras repetitivas (`while`, `do-while`, `for`, `for-each`); cadenas (`String`) y conversiones. |
| **Módulo 2: POO y Estructuras de Datos** | 5 – 10 | Introducción a la POO; encapsulamiento (`private`, `getters`/`setters`, constructores); herencia (`extends`) y polimorfismo (`@Override`); arreglos, listas (`ArrayList`, `List`) y genéricos básicos; relaciones entre clases; conjuntos (`Set`), mapas (`Map`), enumeraciones (`enum`) y manejo de excepciones (`try / catch / finally`). |
| **Módulo 3: Principios y Patrones de Diseño** | 11 – 14 | Principios SOLID; patrones creacionales (Singleton, Builder); patrones estructurales (DAO/Repository, Facade); patrones de comportamiento (State, Chain of Responsibility, Observer). |
| **Módulo 4: Concurrencia en Java** | 15 – 16 | Introducción a la concurrencia en Java; sincronización de hilos y procesos atómicos básicos. |
| **Módulo 5: Persistencia de Datos** | 17 – 18 | Manejo de archivos en Java (`Properties`, `File`, `FileWriter`, `BufferedWriter`); JDBC (`DriverManager`, `Connection`, `PreparedStatement`, `ResultSet`, transacciones con `commit()` y `rollback()`). |
| **Módulo 6: Lambdas y Stream API** | 19 – 20 | Expresiones Lambda (`->`); `Stream API` directa (`stream()`, `filter()`, `map()`, `sorted()`, `Comparator`, `collect(Collectors.toList())`). |

---

## ✅ Lo que SÍ se debe usar

- **Control de flujo claro:** `if / else`, `switch` sencillo, `while`, `do-while`, `for` y `for-each`.
- **Cadenas y conversiones directas:** métodos estándar de `String` (`trim()`, `isEmpty()`, `equals()`, `equalsIgnoreCase()`, `toUpperCase()`, `toLowerCase()`, `replace()`, `contains()`, `matches()`), `Integer.parseInt(...)`, `new BigDecimal(...)` con escala `2` y `RoundingMode.HALF_UP`, `LocalDate.parse(...)` con `DateTimeFormatter.ofPattern("yyyy-MM-dd")` y bloques `try / catch`.
- **POO clásica y colecciones:** clases POJO tradicionales con atributos privados, constructores, `getters`/`setters` y `toString()`; herencia e interfaces claras; `List` (`ArrayList`), `Set`, `Map`, `enum` y excepciones propias (`extends Exception` / `RuntimeException`).
- **Patrones, archivos y JDBC:** implementación directa de SOLID y los 7 patrones del proyecto sin sobre-arquitectura; lectura de `config.properties` con `Properties`; generación de facturas TXT UTF-8 con `FileWriter`/`BufferedWriter`; JDBC puro con `PreparedStatement` y transacciones explícitas.
- **Lambdas y Streams legibles:** en búsquedas, filtros y reportes (`stream()`, `filter()`, `map()`, `sorted()`, `collect(Collectors.toList())`), priorizando claridad antes que expresiones anidadas largas.

---

## 🚫 Lo que NO se debe usar (Nivel avanzado / fuera del curso)

- **Evitar `Optional<T>` en validadores y lógica cotidiana:** usar retornos directos (`boolean`, `int`, `BigDecimal`, `String` o `null` controlado cuando sea opcional) y `if / else` o `try / catch` clásicos en lugar de cadenas `.map()`, `.flatMap()` o `.orElseGet()`.
- **Evitar APIs avanzadas o sintaxis poco vista:** no usar `ResolverStyle.STRICT` con patrón `uuuu-MM-dd`, `Locale.ROOT`, reflexión (`java.lang.reflect`), anotaciones personalizadas (`@interface`), `CompletableFuture`, `ForkJoinPool`, `record`s ni `sealed classes`.
- **Regla práctica de equipo:** antes de aprobar cualquier método o clase, todo el código debe poder explicarse línea por línea con lo visto en las 20 sesiones del curso.
