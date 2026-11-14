---
tags:
  - agente
  - tecnicos
  - patrones
---
# Agente Especialista: `@agent-tecnicos`

- **Ficha del agente:** [agent-tecnicos.md](../../../.agents/agent-tecnicos.md)
- **Reglas globales:** [AGENTS.md](../../../AGENTS.md)
- **Mapa de agentes y skills:** [[00-Mapa-Agentes-Skills]]
- **Rama Git Flow:** `feature/gestion-tecnicos`
- **Perfil de modelo (`AGENTS.md`):** `Gemini 4 Hydrogen (Medium/High)` (o `Claude Sonnet 5.5 Medium`); módulo independiente apto para ejecución paralela con `/teamwork` junto a [[agent-clientes]] y [[agent-proveedores]].

## 📦 Módulo e Historias Asignadas
- **Módulo:** [[04-Tecnicos]] (`RF-33`, `RF-34`)
- **Historias de Usuario:** [[HU-16]]

## 🔗 Referencias Exactas en la Bóveda
- **Arquitectura:** [[Modelo-Relacional]] (`tecnicos`, `especialidades`) · [[Diagrama-Clases]] · [[Patrones-Diseno]] (**Chain of Responsibility**: Especialista libre → Especialista menor carga → General menor carga, desempate menor ID) · [[Principios-SOLID]] (OCP en eslabones) · [[Reglas-Negocio]] (`RN-07`, `RN-17`, `RN-19`, `RN-20`, `RN-21`) · [[Requerimientos-No-Funcionales]]
- **Gestión:** [[Plan-Sprints]] · [[Plan-Iteraciones]] · [[Git-Flow]] · [[Definition-of-Done]]

## 🛠️ Skills y Dependencias entre Agentes
- **Skills:** [[carmotors-qmd-consultant]] · [[carmotors-rules-validator]] · [[carmotors-pr-reviewer]]
- **Depende de:** [[agent-setup]] · [[agent-database]]
- **Provee capacidades a:** [[agent-servicios]] (sugerencia y validación de técnicos en líneas de orden) · [[agent-reportes]] (`RF-26` productividad y horas por técnico)
