# 🤖 Agentes Especialistas de CarMotors

Esta carpeta define las fichas de contexto especializadas por módulo y las skills compartidas para el proyecto **CarMotors SGT** (`Java 17 · Maven · MySQL 8 · JDBC`).

> [!IMPORTANT]
> Las reglas globales obligatorias para cualquier herramienta viven en [AGENTS.md](../AGENTS.md) en la raíz del repositorio. Todas las skills compartidas viven en `.agents/skills/`.

---

## Compatibilidad Multi-Herramienta (Universal)
- **Google Antigravity:** carga automáticamente `AGENTS.md` y `.agents/skills/*`; las fichas `agent-*.md` se adjuntan bajo demanda con `@agent-<modulo>` o como `brief` al usar `/teamwork`.
- **Claude Code / Gemini CLI / Codex:** leen `AGENTS.md` en la raíz y pueden cargar cualquiera de las fichas `agent-*.md` como contexto específico de módulo.

---

## Índice de Agentes y Perfil de Modelo Recomendado

| Agente | Archivo | Módulo Asociado | Contexto en Bóveda | Modelo / Modo Recomendado (Antigravity) |
|---|---|---|---|---|
| `@agent-setup` | `agent-setup.md` | Configuración base (`S0–S1, S8`) | `docs/vault/01-Modulos/00-Setup.md` | `Gemini 4 Hydrogen (Medium)` / `Gemini 3.8 Flash` |
| `@agent-clientes` | `agent-clientes.md` | Clientes y Vehículos (`S1–S2, S7`) | `docs/vault/01-Modulos/01-Clientes-Vehiculos.md` | `Gemini 4 Hydrogen (Medium/High)` · apto para `/teamwork` |
| `@agent-proveedores` | `agent-proveedores.md` | Proveedores y Catálogo (`S1–S3`) | `docs/vault/01-Modulos/02-Proveedores.md` | `Gemini 4 Hydrogen (Medium/High)` · `/plan` en `T3.1` |
| `@agent-inventario` | `agent-inventario.md` | Inventario y Lotes (`S2–S3, S5`) | `docs/vault/01-Modulos/03-Inventario.md` | `Gemini 4 Hydrogen (High)` · **`Orchestrator (Argon)`** + `/plan` en `T3.3`, `T3.5` |
| `@agent-tecnicos` | `agent-tecnicos.md` | Técnicos y Especialidades (`S2, S4`) | `docs/vault/01-Modulos/04-Tecnicos.md` | `Gemini 4 Hydrogen (Medium/High)` · `/plan` en `T4.4` |
| `@agent-servicios` | `agent-servicios.md` | Servicios y Órdenes (`S2, S4–S5`) | `docs/vault/01-Modulos/05-Servicios-Ordenes.md` | **`Orchestrator (Argon)`** + **`/plan` obligatorio** |
| `@agent-facturacion` | `agent-facturacion.md` | Facturación Interna (`S6`) | `docs/vault/01-Modulos/06-Facturacion.md` | **`Orchestrator (Argon)`** + **`/plan` obligatorio** en `T6.1`–`T6.2` |
| `@agent-reportes` | `agent-reportes.md` | Reportes Gerenciales (`S7`) | `docs/vault/01-Modulos/07-Reportes.md` | **`Gemini 4 Argon (High)`** / `Orchestrator (Argon)` + `/plan` en `T7.2`, `T7.5` |
| `@agent-database` | `agent-database.md` | Base de Datos DDL/DML (`S0, S8`) | `docs/vault/01-Modulos/08-Base-Datos.md` | `Gemini 4 Hydrogen (High)` / `Gemini 4 Argon (Medium/High)` |

---

## Skills Compartidas (`.agents/skills/`)

- **`carmotors-qmd-consultant`:** Consulta rápida (BM25/semántica) sobre la bóveda Obsidian y `Plan-Sprints.md` vía `qmd`.
- **`carmotors-rules-validator`:** Checklist obligatoria de arquitectura (`repository`), transacciones JDBC, FEFO (4 criterios), `BigDecimal` (`HALF_UP`) y snapshots v1.3.
- **`carmotors-pr-reviewer`:** Auditoría técnica de ramas/PRs contra `develop`, compilación Maven, actualización de `CHANGELOG.md` y protección de `README.md`.
