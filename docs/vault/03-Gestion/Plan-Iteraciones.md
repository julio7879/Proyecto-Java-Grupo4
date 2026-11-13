# Plan de las 9 Ramas Feature & Dependencias

Orden de construcción acordado en el maestro. Cada rama se abre desde `develop` y vuelve por Pull Request (ver [[Git-Flow]]).

> [!warning] Documento derivado (no editar a mano)
> Generado desde [[00-Documento-Maestro-v1.3]] por `scripts/sync-vault-from-master.ps1`.
> Cualquier corrección se hace en el maestro y se regenera: así no hay dos versiones de la verdad.

## 8. Plan de desarrollo y dependencias

Se conserva la propuesta de nueve ramas `feature/*`, ordenadas según las dependencias funcionales. El objetivo es evitar empezar una funcionalidad antes de disponer de los módulos y tablas que necesita.

| Orden | Rama | Historias / entregables | Depende de |
|---:|---|---|---|
| 1 | `feature/project-setup` | Estructura Maven, `.gitignore`, configuración, `ConnectionFactory`, `schema.sql` inicial y conexión de prueba. | — |
| 2 | `feature/gestion-clientes` | HU-09 y HU-10: clientes, vehículos, búsquedas e historial básico. | 1 |
| 3 | `feature/gestion-proveedores` | HU-14 y HU-15: proveedores, catálogo M:N, evaluación y ranking. | 1 |
| 4 | `feature/gestion-inventario` | HU-01, HU-02 y HU-03: repuestos, lotes, reservas agregadas, alertas y consulta de trazabilidad de lote. | 3 |
| 5 | `feature/gestion-tecnicos` | HU-16: técnicos, especialidades, activación y carga de trabajo. | 1 |
| 6 | `feature/gestion-servicios` | HU-04 a HU-08: órdenes, líneas, técnicos, reservas, estados, cancelación y consumo FEFO. | 2, 4 y 5 |
| 7 | `feature/facturacion` | HU-12: cabecera de factura, snapshot del cliente/vehículo, conceptos en detalles de factura, cálculos y generación/reimpresión TXT. | 6 |
| 8 | `feature/reportes` | HU-11 y HU-13: recordatorios, servicios, productividad y consumo. | 6 y 7 |
| 9 | `feature/documentacion` | README, diagrama E-R, diagrama de clases, diccionario de datos, pruebas y `data.sql` definitivo. | Todas |

### 8.1 Dependencias que deben respetarse

- Inventario depende del catálogo de proveedores porque cada lote debe validar que el proveedor ofrece el repuesto.
- Servicios depende de clientes/vehículos, inventario y técnicos: no puede crear órdenes válidas sin vehículos, reservar sin repuestos ni avanzar a En Proceso sin técnicos.
- Facturación depende de órdenes Completadas y de sus valores históricos; por eso se implementa después del módulo de servicios.
- Los reportes gerenciales dependen de fechas y datos de cierre definidos en servicios y facturación. El recordatorio preventivo depende de órdenes Entregadas.
- Las ramas 2, 3 y 5 pueden desarrollarse en paralelo si el equipo acuerda interfaces, convenciones y contratos de datos antes de integrarlas.

### 8.2 Ejemplos de commits

```text
chore: add gitignore and maven project structure
feat: add stock reservation when assigning parts to a service
fix: rollback stock deduction when service completion fails
feat: add supplier evaluation ranking
feat: add technician assignment suggestion chain
fix: prevent reserving expired lot inventory
docs: add logical data model and design decisions to readme
```

### 8.3 Integración y control de cambios

- Cada feature debe tener un objetivo acotado, criterios de aceptación y responsable identificado por el equipo.
- Antes de integrar, ejecutar compilación y pruebas relacionadas con la historia, revisar los cambios del modelo o scripts SQL y resolver conflictos.
- Los cambios de modelo que afectan a varios módulos deben comunicarse antes de modificar interfaces DAO o servicios compartidos.
- La rama `main` debe contener únicamente una versión estable que el equipo pueda ejecutar siguiendo el README.
- Si se modifica una regla, tabla, relación o historia, se actualiza este documento maestro en el mismo cambio o en un commit de documentación relacionado.
