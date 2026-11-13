# Módulo 03 — Inventario & Lotes

## 1. Identificación
- **Rama asociada:** `feature/gestion-inventario`
- **Agente especialista:** `@agent-inventario`
- **Dependencias:** `feature/gestion-proveedores` (para validar proveedores activos en catálogo)
- **Requerimientos Funcionales:** RF-01, RF-02, RF-03, RF-04, RF-05, RF-06, RF-07
- **Reglas de Negocio:** RN-01, RN-02, RN-14, RN-17, RN-22
- **Historias de Usuario:** [[HU-01]], [[HU-02]], [[HU-03]]

---

## 2. Entidades del Dominio
- **`RepuestoTipo`:** `id`, `nombre` (Mecánico, Eléctrico, Carrocería, Consumo...).
- **`Repuesto`:** `id`, `nombre`, `repuesto_tipo_id` (FK), `marca`, `precio_venta` (BigDecimal > 0), `cantidad_reservada` (agregado operativo), `stock_minimo`, `activo`.
- **`Lote`:** `id`, `codigo_lote` (UK), `proveedor_id` (FK), `repuesto_id` (FK), `fecha_ingreso`, `fecha_caducidad` (opcional), `cantidad_inicial`, `cantidad_actual`.

---

## 3. Modelo de Fuente de Verdad y Cálculos
El stock físico y operativo **no se almacena duplicado** en `repuestos`:
- **Stock Físico:** $\sum \text{lotes.cantidad\_actual}$ (incluye reservadas y vencidas mientras estén físicas).
- **Stock Operativo:** $\sum \text{lotes.cantidad\_actual}$ donde $\text{caducidad} \ge \text{HOY} \lor \text{caducidad IS NULL}$.
- **Cantidad Reservada:** Total comprometido en órdenes Pendientes y En Proceso (`repuestos.cantidad_reservada`).
- **Disponible para nuevas reservas:** $\max(0, \text{stock\_operativo} - \text{cantidad\_reservada})$.
- **Déficit de reservas:** $\max(0, \text{cantidad\_reservada} - \text{stock\_operativo})$ (alerta crítica si un lote vence teniendo reservas).

---

## 4. Reglas de Negocio Clave
- **RN-01:** Solo se puede reservar repuestos si el stock operativo menos reservas vigentes cubre la cantidad solicitada. Lotes vencidos quedan excluidos.
- **RN-02:** Al reservar, incrementa `repuestos.cantidad_reservada`. Las existencias físicas de los lotes **no** disminuyen hasta completar la orden.
- **RN-14:** `codigo_lote` es único. Si existe fecha de caducidad, debe ser posterior a la fecha de ingreso. El proveedor debe ofrecer el repuesto en catálogo y estar activo al ingresar el lote.
- **RN-22:** Tras completar una orden y descontar stock, si el disponible queda $\le \text{stock\_minimo}$, emitir alerta con stock físico, operativo, reservado, disponible y proveedor habitual.

---

## 5. Requerimientos Funcionales
- **RF-01:** Gestión de repuestos (precios positivos, stock derivado, desactivación lógica).
- **RF-02:** Ingreso de lotes con validación de código único, proveedor activo y fecha de caducidad.
- **RF-03:** Consulta de lotes por repuesto y trazabilidad por lote (origen y saldo restante; lista primero caducidades próximas).
- **RF-04:** Búsqueda y filtrado insensible a mayúsculas/minúsculas.
- **RF-05:** Reporte de repuestos con disponible $\le \text{stock\_minimo}$.
- **RF-06:** Reporte de lotes por vencer en $N$ días (30 por defecto) o ya vencidos con saldo físico.
- **RF-07:** Consulta por repuesto de stock físico, operativo, reservado y disponible.
