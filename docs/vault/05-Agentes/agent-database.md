---
tags:
  - agente
  - database
  - mysql
---
# Agente Especialista: `@agent-database`

- **Ficha del agente:** [agent-database.md](../../../.agents/agent-database.md)
- **Reglas globales:** [AGENTS.md](../../../AGENTS.md)
- **Mapa de agentes y skills:** [[00-Mapa-Agentes-Skills]]
- **Rama Git Flow:** `feature/project-setup`
- **Perfil de modelo (`AGENTS.md`):** `Gemini 4 Hydrogen (Medium/High)` para DDL/DML base (`schema.sql`, `data.sql`) y `Orchestrator (Argon)` si revisa integridad referencial transversal multi-módulo.

## 📦 Módulo Asignado
- [[08-Base-Datos]] — Script DDL (`database/schema.sql`) y dataset semilla (`database/data.sql`) alineados con MySQL 8 e invariantes v1.3 (sin columna redundante `cantidad_stock` en `repuestos`; `tasa_iva` en `facturas`; constraints e índices).

## 🔗 Referencias Exactas en la Bóveda
- **Arquitectura:** [[Modelo-Relacional]] · [[Transacciones-JDBC]] · [[Reglas-Negocio]] · [[Requerimientos-No-Funcionales]]
- **Gestión:** [[Plan-Sprints]] · [[Plan-Iteraciones]] · [[Git-Flow]] · [[Definition-of-Done]]
- **Fuente maestra:** [[00-Documento-Maestro-v1.3]]

## 🛠️ Skills y Dependencias entre Agentes
- **Skills:** [[carmotors-qmd-consultant]] · [[carmotors-rules-validator]] · [[carmotors-pr-reviewer]]
- **Colabora con:** [[agent-setup]] (conexión JDBC)
- **Da soporte de esquema a todos los dominios:** [[agent-clientes]] · [[agent-proveedores]] · [[agent-inventario]] · [[agent-tecnicos]] · [[agent-servicios]] · [[agent-facturacion]] · [[agent-reportes]]
