---
name: carmotors-rules-validator
description: Valida que las implementaciones en Java cumplan con las reglas de negocio críticas de CarMotors (FEFO, transacciones JDBC, inmutabilidad de snapshots, cálculo de IVA con BigDecimal).
---

# Skill: CarMotors Rules Validator

Esta skill contiene las listas de verificación obligatorias que los agentes deben aplicar antes de generar o aprobar código en los Pull Requests de CarMotors.

## 1. Validación de Transacciones JDBC (RN-04, RNF-08)
- [ ] La operación compuesta obtiene una única instancia de `Connection`.
- [ ] Se desactiva el commit automático: `conn.setAutoCommit(false)`.
- [ ] La **misma conexión** `conn` se pasa como parámetro a todos los DAOs participantes.
- [ ] Bloque `try { ... conn.commit(); } catch (SQLException e) { conn.rollback(); throw e; } finally { conn.setAutoCommit(true); conn.close(); }`.

## 2. Validación Financiera (RN-12, RNF-15)
- [ ] Ningún importe monetario utiliza `double` ni `float`.
- [ ] Todos los montos son `BigDecimal`.
- [ ] Todos los redondeos usan `.setScale(2, RoundingMode.HALF_UP)`.
- [ ] Tasa de IVA: constante `BigDecimal.valueOf(0.19)`.

## 3. Validación de Descuento FEFO (RN-03)
- [ ] Excluye lotes vencidos ($\text{caducidad} < \text{hoy}$).
- [ ] Orden de consumo:
  1. Lotes con caducidad antes que lotes sin caducidad.
  2. Caducidad más próxima primero.
  3. Desempate: fecha de ingreso más antigua.
  4. Desempate final: menor ID de lote.
- [ ] Decrementa `lotes.cantidad_actual` y `repuestos.cantidad_reservada` en la misma transacción.

## 4. Validación de Snapshots (RN-11, RN-12, Secc. 2.7)
- [ ] Al asignar, se copian los valores vigentes a `nombre_repuesto_snapshot` y `precio_unitario_venta`.
- [ ] Al facturar se inserta exactamente un `factura_snapshot` y al menos un `factura_detalles` (MANO_OBRA y/o REPUESTO), con descripción, cantidad y precio congelados.
- [ ] Los totales de `facturas` se derivan de la suma de subtotales de `factura_detalles`, no del catálogo actual.
- [ ] Las líneas de una orden `Completada` no admiten modificaciones ni eliminación.
