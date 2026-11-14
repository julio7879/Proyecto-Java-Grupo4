---
tags:
  - moc
  - agentes
  - skills
  - arquitectura
---
# Mapa Visual de Agentes Especialistas y Skills (CarMotors SGT)

Este nodo conecta el sistema multi-agente y las skills de Antigravity (ubicadas en `.agents/`) dentro de la bóveda Obsidian (`docs/vault/`) para visualizar en la **Vista Gráfica (`Ctrl+G`)** exactamente qué módulo, arquitectura, historias de usuario y documentación gestiona cada agente.

- **Índice principal:** [[00-Indice-General]]
- **Fuente de verdad funcional:** [[00-Documento-Maestro-v1.3]]
- **Reglas globales del repositorio:** [AGENTS.md](../../../AGENTS.md) · [README de agentes](../../../.agents/README.md) · [CHANGELOG.md](../../../CHANGELOG.md)

---

## ⚙️ Matriz de Agentes Especialistas (9 Módulos)

| Agente en Bóveda | Ficha real | Módulo | Rama Git Flow | Perfil / Modelo recomendado | Historias (HU) | Dependencias clave |
|---|---|---|---|---|---|---|
| [[agent-setup]] | [agent-setup.md](../../../.agents/agent-setup.md) | [[00-Setup]] | `feature/project-setup` | `Gemini 4 Hydrogen (Medium/High)` | Infraestructura base | [[agent-database]] |
| [[agent-database]] | [agent-database.md](../../../.agents/agent-database.md) | [[08-Base-Datos]] | `feature/project-setup` | `Gemini 4 Hydrogen (Medium/High)` | Esquema y dataset base | [[agent-setup]] |
| [[agent-clientes]] | [agent-clientes.md](../../../.agents/agent-clientes.md) | [[01-Clientes-Vehiculos]] | `feature/gestion-clientes` | `Gemini 4 Hydrogen (Medium/High)` (`/teamwork`) | [[HU-09]], [[HU-10]], [[HU-11]] | [[agent-setup]], [[agent-database]] |
| [[agent-proveedores]] | [agent-proveedores.md](../../../.agents/agent-proveedores.md) | [[02-Proveedores]] | `feature/gestion-proveedores` | `Gemini 4 Hydrogen (Medium/High)` (`/teamwork`) | [[HU-14]], [[HU-15]] | [[agent-setup]], [[agent-database]] |
| [[agent-tecnicos]] | [agent-tecnicos.md](../../../.agents/agent-tecnicos.md) | [[04-Tecnicos]] | `feature/gestion-tecnicos` | `Gemini 4 Hydrogen (Medium/High)` (`/teamwork`) | [[HU-16]] | [[agent-setup]], [[agent-database]] |
| [[agent-inventario]] | [agent-inventario.md](../../../.agents/agent-inventario.md) | [[03-Inventario]] | `feature/gestion-inventario` | `Orchestrator (Argon)` (`T3.5` + `/plan`) / `Hydrogen` | [[HU-01]], [[HU-02]], [[HU-03]] | [[agent-proveedores]] |
| [[agent-servicios]] | [agent-servicios.md](../../../.agents/agent-servicios.md) | [[05-Servicios-Ordenes]] | `feature/gestion-servicios` | `Orchestrator (Argon)` + `/plan` | [[HU-04]], [[HU-05]], [[HU-06]], [[HU-07]], [[HU-08]] | [[agent-clientes]], [[agent-inventario]], [[agent-tecnicos]] |
| [[agent-facturacion]] | [agent-facturacion.md](../../../.agents/agent-facturacion.md) | [[06-Facturacion]] | `feature/facturacion` | `Orchestrator (Argon)` + `/plan` | [[HU-12]] | [[agent-servicios]] |
| [[agent-reportes]] | [agent-reportes.md](../../../.agents/agent-reportes.md) | [[07-Reportes]] | `feature/reportes` | `Orchestrator (Argon)` + `/plan` (`T7.2`, `T7.5`) | [[HU-13]], [[HU-11]] | [[agent-servicios]], [[agent-facturacion]], [[agent-clientes]], [[agent-tecnicos]] |

---

## 🛠️ Skills del Proyecto (`.agents/skills/`)

| Skill en Bóveda | Definición real | Propósito en el flujo de trabajo | Referencias principales |
|---|---|---|---|
| [[carmotors-qmd-consultant]] | [SKILL.md](../../../.agents/skills/carmotors-qmd-consultant/SKILL.md) | Consulta rápida sobre QMD (`carmotors-vault`), especificaciones v1.3 y reparto de sprints antes de codificar | [[00-Documento-Maestro-v1.3]], [[Plan-Sprints]], [[Reglas-Negocio]] |
| [[carmotors-rules-validator]] | [SKILL.md](../../../.agents/skills/carmotors-rules-validator/SKILL.md) | Verifica invariantes Java: arquitectura `repository`, FEFO 4 criterios, JDBC atómico, snapshots, `BigDecimal` + IVA `0.19` | [[Reglas-Negocio]], [[Transacciones-JDBC]], [[Patrones-Diseno]], [[Principios-SOLID]], [[Requerimientos-No-Funcionales]] |
| [[carmotors-pr-reviewer]] | [SKILL.md](../../../.agents/skills/carmotors-pr-reviewer/SKILL.md) | Auditoría de PR hacia `develop`, validación de reglas v1.3, `mvn clean compile`, Git Flow y registro obligatorio en `CHANGELOG.md` | [[Git-Flow]], [[Definition-of-Done]], [[Plan-Iteraciones]], [[Plan-Sprints]] |

---

## 🧭 Ejes Conectados en el Grafo

- **Arquitectura:** [[Modelo-Relacional]] · [[Diagrama-Clases]] · [[Transacciones-JDBC]] · [[Patrones-Diseno]] · [[Reglas-Negocio]] · [[Requerimientos-No-Funcionales]] · [[Principios-SOLID]]
- **Gestión:** [[Plan-Sprints]] · [[Plan-Iteraciones]] · [[Git-Flow]] · [[Definition-of-Done]]
