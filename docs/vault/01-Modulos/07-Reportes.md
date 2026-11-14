# Módulo 07 — Reportes Gerenciales & Streams

## 1. Identificación
- **Rama asociada:** `feature/reportes`
- **Agente especialista:** [[agent-reportes|@agent-reportes]]
- **Skills obligatorias:** [[carmotors-qmd-consultant]] · [[carmotors-rules-validator]] · [[carmotors-pr-reviewer]]
- **Dependencias:** [[05-Servicios-Ordenes]] (`feature/gestion-servicios`), [[06-Facturacion]] (`feature/facturacion`)
- **Requerimientos Funcionales:** RF-21, RF-25, RF-26, RF-27
- **Reglas de Negocio:** [[Reglas-Negocio]] (`RN-12`, `RN-15`), [[00-Documento-Maestro-v1.3]] (Sección 4.2 y 4.3)
- **Arquitectura y Planificación:** [[Modelo-Relacional]] · [[Diagrama-Clases]] · [[Requerimientos-No-Funcionales]] · [[Plan-Sprints]] · [[Plan-Iteraciones]]
- **Historias de Usuario:** [[HU-13]]; [[HU-11]] (dominio de clientes, se construye en esta rama)
- **Tecnología obligatoria:** Java 17 Stream API, Lambdas, `BigDecimal` con `RoundingMode.HALF_UP`.

---

## 2. Catálogo de Reportes

### 1. Servicios Más Solicitados (RF-25)
- **Filtro:** Rango de `fecha_completado` en órdenes Completadas, Facturadas o Entregadas.
- **Métricas:** Conteo de líneas de servicio e ingresos totales por tipo de servicio (repuestos a precio histórico + mano de obra, antes de IVA).
- **Orden:** Cantidad descendente.

### 2. Productividad por Técnico (RF-26)
- **Filtro:** Rango de `fecha_completado` en órdenes Completadas o posteriores.
- **Distribución de Mano de Obra:**
  $$\text{ingreso\_tecnico} = \frac{\text{costo\_mano\_obra\_linea} \times \text{horas\_tecnico}}{\sum \text{horas\_linea}}$$
  *Centavos de redondeo:* Asignados al técnico con mayor cantidad de horas trabajadas (desempate: menor ID). Líneas sin horas registradas quedan en categoría "Sin distribuir".
- **Inclusión:** Técnicos activos sin horas figuran con valor 0.

### 3. Consumo de Repuestos (RF-27)
- **Filtro:** Rango de `fecha_completado`.
- **Métricas:** Unidades asignadas en órdenes Completadas o posteriores valorizadas al precio histórico de venta (valor comercial).
- **Orden:** Valor total descendente.

### 4. Recordatorios de Mantenimiento Preventivo (RF-21)
- Identifica vehículos cuyo servicio preventivo venció o vencerá dentro de los próximos 30 días, basado en `fecha_entrega` del último servicio del mismo tipo.
