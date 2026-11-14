# CarMotors SGT — Guía Global de Agentes (`AGENTS.md`)

Proyecto académico de 5 días: **Java 17 · Maven · MySQL 8 · JDBC puro · Aplicación de Consola**.
Raíz del paquete Java: `com.carmotors.sgt`. Fuente de verdad funcional: `docs/vault/00-Documento-Maestro-v1.3.md`.

---

## 1. Arquitectura y Capas Obligatorias (RNF-02 a RNF-06)

- **`view` (`com.carmotors.sgt.view`):** Solo menús y lectura/impresión por consola (`util`). **NUNCA** llama a `repository` ni ejecuta reglas de negocio o cálculos.
- **`controller` (`com.carmotors.sgt.controller`):** Recibe peticiones de `view`, invoca a `service` (o `TallerFacade`), captura excepciones propias y devuelve respuestas amigables. **NUNCA** abre conexiones JDBC ni accede directo a `repository`.
- **`service` (`com.carmotors.sgt.service` + `builder`, `chain`, `facade`, `observer`, `state`):** Contiene el 100% de las reglas de negocio (RN-01 a RN-22), validaciones de dominio y demarcación de transacciones JDBC. **NUNCA** lee ni imprime directo en consola.
- **`repository` / `repository.impl` (`com.carmotors.sgt.repository`):** Interfaces DAO y sus implementaciones JDBC. Usar **siempre** `PreparedStatement`; **NUNCA** crear un paquete llamado `dao` ni decidir reglas de negocio en SQL/DAO.
- **`model` / `config` / `util` / `exception`:** Entidades del dominio, `ConnectionFactory` (Singleton), utilidades de consola/fecha y jerarquía de excepciones propias.

---

## 2. Invariantes Críticas de Negocio (v1.3)

1. **Transacciones JDBC atómicas (RN-04, RNF-08):** Toda operación que modifique 2 o más tablas obtiene **una única `Connection`** (`ConnectionFactory.getInstance().getConnection()`), fija `setAutoCommit(false)`, pasa la misma `Connection` a todos los repositorios participantes y ejecuta `commit()` o `rollback()` en `try/catch/finally`.
2. **Dinero e IVA (RN-12, RNF-15):** Prohibido `float` o `double` para dinero. Usar `java.math.BigDecimal`, escala 2 y `RoundingMode.HALF_UP`. Tasa de IVA fija académica: **`0.19`** en constante formal guardada en `facturas.tasa_iva` (no viene de `config.properties`).
3. **FEFO determinista (RN-03, §2.6):** Excluir lotes vencidos (`fecha_caducidad IS NOT NULL AND fecha_caducidad < CURDATE()`). Orden obligatorio de consumo:
   1) Lotes **con** `fecha_caducidad` antes que lotes **sin** `fecha_caducidad` (`CASE WHEN fecha_caducidad IS NULL THEN 1 ELSE 0 END ASC`).
   2) `fecha_caducidad ASC` (más próxima primero).
   3) `fecha_ingreso ASC` (más antigua primero).
   4) `id ASC` (menor ID de lote).
   *El stock físico y operativo se calcula siempre desde `lotes`; nunca crear columna `cantidad_stock` en `repuestos`.*
4. **Snapshots inmutables (RN-11, §2.7):** Al asignar servicios/repuestos congelar `nombre_servicio_snapshot`, `nombre_repuesto_snapshot` y `precio_unitario_venta`. Al facturar persistir `facturas`, exactamente un `factura_snapshot` (1:1) y `>= 1` `factura_detalles` (`MANO_OBRA` / `REPUESTO`) en la misma transacción; generar `output/factura_<numero>.txt` (UTF-8) después del `commit()`.
5. **Excepciones, Logs y Seguridad (RNF-12, RNF-14, RNF-23):** Usar `ConfiguracionException` y las excepciones propias de dominio (`T1.2`). Prohibido `System.exit()` en `service`/`repository` y bloques `catch` vacíos. Nunca versionar `config.properties` ni contraseñas.
6. **Nivel técnico de Java del curso (`docs/vault/03-Gestion/Alcance-Tecnico-Java.md`):** Escribir código Java básico–intermedio ceñido exclusivamente a las 20 sesiones vistas en clase (`if/else`, `while/for`, POJOs con `getters`/`setters`, `ArrayList`/`List`/`Map`/`Set`, `enum`, `try/catch/finally`, SOLID, los patrones del curso, `BufferedWriter`/`Properties`, JDBC clásico y Lambdas/`Stream API` directa). **Prohibido** usar `Optional` en validadores, `ResolverStyle.STRICT`, `Locale.ROOT`, reflexión, `CompletableFuture`, `record` o `sealed classes`.

