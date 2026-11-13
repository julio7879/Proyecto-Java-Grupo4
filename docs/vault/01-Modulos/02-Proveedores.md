# Módulo 02 — Proveedores & Catálogo

## 1. Identificación
- **Rama asociada:** `feature/gestion-proveedores`
- **Agente especialista:** `@agent-proveedores`
- **Dependencias:** `feature/project-setup`
- **Requerimientos Funcionales:** RF-28, RF-29, RF-30, RF-31, RF-32
- **Reglas de Negocio:** RN-16, RN-17, RN-18
- **Historias de Usuario:** [[HU-14]], [[HU-15]]

---

## 2. Entidades del Dominio
- **`Proveedor`:** `id`, `nombre`, `telefono`, `email`, `direccion`, `activo`.
- **`ProveedorRepuesto`:** `proveedor_id` (PK, FK), `repuesto_id` (PK, FK), `costo` (BigDecimal > 0), `tiempo_entrega_dias` (>= 0), `es_habitual` (boolean).
- **`EvaluacionProveedor`:** `id`, `proveedor_id` (FK), `fecha`, `calidad` (1..5), `puntualidad` (1..5), `comentario`.

---

## 3. Reglas de Negocio Clave
- **RN-16:** Calidad y puntualidad son enteros entre 1 y 5. El puntaje de una evaluación es el promedio de ambas; el puntaje del proveedor es el promedio de todas sus evaluaciones con un decimal.
- **RN-17:** Desactivación lógica: un proveedor inactivo no admite nuevos ingresos de lotes, pierde la condición de proveedor habitual si la tenía, pero conserva su historial de lotes, evaluaciones y órdenes pasadas.
- **RN-18:** Cada repuesto tiene **como máximo un proveedor habitual** (o ninguno). Al marcar un nuevo proveedor habitual para un repuesto, cualquier habitual anterior se desmarca automáticamente en una transacción JDBC.

---

## 4. Requerimientos Funcionales
- **RF-28:** CRUD y desactivación lógica de proveedores.
- **RF-29:** Gestión de catálogo M:N proveedor-repuesto (costo > 0, tiempo de entrega, indicador habitual).
- **RF-30:** Comparación de proveedores activos que ofrecen un repuesto ordenados por costo, mostrando tiempo de entrega y promedio de evaluación (Streams / Lambdas).
- **RF-31:** Registro de evaluaciones de proveedores con calificaciones de 1 a 5.
- **RF-32:** Ranking de proveedores según promedio de evaluaciones, ordenados descendentemente con desempate por nombre; no evaluados al final como "Sin evaluar".
