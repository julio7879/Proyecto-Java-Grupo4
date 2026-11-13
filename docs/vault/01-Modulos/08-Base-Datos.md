# Módulo 08 — Base de Datos & Dataset de Prueba

## 1. Identificación
- **Rama asociada:** `feature/documentacion` (y transversal durante todo el desarrollo)
- **Agente especialista:** `@agent-database`
- **Archivos fuente:** `database/schema.sql` y `database/data.sql`
- **Requerimientos no funcionales:** RNF-01, RNF-06, RNF-07, RNF-08, RNF-21, RNF-25
- **Modelo de referencia:** 20 tablas descritas en [[Modelo-Relacional]] y [[Diagrama-Clases]] (v1.3, facturación en cabecera + snapshot + conceptos)

---

## 2. Convenciones de Diseño DDL (MySQL 8)
- Motor: `InnoDB` obligatorio para soporte de transacciones ACID y Foreign Keys.
- Charset: `utf8mb4` con collation `utf8mb4_unicode_ci`.
- Tipos de datos estandarizados:
  - Identificadores: `INT AUTO_INCREMENT PRIMARY KEY`.
  - Importes monetarios: `DECIMAL(12, 2) NOT NULL`.
  - Horas: `DECIMAL(5, 2) NOT NULL DEFAULT 0.00`.
  - Fechas: `DATE` (caducidad, ingreso) o `DATETIME` (recepción, emisión).
  - Estados/Booleans: `TINYINT(1)` o `BOOLEAN`.

---

## 3. Dataset Representativo para `data.sql` (Sección 9)
Para cumplir con los criterios de la rúbrica y las pruebas del equipo:

| Entidad | Cantidad Mínima | Casos Límite a Cubrir |
|---|---:|---|
| `clientes` | 12 | Clientes con múltiples vehículos, cliente inactivo, cliente sin órdenes |
| `vehiculos` | 18 | Placas normalizadas, vehículos con historial |
| `proveedores` | 8 | Proveedor habitual, proveedor sin evaluar, proveedor inactivo |
| `repuestos` | 20 | Stock bajo, stock en mínimo, sobre mínimo |
| `lotes` | 30 | Lote vencido con saldo, lote por vencer (< 30 días), lote sin caducidad |
| `tecnicos` | 6 | Especialistas libres (carga 0), técnicos con carga empatada |
| `especialidades` | 5 | 5 especialidades maestras |
| `servicio_tipos` | 8 | Preventivos con intervalo > 0, correctivos con intervalo NULL |
| `ordenes_servicio` | 12 | 3 Pendientes, 2 En Proceso, 2 Completadas, 1 Facturada, 3 Entregadas, 1 Cancelada |
| `facturas` | 4 | 1 para la Facturada y 3 para las Entregadas (integridad estricta) |
| `factura_snapshot` | 4 | Exactamente una por factura (PK = FK); cliente y vehículo históricos |
| `factura_detalles` | 20 | Conceptos `MANO_OBRA` y `REPUESTO`; incluir una factura con solo mano de obra |
| `evaluaciones_proveedor` | 12 | Puntajes del 1 al 5 variados |
