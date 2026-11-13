# Módulo 06 — Facturación Interna

## 1. Identificación
- **Rama asociada:** `feature/facturacion`
- **Agente especialista:** `@agent-facturacion`
- **Dependencias:** `feature/gestion-servicios`
- **Requerimientos Funcionales:** RF-22, RF-23, RF-24
- **Reglas de Negocio:** RN-09, RN-10, RN-11, RN-12
- **Historias de Usuario:** [[HU-12]]
- **Patrón de Diseño aplicado:** **Builder** (construcción paso a paso del archivo TXT de factura)

> [!info] Cambio de la versión 1.3
> La factura ya **no** guarda los snapshots ni los conceptos en una sola tabla. Se separa en cabecera (`facturas`), snapshot del cliente y vehículo (`factura_snapshot`) y conceptos cobrados (`factura_detalles`). Ver [[00-Documento-Maestro-v1.3]] §2.7.

---

## 2. Entidades del Dominio (tres tablas, tres responsabilidades)

**`facturas` — cabecera financiera**
- `id` (PK), `numero_factura` (UK, formato sugerido `FAC-YYYYMM-XXXX`), `orden_id` (FK y UK: una sola factura por orden).
- `fecha_emision`, `total_repuestos`, `total_mano_obra`, `subtotal`, `tasa_iva` (0.19 fijo), `impuestos`, `total_final`, `archivo_generado`.
- No contiene snapshots ni líneas: solo importes y estado de generación.

**`factura_snapshot` — copia histórica del cliente y del vehículo**
- `factura_id` (PK y FK → `facturas`): relación obligatoria **1:1**.
- Cliente: `cliente_nombre_snapshot`, `cliente_documento_snapshot`, `cliente_telefono_snapshot`, `cliente_email_snapshot`.
- Vehículo: `vehiculo_placa_snapshot`, `vehiculo_marca_snapshot`, `vehiculo_modelo_snapshot`.

**`factura_detalles` — conceptos cobrados**
- `id` (PK), `factura_id` (FK), `orden_detalle_id` (FK), `repuesto_id` (parte de FK compuesta, nullable).
- `tipo_concepto` (`MANO_OBRA` | `REPUESTO`), `descripcion_snapshot`, `cantidad`, `precio_unitario`, `subtotal`.
- Una fila por concepto: **cada línea de servicio genera una fila de mano de obra** y **cada repuesto asignado genera una fila de repuesto**. Una línea sin repuestos factura solo su mano de obra.
- Para `MANO_OBRA`: `repuesto_id = NULL`, `cantidad = 1`, `precio_unitario = costo_mano_obra` de la línea.
- Para `REPUESTO`: `repuesto_id` obligatorio; `(orden_detalle_id, repuesto_id)` debe existir en `detalle_repuestos`; `precio_unitario = precio_unitario_venta`.

---

## 3. Reglas de Cálculo (RN-12)
$$\text{total\_repuestos} = \sum \text{subtotal}(\text{conceptos REPUESTO})$$
$$\text{total\_mano\_obra} = \sum \text{subtotal}(\text{conceptos MANO\_OBRA})$$
$$\text{subtotal} = \text{total\_repuestos} + \text{total\_mano\_obra}$$
$$\text{impuestos} = \text{subtotal} \times 0.19 \qquad \text{total\_final} = \text{subtotal} + \text{impuestos}$$

- El subtotal de cada concepto es `cantidad × precio_unitario`, con dos decimales.
- Todo con `BigDecimal`, dos decimales y `RoundingMode.HALF_UP`. Nunca `double`.
- El 19 % es un supuesto académico fijo, no una validación tributaria.

---

## 4. Inmutabilidad y Generación del TXT (RN-09)

1. Se verifica que la orden esté **Completada** y sin factura previa.
2. En **una única transacción JDBC** se inserta la cabecera, exactamente un `factura_snapshot`, uno o más `factura_detalles` y se cambia la orden a `Facturado`. Si algo falla: `ROLLBACK` completo.
3. Antes del `COMMIT` se valida que haya **exactamente un snapshot y al menos un detalle** (una FK no garantiza mínimos).
4. Tras el `COMMIT` se escribe `factura_<numero_factura>.txt` en `output/`, en **UTF-8**, y se actualiza `archivo_generado = true`.
5. Si falla la escritura, la factura queda registrada con `archivo_generado = false` y puede regenerarse desde los datos históricos. Reescribir el mismo nombre de archivo debe ser **idempotente**.
6. **Snapshot total:** si el cliente cambia de teléfono o un repuesto sube de precio, la reimpresión no cambia porque lee `facturas`, `factura_snapshot` y `factura_detalles`, nunca el catálogo actual.

---

## 5. Puntos de Cuidado en la Implementación
- **Herencia de datos:** los conceptos se copian desde `orden_servicio_detalle` y `detalle_repuestos` (valores históricos), no desde `servicio_tipos` ni `repuestos`.
- **Mínimo de un detalle:** se valida en `service`, dentro de la transacción.
- **FK compuesta opcional:** `(orden_detalle_id, repuesto_id)` → `detalle_repuestos(detalle_id, repuesto_id)`; si MySQL exige una alternativa, documentar la validación transaccional.
- **Reimpresión (RF-24):** no crea otra factura ni cambia el estado de la orden; si el TXT no existe, lo reconstruye.
