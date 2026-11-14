---
tags:
  - agente
  - proveedores
---
# Agente Especialista: `@agent-proveedores`

- **Ficha del agente:** [agent-proveedores.md](../../../.agents/agent-proveedores.md)
- **Reglas globales:** [AGENTS.md](../../../AGENTS.md)
- **Mapa de agentes y skills:** [[00-Mapa-Agentes-Skills]]
- **Rama Git Flow:** `feature/gestion-proveedores`
- **Perfil de modelo (`AGENTS.md`):** `Gemini 4 Hydrogen (Medium/High)` (o `Claude Sonnet 5.5 Medium`); módulo independiente apto para ejecución paralela con `/teamwork` junto a [[agent-clientes]] y [[agent-tecnicos]].

## 📦 Módulo e Historias Asignadas
- **Módulo:** [[02-Proveedores]] (`RF-28` a `RF-32`)
- **Historias de Usuario:** [[HU-14]] · [[HU-15]]

## 🔗 Referencias Exactas en la Bóveda
- **Arquitectura:** [[Modelo-Relacional]] (`proveedores`, `proveedor_repuesto`, `evaluaciones_proveedor`) · [[Diagrama-Clases]] · [[Transacciones-JDBC]] (`RN-18` unicidad transaccional de proveedor habitual) · [[Reglas-Negocio]] (`RN-16`, `RN-17`, `RN-18`) · [[Requerimientos-No-Funcionales]] · [[Principios-SOLID]]
- **Gestión:** [[Plan-Sprints]] · [[Plan-Iteraciones]] · [[Git-Flow]] · [[Definition-of-Done]]

## 🛠️ Skills y Dependencias entre Agentes
- **Skills:** [[carmotors-qmd-consultant]] · [[carmotors-rules-validator]] · [[carmotors-pr-reviewer]]
- **Depende de:** [[agent-setup]] · [[agent-database]]
- **Desbloquea a:** [[agent-inventario]] (validación de catálogo activo `proveedor_repuesto` al ingresar lotes y alertas de proveedor habitual)
