# Módulo 06 — Facturación Interna

## 1. Identificación
- **Rama asociada:** `feature/facturacion`
- **Agente especialista:** `@agent-facturacion`
- **Dependencias:** `feature/gestion-servicios`
- **Requerimientos Funcionales:** RF-22, RF-23, RF-24
- **Reglas de Negocio:** RN-09, RN-10, RN-11, RN-12
- **Historias de Usuario:** HU-12
- **Patrón de Diseño aplicado:** **Builder** (construcción paso a paso del archivo TXT de factura)

---

## 2. Entidad del Dominio
- **`Factura`:**
  - `id` (PK), `numero_factura` (UK, formato sugerido: `FAC-YYYYMM-XXXX`).
  - `orden_id` (FK, UK — relación 1:1 estricta).
  - `fecha_emision` (datetime).
  - Snapshots cliente: `cliente_nombre_snapshot`, `cliente_documento_snapshot`, `cliente_telefono_snapshot`, `cliente_email_snapshot`.
  - Snapshots vehículo: `vehiculo_placa_snapshot`, `vehiculo_marca_snapshot`, `vehiculo_modelo_snapshot`.
  - Totales financieros (`BigDecimal`, 2 decimales, `HALF_UP`):
    - `total_repuestos`
    - `total_mano_obra`
    - `tasa_iva` (0.19 fijo)
    - `impuestos`
    - `total_final`
  - `archivo_generado` (boolean).

---

## 3. Reglas de Cálculo e Inmutabilidad (RN-12)
$$\text{base} = \text{total\_repuestos} + \text{total\_mano\_obra}$$
$$\text{impuestos} = \text{base} \times 0.19$$
$$\text{total\_final} = \text{base} + \text{impuestos}$$

- **Snapshot total:** Si el cliente cambia de teléfono o un repuesto sube de precio en el catálogo años después, la factura reimpresa **no cambia**.
- **Generación TXT:**
  1. La factura se persiste en MySQL y la orden pasa a `Facturado` en una transacción JDBC.
  2. Tras el `commit`, se escribe el archivo `factura_<numero_factura>.txt` en la carpeta `output/` en codificación **UTF-8**.
  3. Si la escritura del archivo falla, la factura queda guardada (`archivo_generado = false`) y el administrador puede reintentar la reimpresión sin duplicar el registro.
