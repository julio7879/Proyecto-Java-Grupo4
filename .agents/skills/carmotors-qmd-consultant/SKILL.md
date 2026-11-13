---
name: carmotors-qmd-consultant
description: Busca y recupera especificaciones, requerimientos funcionales, historias de usuario y decisiones de arquitectura en la bóveda Obsidian de CarMotors usando el CLI local QMD.
---

# Skill: CarMotors QMD Consultant

Esta skill permite a cualquier agente (Antigravity, Claude Code, Gemini CLI, Codex) consultar la documentación de CarMotors indexada en QMD.

## Cuándo usarla
- Necesitas conocer el alcance exacto de un Requerimiento Funcional (RF-01 a RF-35).
- Necesitas validar una Regla de Negocio (RN-01 a RN-22).
- Necesitas revisar los criterios de aceptación Dado / Cuando / Entonces de una Historia de Usuario (HU-01 a HU-16).
- Necesitas consultar la definición técnica de un patrón o las columnas del modelo de base de datos.

## Comandos a ejecutar en terminal

1. **Búsqueda por palabras clave (BM25 rápido, sin LLM):**
   ```bash
   qmd search "<termino>" -c carmotors-vault
   ```
   *Ejemplo:* `qmd search "FEFO" -c carmotors-vault`

2. **Consulta con expansión semántica:**
   ```bash
   qmd query "<pregunta o concepto>" -c carmotors-vault
   ```
   *Ejemplo:* `qmd query "como se calcula la sugerencia de tecnicos" -c carmotors-vault`

3. **Lectura completa de un archivo específico:**
   ```bash
   qmd get docs/vault/01-Modulos/05-Servicios-Ordenes.md
   ```
