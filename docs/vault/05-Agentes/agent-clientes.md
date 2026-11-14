---
tags:
  - agente
  - clientes
  - vehiculos
---
# Agente Especialista: `@agent-clientes`

- **Ficha del agente:** [agent-clientes.md](../../../.agents/agent-clientes.md)
- **Reglas globales:** [AGENTS.md](../../../AGENTS.md)
- **Mapa de agentes y skills:** [[00-Mapa-Agentes-Skills]]
- **Rama Git Flow:** `feature/gestion-clientes`
- **Perfil de modelo (`AGENTS.md`):** `Gemini 4 Hydrogen (Medium/High)` (o `Claude Sonnet 5.5 Medium`); módulo independiente apto para ejecución paralela con `/teamwork` junto a [[agent-proveedores]] y [[agent-tecnicos]].

## 📦 Módulo e Historias Asignadas
- **Módulo:** [[01-Clientes-Vehiculos]] (`RF-17` a `RF-21`)
- **Historias de Usuario:** [[HU-09]] · [[HU-10]] · [[HU-11]]

## 🔗 Referencias Exactas en la Bóveda
- **Arquitectura:** [[Modelo-Relacional]] (`clientes`, `vehiculos`) · [[Diagrama-Clases]] · [[Reglas-Negocio]] (`RN-13`, `RN-15`, `RN-17`) · [[Requerimientos-No-Funcionales]] · [[Principios-SOLID]]
- **Gestión:** [[Plan-Sprints]] · [[Plan-Iteraciones]] · [[Git-Flow]] · [[Definition-of-Done]]

## 🛠️ Skills y Dependencias entre Agentes
- **Skills:** [[carmotors-qmd-consultant]] · [[carmotors-rules-validator]] · [[carmotors-pr-reviewer]]
- **Depende de:** [[agent-setup]] · [[agent-database]]
- **Provee contexto a:** [[agent-servicios]] (validación de cliente activo y vehículo en órdenes) · [[agent-reportes]] (`RF-21` / [[HU-11]] recordatorios preventivos a 30 días)
