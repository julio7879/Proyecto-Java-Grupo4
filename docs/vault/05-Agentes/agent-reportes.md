---
tags:
  - agente
  - reportes
  - streams
---
# Agente Especialista: `@agent-reportes`

- **Ficha del agente:** [agent-reportes.md](../../../.agents/agent-reportes.md)
- **Reglas globales:** [AGENTS.md](../../../AGENTS.md)
- **Mapa de agentes y skills:** [[00-Mapa-Agentes-Skills]]
- **Rama Git Flow:** `feature/reportes`
- **Perfil de modelo (`AGENTS.md`):** **`Orchestrator (Argon)`** + `/plan` para consultas y agregaciones críticas (`T7.2`, `T7.5`, prorrateo de mano de obra y `Streams` con `BigDecimal` `HALF_UP`).

## 📦 Módulo e Historias Asignadas
- **Módulo:** [[07-Reportes]] (`RF-21`, `RF-25`, `RF-26`, `RF-27`)
- **Historias de Usuario:** [[HU-13]] · [[HU-11]] (recordatorios de mantenimiento preventivo en 30 días)

## 🔗 Referencias Exactas en la Bóveda
- **Arquitectura:** [[Modelo-Relacional]] · [[Diagrama-Clases]] · [[Reglas-Negocio]] (`RN-12`, `RN-15`, prorrateo de productividad por técnico con asignación de centavos residuales al técnico con más horas / menor ID) · [[Requerimientos-No-Funcionales]] (Java 17 Stream API + Lambdas) · [[Principios-SOLID]]
- **Gestión:** [[Plan-Sprints]] · [[Plan-Iteraciones]] · [[Git-Flow]] · [[Definition-of-Done]]

## 🛠️ Skills y Dependencias entre Agentes
- **Skills:** [[carmotors-qmd-consultant]] · [[carmotors-rules-validator]] · [[carmotors-pr-reviewer]]
- **Depende de:** [[agent-setup]] · [[agent-database]] · [[agent-servicios]] · [[agent-facturacion]] · [[agent-clientes]] · [[agent-tecnicos]] · [[agent-inventario]]
