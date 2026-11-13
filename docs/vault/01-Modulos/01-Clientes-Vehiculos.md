# Módulo 01 — Clientes y Vehículos

## 1. Identificación
- **Rama asociada:** `feature/gestion-clientes`
- **Agente especialista:** `@agent-clientes`
- **Dependencias:** `feature/project-setup`
- **Requerimientos Funcionales:** RF-17, RF-18, RF-19, RF-20, RF-21
- **Reglas de Negocio:** RN-13, RN-15, RN-17
- **Historias de Usuario:** [[HU-09]], [[HU-10]], [[HU-11]]

---

## 2. Entidades del Dominio
- **`Cliente`:** `id`, `nombre`, `documento_identidad` (UK), `telefono`, `email` (UK), `activo`.
- **`Vehiculo`:** `id`, `cliente_id` (FK), `placa` (UK normalizada sin espacios), `marca`, `modelo`.

---

## 3. Reglas de Negocio Clave
- **RN-13:** Documento y email de cliente son únicos y obligatorios; la placa del vehículo es única. La placa se normaliza a mayúsculas y sin espacios antes de validar y guardar.
- **RN-15:** El recordatorio de mantenimiento preventivo se calcula por vehículo y tipo de servicio preventivo:
  $$\text{fecha sugerida} = \text{fecha\_entrega de la última orden Entregada con ese tipo} + \text{intervalo\_meses}$$
  Se informa si ya venció o cae dentro de los próximos 30 días.
- **RN-17:** Desactivación lógica: clientes con historial no se eliminan físicamente. Un cliente inactivo no puede registrar nuevos vehículos ni abrir nuevas órdenes, pero su historial sigue consultable.

---

## 4. Requerimientos Funcionales
- **RF-17:** Registrar, consultar, actualizar y desactivar clientes con nombre, documento, teléfono y email.
- **RF-18:** Registrar y consultar vehículos asociados a un cliente activo: placa, marca y modelo.
- **RF-19:** Buscar un cliente por documento o un vehículo por placa normalizada.
- **RF-20:** Mostrar historial completo de un cliente: vehículos y órdenes asociadas ordenadas de la más reciente a la más antigua con subtotales cuando existan facturas.
- **RF-21:** Reporte de clientes con mantenimiento preventivo próximo o vencido (órdenes Entregadas).

---

## 5. Historias de Usuario
- **HU-09 — Registrar clientes y vehículos:** Validación de unicidad de documento, email y placa; rechazo de clientes inactivos para nuevas operaciones.
- **HU-10 — Consultar historial del cliente:** Lista de vehículos y órdenes cronológicas descendentes.
- **HU-11 — Recordatorios de mantenimiento:** Identificación de fechas de vencimiento preventivo a 30 días sin envíos externos automatizados.
