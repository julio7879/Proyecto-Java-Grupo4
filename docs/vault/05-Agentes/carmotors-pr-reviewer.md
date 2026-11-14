---
tags:
  - skill
  - pr-review
  - gitflow
---
# Skill: `carmotors-pr-reviewer`

- **Archivo fuente:** [SKILL.md](../../../.agents/skills/carmotors-pr-reviewer/SKILL.md)
- **Mapa central:** [[00-Mapa-Agentes-Skills]] · [[00-Indice-General]]
- **Historial y reglas:** [CHANGELOG.md](../../../CHANGELOG.md) · [AGENTS.md](../../../AGENTS.md)
- **Modelo recomendado (`AGENTS.md`):** `Gemini 4 Argon (High)` o `Claude Sonnet/Opus 5.5 (High)` para revisión integral.

## 🎯 Responsabilidad Exacta
Revisa y audita Pull Requests en CarMotors SGT antes de aprobar su integración en `develop`, combinando verificación técnica, cumplimiento de negocio v1.3, flujo Git y actualización obligatoria del historial del proyecto.

## ✅ Lista de Referencias que Audita
- **Flujo de ramas y commits:** [[Git-Flow]] · [[Plan-Iteraciones]] · [[Plan-Sprints]]
- **Definición de Terminado:** [[Definition-of-Done]]
- **Reglas técnicas delegadas a:** [[carmotors-rules-validator]] y [[carmotors-qmd-consultant]]
- **Arquitectura y invariantes:** [[Reglas-Negocio]] · [[Transacciones-JDBC]] · [[Requerimientos-No-Funcionales]] · [[Patrones-Diseno]]
- **Política de historial:** Todo PR integrado se documenta en [CHANGELOG.md](../../../CHANGELOG.md); **prohibido modificar `README.md` hasta `T8.4`**.
- **Compilación obligatoria:** `mvn clean compile` limpia antes de aprobar el PR.

## 🤖 Ramas de Agentes Auditadas
[[agent-setup]] · [[agent-database]] · [[agent-clientes]] · [[agent-proveedores]] · [[agent-inventario]] · [[agent-tecnicos]] · [[agent-servicios]] · [[agent-facturacion]] · [[agent-reportes]]
