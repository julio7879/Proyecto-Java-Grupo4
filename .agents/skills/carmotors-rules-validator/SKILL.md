---
name: carmotors-rules-validator
description: Valida que las implementaciones en Java cumplan con las reglas de negocio críticas de CarMotors v1.3 (arquitectura con repository, FEFO de 4 criterios, transacciones JDBC, inmutabilidad de snapshots, cálculo de IVA fijo 0.19 con BigDecimal).
---

# Skill: CarMotors Rules Validator

Esta skill contiene las listas de verificación obligatorias que los agentes deben aplicar antes de generar o aprobar código en **CarMotors SGT** (`com.carmotors.sgt`).

> **Modelo recomendado al validar módulos críticos:** `Orchestrator (Argon)` o `Gemini 4 Argon (High)`.

## 1. Arquitectura de Paquetes (RNF-02 a RNF-06)
- [ ] Capas estrictas: `view` → `controller` → `service` → `repository` / `repository.impl` (NO existe paquete `dao`).
- [ ] Todo SQL parametrizable en `repository.impl` usa `PreparedStatement`.
- [ ] Excepciones propias (`ConfiguracionException` y jerarquía de dominio definida en `T1.2`), sin `System.exit()` ni `catch` vacíos.

## 2. Validación de Transacciones JDBC (RN-04, RNF-08)
- [ ] La operación compuesta (2 o más tablas) obtiene una única instancia de `Connection` desde `ConnectionFactory.getInstance().getConnection()`.
- [ ] Se desactiva el commit automático: `conn.setAutoCommit(false)`.
- [ ] La **misma conexión** `conn` se pasa como parámetro a todos los repositorios participantes.
- [ ] Bloque `try { ... conn.commit(); } catch (SQLException e) { conn.rollback(); throw ...; } finally { conn.setAutoCommit(true); conn.close(); }` (o equivalente seguro sin fugas de conexión).

## 3. Validación Financiera (RN-12, RNF-15, §2.8 v1.3)
- [ ] Ningún importe monetario utiliza `double` ni `float`.
- [ ] Todos los montos y horas usan `BigDecimal` (dinero con escala 2 y `RoundingMode.HALF_UP`).
- [ ] Tasa de IVA fija académica: constante formal `new BigDecimal("0.19")` almacenada en `facturas.tasa_iva` (no proviene de `config.properties`).

## 4. Validación de Descuento FEFO (RN-03, §2.6 v1.3)
- [ ] Excluye lotes vencidos (`l.fecha_caducidad IS NULL OR l.fecha_caducidad >= CURDATE()`).
- [ ] Orden determinista obligatorio (4 criterios):
  1. Lotes con caducidad antes que lotes sin caducidad (`CASE WHEN l.fecha_caducidad IS NULL THEN 1 ELSE 0 END ASC`).
  2. Caducidad más próxima primero (`l.fecha_caducidad ASC`).
  3. Desempate: fecha de ingreso más antigua (`l.fecha_ingreso ASC`).
  4. Desempate final: menor ID de lote (`l.id ASC`).
- [ ] Decrementa `lotes.cantidad_actual` y `repuestos.cantidad_reservada` en la misma transacción; nunca crea ni lee `repuestos.cantidad_stock`.

## 5. Validación de Snapshots e Inmutabilidad (RN-09, RN-11, §2.7 v1.3)
- [ ] Al asignar servicios/repuestos a una orden, se congelan `nombre_servicio_snapshot`, `nombre_repuesto_snapshot` y `precio_unitario_venta`.
- [ ] Al facturar se inserta en una sola transacción: 1 fila en `facturas`, exactamente 1 en `factura_snapshot` y `>= 1` en `factura_detalles` (`MANO_OBRA` con `repuesto_id = NULL`, o `REPUESTO` con `(orden_detalle_id, repuesto_id)` válido).
- [ ] Los totales de `facturas` se derivan de la suma de subtotales de `factura_detalles`, no del catálogo actual.
- [ ] Las órdenes en estado `Completado` o posterior (y `Cancelada`) no admiten modificaciones ni eliminación de líneas/repuestos.
- [ ] El archivo `output/factura_<numero>.txt` se escribe en UTF-8 **después** del `COMMIT`.
