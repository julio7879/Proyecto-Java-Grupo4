---
name: carmotors-qmd-consultant
description: Busca y recupera especificaciones, requerimientos funcionales, reglas de negocio, historias de usuario y el plan de sprints en la bóveda Obsidian de CarMotors usando el CLI local QMD.
---

# Skill: CarMotors QMD Consultant

Esta skill permite a cualquier agente (Antigravity, Claude Code, Gemini CLI, Codex) consultar la documentación oficial de CarMotors indexada en QMD (`carmotors-vault`).

> **Modelo recomendado:** Esta skill es de solo lectura; ejecutarla con modelos ligeros (`Gemini 3.8 Flash` / `Gemini 4 Hydrogen Low`) o delegar en subagentes `flash` para ahorrar cuota.

## Cuándo usarla
- Necesitas conocer el alcance exacto de un Requerimiento Funcional (`RF-01` a `RF-35`) o No Funcional (`RNF-01` a `RNF-25`).
- Necesitas validar una Regla de Negocio (`RN-01` a `RN-22`).
- Necesitas revisar los criterios de aceptación Dado / Cuando / Entonces de una Historia de Usuario (`HU-01` a `HU-16`).
- Necesitas consultar una tarea del plan de sprints (`T0.1` a `T8.6`), su dificultad, dependencias o responsable en `docs/vault/03-Gestion/Plan-Sprints.md`.
- Necesitas verificar tablas, columnas, patrones o historial de cambios (`CHANGELOG.md`).

## Comandos a ejecutar en terminal

1. **Refrescar el índice después de un `git pull` o cambios en `docs/vault/`:**
   ```bash
   qmd update
   ```

2. **Búsqueda rápida por palabras clave (BM25, sin costo de LLM):**
   ```bash
   qmd search "<termino>" -c carmotors-vault
   ```
   *Ejemplos:*
   - `qmd search "FEFO" -c carmotors-vault`
   - `qmd search "T5.2" -c carmotors-vault`

3. **Consulta con expansión semántica:**
   ```bash
   qmd query "<pregunta o concepto>" -c carmotors-vault
   ```
   *Ejemplo:* `qmd query "como se calcula la sugerencia de tecnicos" -c carmotors-vault`

4. **Lectura completa de un archivo específico en QMD o workspace:**
   ```bash
   qmd get docs/vault/01-Modulos/05-Servicios-Ordenes.md
   qmd get docs/vault/03-Gestion/Plan-Sprints.md
   ```
