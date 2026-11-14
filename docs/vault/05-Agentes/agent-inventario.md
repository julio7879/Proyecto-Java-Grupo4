---
tags:
  - agente
  - inventario
  - fefo
---
# Agente Especialista: `@agent-inventario`

- **Ficha del agente:** [agent-inventario.md](../../../.agents/agent-inventario.md)
- **Reglas globales:** [AGENTS.md](../../../AGENTS.md)
- **Mapa de agentes y skills:** [[00-Mapa-Agentes-Skills]]
- **Rama Git Flow:** `feature/gestion-inventario`
- **Perfil de modelo (`AGENTS.md`):** **`Orchestrator (Argon)`** + `/plan` para cálculo crítico de stock derivado/alertas (`T3.5`); `Gemini 4 Hydrogen (Medium/High)` para CRUDs y vistas (`T3.4`–`T3.8`).

## 📦 Módulo e Historias Asignadas
- **Módulo:** [[03-Inventario]] (`RF-01` a `RF-07`)
- **Historias de Usuario:** [[HU-01]] · [[HU-02]] · [[HU-03]]

## 🔗 Referencias Exactas en la Bóveda
- **Arquitectura:** [[Modelo-Relacional]] (`repuestos`, `repuesto_tipos`, `lotes`) · [[Diagrama-Clases]] · [[Reglas-Negocio]] (`RN-01`, `RN-02`, `RN-14`, `RN-17`, `RN-22`) · [[Patrones-Diseno]] (Observer de stock bajo / déficit de reservas) · [[Requerimientos-No-Funcionales]] · [[Principios-SOLID]]
- **Gestión:** [[Plan-Sprints]] · [[Plan-Iteraciones]] · [[Git-Flow]] · [[Definition-of-Done]]

## 🛠️ Skills y Dependencias entre Agentes
- **Skills:** [[carmotors-qmd-consultant]] · [[carmotors-rules-validator]] · [[carmotors-pr-reviewer]]
- **Depende de:** [[agent-setup]] · [[agent-database]] · [[agent-proveedores]] (catálogo M:N activo al ingresar lotes)
- **Colabora con:** [[agent-servicios]] (disponibilidad operativa, incremento/liberación de `cantidad_reservada` y base de lotes para consumo FEFO) · [[agent-reportes]]
