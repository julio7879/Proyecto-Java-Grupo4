---
tags:
  - skill
  - reglas
  - calidad
---
# Skill: `carmotors-rules-validator`

- **Archivo fuente:** [SKILL.md](../../../.agents/skills/carmotors-rules-validator/SKILL.md)
- **Mapa central:** [[00-Mapa-Agentes-Skills]] · [[00-Indice-General]]
- **Reglas globales:** [AGENTS.md](../../../AGENTS.md)

## 🎯 Responsabilidad Exacta
Audita código Java para verificar el cumplimiento estricto de la arquitectura **MVC + Service + Repository (`repository` / `repository.impl`, nunca `dao`)** y las invariantes críticas v1.3 antes de dar una tarea o rama por terminada.

## 🧱 Invariantes y Documentos que Valida
1. **Arquitectura por capas:** [[Principios-SOLID]] · [[Patrones-Diseno]] · [[Diagrama-Clases]] · [[Requerimientos-No-Funcionales]]
2. **Transacciones JDBC atómicas (RN-04, RNF-08):** [[Transacciones-JDBC]] (única `Connection` por caso de uso multi-tabla, `setAutoCommit(false)`, `commit()`/`rollback()`).
3. **Reglas de negocio críticas:** [[Reglas-Negocio]] · [[00-Documento-Maestro-v1.3]]
   - FEFO determinista de 4 criterios (excluir vencidos, con caducidad antes que sin caducidad, `fecha_caducidad ASC`, `fecha_ingreso ASC`, `id ASC`).
   - `BigDecimal` con escala 2, `RoundingMode.HALF_UP` e IVA fijo **`0.19`** persistido en `facturas.tasa_iva`.
   - Snapshots inmutables en detalle de orden y factura 1:1 (`factura_snapshot` + `factura_detalles` + TXT UTF-8 post-commit).
   - Stock derivado siempre desde `lotes` (sin columna redundante en `repuestos`).
4. **Esquema relacional:** [[Modelo-Relacional]]

## 🤖 Agentes Validados con esta Skill
- **Validación crítica (`Orchestrator (Argon)`):** [[agent-inventario]] · [[agent-servicios]] · [[agent-facturacion]] · [[agent-reportes]]
- **Validación de capas y catálogo:** [[agent-setup]] · [[agent-database]] · [[agent-clientes]] · [[agent-proveedores]] · [[agent-tecnicos]]

## 🛠️ Skills Relacionadas
- [[carmotors-qmd-consultant]] · [[carmotors-pr-reviewer]]
