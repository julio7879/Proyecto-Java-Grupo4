# Historial de Cambios — CarMotors SGT (`Proyecto-Java-Grupo4`)

> [!NOTE]
> Este archivo registra de forma cronológica todos los Pull Requests, ramas y cambios técnicos integrados al proyecto.
> **Regla del equipo:** cada vez que se acepte e integre un PR o rama, se debe agregar una entrada aquí. **`README.md` no se modifica en tareas intermedias** hasta la tarea `T8.4` del Sprint 8.

---

## [En curso] — Sprint 1: Piezas comunes (`develop`)

### Pendiente de integración — `[S1 - T1.1] Utilidades de lectura y validación por consola` (`feature/project-setup` → `develop`, Issue `#14`)
- **Autor:** `@julio7879`
- **Cambios incluidos:**
  - Creación de `InputValidator` (`com.carmotors.sgt.util`) para validación y normalización de textos, enteros, rangos, importes `BigDecimal` (escala 2, `RoundingMode.HALF_UP`, `RNF-15`), fechas `yyyy-MM-dd` (`DateTimeFormatter`, `RNF-13`), emails, placas (`RN-13`) y confirmaciones `S/N` (`RNF-24`) al nivel del curso.
  - Creación de `ConsoleReader` (`com.carmotors.sgt.util`) con métodos estáticos seguros en una sola llamada y reintento automático sin abortar la aplicación (`RNF-12`, `RNF-13`, `RNF-24`).
  - Creación de pruebas unitarias JUnit 5 en `ConsoleReaderTest`.
  - Documentación del nivel técnico permitido de Java (`docs/vault/03-Gestion/Alcance-Tecnico-Java.md`, 20 sesiones del curso) conectada a `AGENTS.md`, `carmotors-rules-validator`, `carmotors-pr-reviewer`, `Definition-of-Done.md` y reorganización de `docs/vault/06-Skills/` y `00-MOC/00-Mapa-Agentes-Skills.md`.

---

## [2026-11-14] — Configuración multi-agente, plan de sprints y grafo Obsidian (`develop`)

### PR #65 — `chore: configure AGENTS.md, sprint plan, unified skills v1.3 and Obsidian graph` (`chore/agent-workspace-setup` → `develop`, commit `5b74cae`, cierra `#64`)
- **Autor:** `@julio7879`
- **Cambios incluidos:**
  - Incrustado del plan oficial de sprints en `docs/vault/03-Gestion/Plan-Sprints.md` (con `T0.10` marcada como completada) y enlace en `docs/vault/00-MOC/00-Indice-General.md`.
  - Creación de `AGENTS.md` en la raíz con arquitectura v1.3, reglas innegociables y matriz automática de modelos/modos (`Orchestrator (Argon)`, `Gemini 4 Hydrogen`, `Gemini 4 Argon`, `Gemini 3.8 Flash`, `/plan`, `/teamwork`).
  - Actualización de `.agents/README.md` y las 9 fichas `.agents/agent-*.md` con estrategia de modelo por módulo y alineación con el Documento Maestro v1.3.
  - Consolidación de `.agents/skills/carmotors-pr-reviewer/SKILL.md` en el repositorio y unificación de `carmotors-rules-validator` y `carmotors-qmd-consultant` (paquete `repository`, FEFO de 4 criterios, IVA fijo `0.19`, excepciones propias y protección de `README.md`).
  - Integración visual de los **9 agentes especialistas** y **3 skills** en la bóveda Obsidian (`docs/vault/05-Agentes/` y `docs/vault/.obsidian/graph.json`) con enlaces bidireccionales hacia módulos, arquitectura, gestión e historias de usuario (`[[00-Mapa-Agentes-Skills]]`).
  - Creación de este historial `CHANGELOG.md` y limpieza temporal de `README.md` hasta `T8.4`.

---

## [2026-11-13] — Estructura base Maven, JDBC y BD v1.3 (`develop`)

### PR #3 — `Feature/project structure` (`feature/project-structure` → `develop`, commit `668acae`)
- **Autor:** Andrés Jiménez (`@AlmX4`), con co-autoría de Jhorman (`@JopsWtD`) y Julio (`@julio7879`).
- **Tareas cubiertas:** `T0.10` (Issue `#13`, cerrado).
- **Cambios principales:**
  - Actualización de `database/schema.sql` al modelo lógico completo **v1.3 de 20 tablas** (`InnoDB`, `utf8mb4`), incorporando `factura_snapshot`, `factura_detalles`, fechas de ciclo (`fecha_completado`, `fecha_entrega`, `fecha_cancelacion`), campos *snapshot*, restricciones `CHECK` y FK compuesta opcional `(orden_detalle_id, repuesto_id)`.
  - Ampliación de `database/data.sql` con el conjunto inicial de datos de prueba y catálogos (incluido el estado `Cancelada` en `servicio_estados`).

### PR #2 — `Feature/project structure` (`feature/project-structure` → `develop`, commit `ba2bc89`)
- **Autor:** Jhorman (`@JopsWtD`).
- **Tareas cubiertas:** `T0.7`, `T0.8`, `T0.9` (Issues `#10`, `#11`, `#12`, cerrados).
- **Cambios principales:**
  - Configuración inicial de Maven en `pom.xml` (Java 17, `mysql-connector-j` 8.4.0, `junit-jupiter` 5.11.4 y `exec-maven-plugin` 3.5.0 apuntando a `com.carmotors.sgt.Main`).
  - Refactorización y esqueleto de paquetes en inglés bajo `com.carmotors.sgt`: `config`, `controller`, `exception`, `model`, `repository`, `repository.impl`, `service` (`builder`, `chain`, `facade`, `observer`, `state`), `util` y `view`.
  - Implementación de `Main.java`, `ConnectionFactory.java` (patrón Singleton JDBC leyendo `config.properties`) y `ConfiguracionException.java`.
  - Configuración de `src/main/resources/config.properties.example` (usando carpeta `output/` y definiendo el IVA fijo al 19% en código según DP-05) y ampliación de `.gitignore`.
  - Creación inicial de `database/schema.sql` y `database/data.sql`.

---

## [2026-11-13] — Backlog, Bóveda Obsidian v1.3 y Flujo Git (`main` / `develop`)

### Issues `#4`–`#63` en GitHub Project #6 (`2026-11-13` / `2026-11-14`)
- **Autor:** `@julio7879`.
- **Detalle:** Creación del tablero completo de Sprints `S0` a `S8` en GitHub (`#4`–`#13` cerrados para `S0`; `#14`–`#63` abiertos para `S1`–`S8`).

### Base inicial y Bóveda v1.3 (`main` / `develop`, commits `00eec57`..`e9f29c9`)
- **Autor:** `@julio7879`.
- **Cambios principales:**
  - Configuración de `.github/` (`CODEOWNERS`, `pull_request_template.md`, `ISSUE_TEMPLATE/*`), rulesets de protección para `main` y `develop`, y `.gitignore`.
  - Creación de la bóveda Obsidian en `docs/vault/` con `00-Documento-Maestro-v1.3.md` (35 RF, 22 RN, 25 RNF, 16 HU), `00-MOC/`, `01-Modulos/`, `02-Arquitectura/` (incluidos diagramas `.mmd`), `03-Gestion/` y `04-Historias/`.
  - Creación de scripts `scripts/sync-vault-from-master.ps1` y `scripts/check-traceability.ps1`.
  - Configuración inicial de `.agents/` (9 fichas especialistas por módulo y skills `carmotors-qmd-consultant` y `carmotors-rules-validator`).
