---
tags:
  - agente
  - facturacion
  - transacciones
---
# Agente Especialista: `@agent-facturacion`

- **Ficha del agente:** [agent-facturacion.md](../../../.agents/agent-facturacion.md)
- **Reglas globales:** [AGENTS.md](../../../AGENTS.md)
- **Mapa de agentes y skills:** [[00-Mapa-Agentes-Skills]]
- **Rama Git Flow:** `feature/facturacion`
- **Perfil de modelo (`AGENTS.md`):** **`Orchestrator (Argon)`** *(respaldo: `Gemini 4 Hydrogen (XHigh)` / `Gemini 4 Argon (High)`)* + `/plan` obligatorio (dificultad 4–5: transacción atómica multi-tabla, snapshot 1:1 inmutable y cálculos financieros con `BigDecimal`).

## 📦 Módulo e Historias Asignadas
- **Módulo:** [[06-Facturacion]] (`RF-22` a `RF-24`)
- **Historias de Usuario:** [[HU-12]]

## 🔗 Referencias Exactas en la Bóveda
- **Arquitectura:** [[Modelo-Relacional]] (`facturas`, `factura_snapshot`, `factura_detalles`) · [[Diagrama-Clases]] · [[Patrones-Diseno]] (**Builder** para `output/factura_<numero>.txt` UTF-8 generado tras `commit()`) · [[Transacciones-JDBC]] (`facturas` + 1 `factura_snapshot` + `>= 1` `factura_detalles` + actualización de orden a `Facturada`) · [[Reglas-Negocio]] (`RN-11` snapshots inmutables, `RN-12` `BigDecimal` escala 2 `HALF_UP` + IVA fijo **`0.19`** en `facturas.tasa_iva`) · [[Requerimientos-No-Funcionales]] · [[Principios-SOLID]]
- **Gestión:** [[Plan-Sprints]] · [[Plan-Iteraciones]] · [[Git-Flow]] · [[Definition-of-Done]]

## 🛠️ Skills y Dependencias entre Agentes
- **Skills:** [[carmotors-qmd-consultant]] · [[carmotors-rules-validator]] · [[carmotors-pr-reviewer]]
- **Depende de:** [[agent-setup]] · [[agent-database]] · [[agent-servicios]] · [[agent-clientes]]
- **Provee datos a:** [[agent-reportes]] (facturas emitidas y montos históricos)
