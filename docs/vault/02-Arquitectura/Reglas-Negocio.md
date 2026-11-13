# Reglas de Negocio Maestras (RN-01 a RN-22)

Este documento centraliza las 22 reglas de negocio del Sistema de Gestión de Taller CarMotors (versión 1.3, con la facturación separada en cabecera, snapshot y conceptos). Toda regla debe residir en la capa `service`.

| ID | Regla | Módulo Principal |
|---|---|---|
| **RN-01** | Solo se puede reservar repuestos si $\text{stock operativo} - \text{reservas vigentes} \ge \text{solicitado}$. Lotes vencidos quedan excluidos. | Inventario / Servicios |
| **RN-02** | Al asignar un repuesto a una línea, se incrementa `repuestos.cantidad_reservada`. Las existencias físicas de repuestos y lotes **no** varían. | Inventario / Servicios |
| **RN-03** | Al completar una orden, el consumo físico se descuenta de lotes válidos usando **FEFO determinista** (fecha más próxima -> fecha ingreso más antigua -> menor ID). Se reduce `repuestos.cantidad_reservada` y `lotes.cantidad_actual`. | Servicios / Inventario |
| **RN-04** | El descuento de inventario multi-línea y el cambio a Completado se ejecutan en **una sola transacción JDBC con rollback ante fallos**. | Servicios / Arquitectura |
| **RN-05** | Retirar repuesto, retirar línea permitida o cancelar orden libera exactamente las reservas. Completadas, Facturadas o Canceladas no admiten cambios. | Servicios |
| **RN-06** | Flujo unidireccional: Pendiente -> En Proceso -> Completado -> Facturado -> Entregado. Cancelada es salida excepcional desde Pendiente o En Proceso. | Servicios (State) |
| **RN-07** | Para avanzar a En Proceso, cada línea debe tener asignado al menos un técnico activo. | Técnicos / Servicios |
| **RN-08** | Cada línea debe tener costo de mano de obra $\ge 0$ antes de completar la orden. Inicia con `precio_mano_obra_base`. | Servicios |
| **RN-09** | Solo se factura una orden Completada y una sola vez. En una misma transacción se guarda una fila en `facturas`, exactamente una en `factura_snapshot`, una o más en `factura_detalles` y se cambia la orden a Facturado. Si falla cualquiera de estas operaciones, se revierte todo. | Facturación |
| **RN-10** | Solo se entrega una orden Facturada, registrando `fecha_entrega`. | Servicios |
| **RN-11** | El precio de venta del repuesto se congela en `precio_unitario_venta` y `nombre_repuesto_snapshot` al asignarlo. | Servicios / Facturación |
| **RN-12** | $\text{total\_repuestos} = \sum \text{subtotal}$ de los conceptos `REPUESTO`; $\text{total\_mano\_obra} = \sum \text{subtotal}$ de los conceptos `MANO_OBRA`; $\text{subtotal} = \text{total\_repuestos} + \text{total\_mano\_obra}$; $\text{impuestos} = \text{subtotal} \times 0.19$; $\text{total\_final} = \text{subtotal} + \text{impuestos}$. Cada línea se copia desde los valores históricos de la orden a `factura_detalles`. Manejo con `BigDecimal`, 2 decimales y `HALF_UP`. | Facturación |
| **RN-13** | Documento y email de cliente son únicos y obligatorios; placa es única y normalizada. | Clientes y Vehículos |
| **RN-14** | `codigo_lote` es único. Caducidad posterior al ingreso. El proveedor debe ofrecer el repuesto y estar activo. Lotes vencidos no se asignan ni consumen. | Inventario / Proveedores |
| **RN-15** | Recordatorio preventivo: $\text{fecha sugerida} = \text{fecha\_entrega} + \text{intervalo\_meses}$. Alerta a 30 días o vencidos. | Clientes / Reportes |
| **RN-16** | Evaluaciones de proveedores de 1 a 5 en calidad y puntualidad. Promedios con 1 decimal. | Proveedores |
| **RN-17** | No eliminación física: clientes, repuestos, técnicos y proveedores se desactivan lógicamente preservando historial. | Transversal |
| **RN-18** | Toda orden tiene al menos una línea. Cada repuesto tiene máximo un proveedor habitual (activo). | Servicios / Proveedores |
| **RN-19** | Asignación de técnico con especialidad no coincidente requiere advertencia explícita y confirmación del administrador. | Técnicos / Servicios |
| **RN-20** | Cadena de sugerencia de técnicos: 1) Especialista libre, 2) Especialista con menor carga, 3) General con menor carga. | Técnicos (Chain of Resp.) |
| **RN-21** | Carga de trabajo = número de líneas en órdenes Pendientes o En Proceso. Desempate por menor ID. | Técnicos |
| **RN-22** | Alerta automática de stock bajo si tras un consumo el disponible queda $\le \text{stock\_minimo}$. | Inventario |
