# Principios SOLID en CarMotors

Aplicación concreta de los principios SOLID según la rúbrica académica:

| Principio | Definición en CarMotors | Ejemplo en el Código |
|---|---|---|
| **S — Single Responsibility (SRP)** | Cada clase tiene un único motivo para cambiar. | `OrdenServicioService` gestiona reglas del negocio; `OrdenServicioDAO` maneja SQL; `ConsolaMenuView` formatea salida; `FacturaTXTBuilder` genera texto. |
| **O — Open / Closed (OCP)** | Clases abiertas a extensión, cerradas a modificación. | La cadena de sugerencia `SugerenciaTecnicoHandler` o los estados `EstadoOrden` permiten agregar nuevos criterios o estados heredando una interfaz sin tocar el código existente. |
| **L — Liskov Substitution (LSP)** | Objetos de un programa deben poder ser reemplazados por subtipos sin alterar la corrección. | Cualquier implementación de `RepuestoDAO` puede usarse dentro de `InventarioService` sin romper su comportamiento. Todos los `EstadoOrden` respetan su contrato común. |
| **I — Interface Segregation (ISP)** | Muchas interfaces específicas son mejores que una interfaz general. | Interfaces pequeñas y enfocadas (`RepuestoDAO`, `LoteDAO`, `StockNotifier`) en lugar de un `MegasystemRepository` con 40 métodos dispares. |
| **D — Dependency Inversion (DIP)** | Los módulos de alto nivel no deben depender de los de bajo nivel; ambos dependen de abstracciones. | `InventarioService` recibe `RepuestoDAO` por constructor (inyección de dependencias manual), no instancia directamente `new RepuestoDAOImpl()`. |
