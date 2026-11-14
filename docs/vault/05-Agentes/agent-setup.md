---
tags:
  - agente
  - setup
  - arquitectura
---
# Agente Especialista: `@agent-setup`

- **Ficha del agente:** [agent-setup.md](../../../.agents/agent-setup.md)
- **Reglas globales:** [AGENTS.md](../../../AGENTS.md)
- **Mapa de agentes y skills:** [[00-Mapa-Agentes-Skills]]
- **Rama Git Flow:** `feature/project-setup`
- **Perfil de modelo (`AGENTS.md`):** `Gemini 4 Hydrogen (Medium/High)` (o `Claude Sonnet 5.5 Medium`); ejecución directa en cambios acotados y `/plan` si introduce contratos transversales.

## 📦 Módulo Asignado
- [[00-Setup]] — Estructura Maven Java 17 (`com.carmotors.sgt`), `ConnectionFactory` (Singleton JDBC), carga externa segura de `config.properties`, jerarquía de excepciones y utilidades base (`ConsoleReader`, validadores, fechas).

## 🔗 Referencias Exactas en la Bóveda
- **Arquitectura:** [[Patrones-Diseno]] (Singleton `ConnectionFactory`, Facade base) · [[Principios-SOLID]] · [[Transacciones-JDBC]] · [[Requerimientos-No-Funcionales]] · [[Diagrama-Clases]]
- **Gestión:** [[Plan-Sprints]] · [[Plan-Iteraciones]] · [[Git-Flow]] · [[Definition-of-Done]]
- **Fuente maestra:** [[00-Documento-Maestro-v1.3]]

## 🛠️ Skills y Colaboración
- **Skills:** [[carmotors-qmd-consultant]] · [[carmotors-rules-validator]] · [[carmotors-pr-reviewer]]
- **Colabora directamente con:** [[agent-database]] (esquema inicial y conexión MySQL 8)
- **Desbloquea el trabajo de:** [[agent-clientes]] · [[agent-proveedores]] · [[agent-tecnicos]] · [[agent-inventario]] · [[agent-servicios]] · [[agent-facturacion]] · [[agent-reportes]]