---

## 3. Documentación, Sprints, QMD e Historial

- **Plan de tareas y dependencias:** Consultar `docs/vault/03-Gestion/Plan-Sprints.md` antes de implementar una tarea `Tx.y`.
- **Bóveda Obsidian y QMD:** Consultar la especificación exacta con la skill `carmotors-qmd-consultant` (`qmd search "<termino>" -c carmotors-vault` o `qmd query "<pregunta>" -c carmotors-vault`). Tras un `git pull` con cambios en `docs/vault/`, ejecutar `qmd update`.
- **Documentos derivados:** Nunca editar a mano archivos derivados del maestro; editar `docs/vault/00-Documento-Maestro-v1.3.md` y regenerar con `powershell -NoProfile -File ./scripts/sync-vault-from-master.ps1` + validar con `powershell -NoProfile -File ./scripts/check-traceability.ps1`.
- **Historial vs. README:** Todo PR aprobado o cambio integrado **DEBE registrarse en `CHANGELOG.md`**. **NUNCA modificar `README.md`** hasta la tarea `T8.4` del Sprint 8.
- **Git Flow:** Ramas `feature/*` o `fix/*` desde `develop`; Conventional Commits en inglés (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`); PR hacia `develop` referenciando `Closes #<issue>` con compilación limpia (`mvn clean compile`).

---

## 4. Estrategia Automática de Modelos, Modos y Subagentes

Al recibir cualquier tarea del proyecto, el agente **DEBE** identificar su módulo y nivel de dificultad (1–5) en `docs/vault/03-Gestion/Plan-Sprints.md` (y su ficha `.agents/agent-<modulo>.md`) y aplicar automáticamente esta política para dosificar cuota y calidad:

| Perfil de tarea | Modelo / Perfil recomendado | Modo y acción automática del agente |
|---|---|---|
| **Dificultad 4–5, transacciones multi-tabla, algoritmos críticos o multi-módulo** (`servicios`: State, FEFO, reserva; `facturacion`: snapshot 1:1 + `BigDecimal`; `inventario`: `T3.5` stock; `reportes`: `T7.2`, `T7.5`; conflictos de merge) | **`Orchestrator (Argon)`** *(alternativas de respaldo: `Gemini 4 Hydrogen (XHigh)` o `Gemini 4 Argon (High)`)* | Activar flujo de planificación (`/plan`) **antes** de editar código. Si la sesión actual corre en un modelo ligero (`Flash` / `Low`), advertir brevemente al usuario antes de implementar lógica crítica. |
| **Dificultad 1–3, CRUDs estándar, catálogos y vistas** (`setup`, `clientes`, `proveedores`, `tecnicos`, `T2.x`, `T3.4`–`T3.8`, `T6.3`–`T6.4`) | **`Gemini 4 Hydrogen (Medium/High)`** *(alternativa: `Claude Sonnet 5.5 Medium`)* | Ejecución directa cuando afecte 1–3 archivos claros; usar `/plan` solo si introduce interfaces compartidas. |
| **Auditoría de PRs (`carmotors-pr-reviewer`) y validación de reglas** | **`Gemini 4 Argon (High)`** o **`Claude Sonnet/Opus 5.5 (High)`** | Verificar todas las invariantes de la sección 2 con `carmotors-rules-validator` y actualizar `CHANGELOG.md`. |
| **Consultas QMD, documentación, `CHANGELOG.md`, commits y subagentes de lectura (`research`)** | **`Gemini 3.8 Flash (Low/Medium)`** o **`Gemini 4 Hydrogen (Low)`** | Al lanzar subagentes de exploración de solo lectura, asignar siempre el modelo ligero (`flash`) para preservar cuota. |
| **Tareas independientes en paralelo (`/teamwork`)** | Coordinador en **`Orchestrator (Argon)`** + workers según dificultad de cada módulo | Usar solo cuando las tareas del mismo sprint no compartan archivos (ej. `clientes`, `proveedores`, `tecnicos`). Pasar la ficha `.agents/agent-*.md` correspondiente como `brief` al worker. |
