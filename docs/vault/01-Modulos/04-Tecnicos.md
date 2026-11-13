# Módulo 04 — Técnicos & Especialidades

## 1. Identificación
- **Rama asociada:** `feature/gestion-tecnicos`
- **Agente especialista:** `@agent-tecnicos`
- **Dependencias:** `feature/project-setup`
- **Requerimientos Funcionales:** RF-33, RF-34
- **Reglas de Negocio:** RN-07, RN-19, RN-20, RN-21
- **Historias de Usuario:** [[HU-16]]
- **Patrón de Diseño aplicado:** **Chain of Responsibility** (cadena de sugerencia de técnicos)

---

## 2. Entidades del Dominio
- **`Especialidad`:** `id`, `nombre` (Mecánica general, Motor, Frenos, Eléctrica, Suspensión y dirección, Carrocería).
- **`Tecnico`:** `id`, `nombre`, `documento_identidad` (UK), `especialidad_id` (FK), `telefono`, `activo`.

---

## 3. Algoritmo y Cadena de Sugerencia (RN-20, RN-21)
La carga de trabajo se calcula como: **número de líneas asignadas en órdenes en estado Pendiente o En Proceso**.

La cadena de responsabilidad evalúa en estricto orden y se detiene al hallar candidatos:
1. **Eslabón 1 — Especialista Libre:** Técnico activo con la especialidad requerida por el tipo de servicio y con carga igual a 0.
2. **Eslabón 2 — Especialista con Menor Carga:** Técnico activo con la especialidad requerida y menor carga actual.
3. **Eslabón 3 — Técnico General con Menor Carga:** Cualquier técnico activo disponible con la menor carga.
*Criterio de desempate determinista:* En caso de igual carga, se selecciona el técnico con **menor ID**.

---

## 4. Reglas de Negocio Clave
- **RN-19:** Si se asigna un técnico cuya especialidad no coincide con la requerida (por selección manual o por haber llegado al Eslabón 3), el sistema emite una advertencia explícita y exige confirmación del administrador.
- **RN-07:** Para que una orden avance a **En Proceso**, cada una de sus líneas de servicio debe tener al menos un técnico activo asignado.
- **RN-17:** Desactivación lógica: un técnico con historial se desactiva, impidiendo nuevas asignaciones o sugerencias, pero conservando su histórico de horas y productividad.

---

## 5. Requerimientos Funcionales
- **RF-33:** CRUD y desactivación de técnicos vinculados a una especialidad.
- **RF-34:** Consulta de líneas de trabajo actuales asignadas a un técnico (órdenes activas).
