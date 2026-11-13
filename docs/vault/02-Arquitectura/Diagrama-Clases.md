# Diagrama de Clases UML (dominio persistente)

Clases del dominio derivadas de las 20 tablas del modelo lógico. Las operaciones de negocio viven en `service`, no en estas clases.

> [!warning] Documento derivado (no editar a mano)
> Generado desde [[00-Documento-Maestro-v1.3]] por `scripts/sync-vault-from-master.ps1`.
> Cualquier corrección se hace en el maestro y se regenera: así no hay dos versiones de la verdad.

### 2.9 Diagrama de clases UML derivado del modelo lógico

El siguiente diagrama representa las **clases del dominio persistente** a partir de las 20 tablas del modelo lógico actualizado. Las claves foráneas se expresan como asociaciones UML; no se repiten como atributos de clase cuando la relación ya las representa. Las tablas intermedias M:N se modelan como clases de asociación (`DetalleTecnico`, `DetalleRepuesto` y `ProveedorRepuesto`) porque contienen atributos propios.

El diagrama se concentra en atributos y relaciones. No inventa métodos de negocio: las operaciones de aplicación se implementarán en las clases de servicio, controladores y DAOs, y deberán diseñarse durante la arquitectura Java. `TipoConceptoFactura` se representa como enumeración porque restringe los tipos de fila de factura, aunque no constituye una tabla independiente.

```mermaid
classDiagram
    direction LR
    class Cliente {
      +int id
      +String nombre
      +String documentoIdentidad
      +String telefono
      +String email
      +boolean activo
    }
    class Vehiculo {
      +int id
      +String placa
      +String marca
      +String modelo
    }
    class ServicioEstado {
      +int id
      +String nombre
    }
    class OrdenServicio {
      +int id
      +LocalDateTime fechaRecepcion
      +LocalDateTime fechaCompletado
      +LocalDateTime fechaEntrega
      +LocalDateTime fechaCancelacion
      +String descripcionProblema
    }
    class OrdenServicioDetalle {
      +int id
      +String nombreServicioSnapshot
      +BigDecimal costoManoObra
      +String observaciones
    }
    class ServicioTipo {
      +int id
      +String nombre
      +BigDecimal precioManoObraBase
      +Integer intervaloMeses
    }
    class MantenimientoTipo {
      +int id
      +String nombre
    }
    class Especialidad {
      +int id
      +String nombre
    }
    class Tecnico {
      +int id
      +String nombre
      +String documentoIdentidad
      +String telefono
      +boolean activo
    }
    class DetalleTecnico {
      +BigDecimal horasTrabajadas
    }
    class RepuestoTipo {
      +int id
      +String nombre
    }
    class Repuesto {
      +int id
      +String nombre
      +String marca
      +BigDecimal precioVenta
      +int cantidadReservada
      +int stockMinimo
      +boolean activo
    }
    class Proveedor {
      +int id
      +String nombre
      +String telefono
      +String email
      +String direccion
      +boolean activo
    }
    class ProveedorRepuesto {
      +BigDecimal costo
      +int tiempoEntregaDias
      +boolean esHabitual
    }
    class Lote {
      +int id
      +String codigoLote
      +LocalDate fechaIngreso
      +LocalDate fechaCaducidad
      +int cantidadInicial
      +int cantidadActual
    }
    class EvaluacionProveedor {
      +int id
      +LocalDate fecha
      +int calidad
      +int puntualidad
      +String comentario
    }
    class DetalleRepuesto {
      +int cantidadAsignada
      +String nombreRepuestoSnapshot
      +BigDecimal precioUnitarioVenta
    }
    class Factura {
      +int id
      +String numeroFactura
      +LocalDateTime fechaEmision
      +BigDecimal totalRepuestos
      +BigDecimal totalManoObra
      +BigDecimal subtotal
      +BigDecimal tasaIva
      +BigDecimal impuestos
      +BigDecimal totalFinal
      +boolean archivoGenerado
    }
    class FacturaSnapshot {
      +String clienteNombreSnapshot
      +String clienteDocumentoSnapshot
      +String clienteTelefonoSnapshot
      +String clienteEmailSnapshot
      +String vehiculoPlacaSnapshot
      +String vehiculoMarcaSnapshot
      +String vehiculoModeloSnapshot
    }
    class FacturaDetalle {
      +int id
      +TipoConceptoFactura tipoConcepto
      +String descripcionSnapshot
      +int cantidad
      +BigDecimal precioUnitario
      +BigDecimal subtotal
    }
    class TipoConceptoFactura {
      <<enumeration>>
      MANO_OBRA
      REPUESTO
    }

    Cliente "1" --> "0..*" Vehiculo : posee
    Vehiculo "1" --> "0..*" OrdenServicio : registra
    ServicioEstado "1" --> "0..*" OrdenServicio : clasifica
    OrdenServicio "1" *-- "1..*" OrdenServicioDetalle : contiene
    ServicioTipo "1" --> "0..*" OrdenServicioDetalle : identifica
    MantenimientoTipo "1" --> "0..*" ServicioTipo : clasifica
    Especialidad "1" --> "0..*" ServicioTipo : requiere
    Especialidad "1" --> "0..*" Tecnico : habilita
    OrdenServicioDetalle "1" --> "0..*" DetalleTecnico : asignaciones
    Tecnico "1" --> "0..*" DetalleTecnico : participa
    RepuestoTipo "1" --> "0..*" Repuesto : clasifica
    OrdenServicioDetalle "1" --> "0..*" DetalleRepuesto : reserva
    Repuesto "1" --> "0..*" DetalleRepuesto : asignado
    Proveedor "1" --> "0..*" ProveedorRepuesto : ofrece
    Repuesto "1" --> "0..*" ProveedorRepuesto : esOfrecido
    ProveedorRepuesto "1" --> "0..*" Lote : abastece
    Proveedor "1" --> "0..*" EvaluacionProveedor : recibe
    OrdenServicio "1" --> "0..1" Factura : genera
    Factura "1" *-- "1" FacturaSnapshot : conserva
    Factura "1" *-- "1..*" FacturaDetalle : desglosa
    OrdenServicioDetalle "1" --> "0..*" FacturaDetalle : origina
    DetalleRepuesto "0..1" --> "0..1" FacturaDetalle : respaldaConcepto
    FacturaDetalle ..> TipoConceptoFactura : clasifica
```

#### Criterios de lectura del UML

- Una `Factura` compone exactamente un `FacturaSnapshot` y uno o varios `FacturaDetalle`. La composición representa que esos registros históricos nacen con la factura y no tienen sentido independiente.
- Una `FacturaDetalle` siempre procede de una `OrdenServicioDetalle`. Puede ser de mano de obra o de repuesto; en el segundo caso debe corresponder a una asignación concreta de `DetalleRepuesto`.
- `DetalleTecnico`, `DetalleRepuesto` y `ProveedorRepuesto` son clases de asociación: materializan relaciones M:N y almacenan atributos que pertenecen a la relación, no solo a una de las entidades relacionadas.
- `Lote` se relaciona con `ProveedorRepuesto`, no libremente con cualquier combinación proveedor/repuesto. Así se conserva la regla de que el proveedor debe ofrecer el repuesto que ingresa.
- El diagrama UML no sustituye el diagrama lógico: el primero modela clases y asociaciones del dominio; el segundo especifica tablas, claves y restricciones de persistencia.

> [!note] Exportaciones pendientes
> Fuente editable: `02-Arquitectura/diagramas/CarMotors_Diagrama_Clases_UML_v1.3.mmd`. Los PNG/SVG se exportan desde ese archivo cuando se necesiten.
