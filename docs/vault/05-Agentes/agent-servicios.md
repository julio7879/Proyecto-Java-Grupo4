---
tags:
  - agente
  - servicios
  - fefo
  - state
---
# Agente Especialista: `@agent-servicios`

- **Ficha del agente:** [agent-servicios.md](../../../.agents/agent-servicios.md)
- **Reglas globales:** [AGENTS.md](../../../AGENTS.md)
- **Mapa de agentes y skills:** [[00-Mapa-Agentes-Skills]]
- **Rama Git Flow:** `feature/gestion-servicios`
- **Perfil de modelo (`AGENTS.md`):** **`Orchestrator (Argon)`** *(respaldo: `Gemini 4 Hydrogen (XHigh)` / `Gemini 4 Argon (High)`)* + `/plan` obligatorio (dificultad 4–5: patrón State, reservas, FEFO determinista de 4 criterios y transacciones JDBC multi-tabla).

## 📦 Módulo e Historias Asignadas
- **Módulo:** [[05-Servicios-Ordenes]] (`RF-08` a `RF-16`, `RF-35`)
- **Historias de Usuario:** [[HU-04]] · [[HU-05]] · [[HU-06]] · [[HU-07]] · [[HU-08]]

## 🔗 Referencias Exactas en la Bóveda
- **Arquitectura:** [[Modelo-Relacional]] (`ordenes_trabajo`, `orden_detalles`, `orden_repuestos`, `orden_tecnicos`, `consumo_lote_orden`, `servicios_catalogo`) · [[Diagrama-Clases]] · [[Patrones-Diseno]] (**State** `EstadoOrden`, **Facade** `TallerFacade`, **Observer** alertas stock) · [[Transacciones-JDBC]] (única `Connection`, `setAutoCommit(false)`) · [[Reglas-Negocio]] (FEFO 4 criterios, reservas, snapshots de línea y repuesto) · [[Requerimientos-No-Funcionales]] · [[Principios-SOLID]]
- **Gestión:** [[Plan-Sprints]] · [[Plan-Iteraciones]] · [[Git-Flow]] · [[Definition-of-Done]]

## 🛠️ Skills y Dependencias entre Agentes
- **Skills:** [[carmotors-qmd-consultant]] · [[carmotors-rules-validator]] · [[carmotors-pr-reviewer]]
- **Depende de:** [[agent-setup]] · [[agent-database]] · [[agent-clientes]] · [[agent-inventario]] · [[agent-tecnicos]]
- **Alimenta directamente a:** [[agent-facturacion]] (órdenes `Completada` → `Facturada` / `Entregada`) · [[agent-reportes]] (consumo de repuestos, ingresos de órdenes y horas trabajadas)
