---
tags:
  - skill
  - qmd
  - documentacion
---
# Skill: `carmotors-qmd-consultant`

- **Archivo fuente:** [SKILL.md](../../../.agents/skills/carmotors-qmd-consultant/SKILL.md)
- **Mapa central:** [[00-Mapa-Agentes-Skills]] · [[00-Indice-General]]
- **Modelo recomendado (`AGENTS.md`):** `Gemini 3.8 Flash (Low/Medium)` o `Gemini 4 Hydrogen (Low)` para consultas QMD y exploración de solo lectura.

## 🎯 Responsabilidad Exacta
Recupera especificaciones funcionales (`RF`), reglas de negocio (`RN-01` a `RN-22`), criterios `Dado/Cuando/Entonces`, esquemas y reparto oficial de tareas (`Tx.y`) desde la colección local **`carmotors-vault`** antes de que un agente implemente o revise código.

## 🔗 Referencias que Consulta en la Bóveda
- **Fuente de verdad:** [[00-Documento-Maestro-v1.3]]
- **Planificación y gestión:** [[Plan-Sprints]] · [[Plan-Iteraciones]] · [[Definition-of-Done]]
- **Arquitectura y reglas:** [[Reglas-Negocio]] · [[Requerimientos-No-Funcionales]] · [[Modelo-Relacional]] · [[Diagrama-Clases]] · [[Transacciones-JDBC]] · [[Patrones-Diseno]]
- **Sincronización:** Después de hacer `git pull` con cambios en `docs/vault/`, ejecutar `qmd update` en consola.

## 🤖 Agentes que la Utilizan
[[agent-setup]] · [[agent-database]] · [[agent-clientes]] · [[agent-proveedores]] · [[agent-inventario]] · [[agent-tecnicos]] · [[agent-servicios]] · [[agent-facturacion]] · [[agent-reportes]]

## 🛠️ Otras Skills Relacionadas
- [[carmotors-rules-validator]] · [[carmotors-pr-reviewer]]
