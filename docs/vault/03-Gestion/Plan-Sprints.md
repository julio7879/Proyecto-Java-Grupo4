# CarMotors SGT — Plan de sprints

> [!info] Navegación en Bóveda
> Volver a [[00-Indice-General]] · Ver dependencias de ramas en [[Plan-Iteraciones]] · Convenciones en [[Git-Flow]] y [[Definition-of-Done]].

Las tareas están ordenadas por dependencias: cada sprint solo usa lo que ya quedó terminado en los sprints anteriores. Dentro de un mismo sprint, las tareas que no dependen entre sí se pueden hacer en paralelo.

## Cómo leer este documento

- Cada sprint tiene una **tabla de reparto** (para asignar responsables) y debajo el **detalle de cada tarea**: qué incluye y cuándo se considera terminada.
- **Módulo**: el módulo del documento maestro al que pertenece la tarea. *Transversal* = lo usan todos los módulos.
- **Rama**: la rama `feature/*` del vault donde se desarrolla. Una rama puede abarcar varios sprints.
- Cuando una tarea dice **CRUD completo**, incluye todas las capas:
  - `model`: la entidad.
  - `repository`: interfaz DAO + implementación JDBC en `repository.impl`.
  - `service`: validaciones y reglas de negocio.
  - `controller`: recibe la petición de la vista y llama al servicio.
  - `view`: el submenú de consola del módulo, conectado al menú principal.
- Toda tarea agrega a `database/data.sql` los datos de prueba que necesita para poder probarse.
- Estados: ✅ hecho · ⏳ pendiente.

## Resumen

| Sprint | Enfoque | Módulos | Depende de |
|---|---|---|---|
| S0 | Preparación: análisis, modelo, UML, arquitectura, conexión | Transversal | — |
| S1 | Piezas comunes y entidades sin dependencias | Transversal, Clientes, Proveedores | S0 |
| S2 | Entidades que dependen de una entidad base | Clientes, Técnicos, Inventario, Servicios, Proveedores | S1 |
| S3 | Catálogo de proveedores, lotes y stock | Proveedores, Inventario | S2 |
| S4 | Órdenes: creación, líneas, técnicos y reservas | Servicios, Técnicos | S1–S3 |
| S5 | Ciclo de vida de la orden: completar, cancelar y alertas | Servicios, Inventario | S4 |
| S6 | Facturación y entrega | Facturación | S5 |
| S7 | Reportes e historial | Reportes, Clientes | S5, S6 |
| S8 | Cierre: documentación, pruebas y entrega | Documentación, Transversal | Todos |

## Escala de dificultad

| Nivel | Significado |
|:-:|---|
| 1 | Trivial: poca lógica, sigue un patrón que ya existe. |
| 2 | Sencilla: un CRUD o consulta simple con validaciones básicas. |
| 3 | Media: varias reglas de negocio, Streams no triviales o define un patrón que otros copiarán. |
| 4 | Alta: transacciones con varias tablas, cálculos delicados o un patrón de diseño completo. |
| 5 | Crítica: regla central del enunciado; un error afecta a todo el sistema. |

Criterio de reparto: las tareas de menor dificultad para Sofía, las intermedias para Julio y Andrés, y las de mayor dificultad para Jhorman. Todos tienen al menos una tarea por sprint, y las tareas que forman una cadena dentro de un sprint quedan con la misma persona cuando es posible.

## Carga por integrante (puntos de dificultad, S1 a S8)

| Integrante | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | Total | Tareas |
|---|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
| Jhorman | 3 | 3 | 7 | 8 | 5 | 4 | 8 | 3 | **41** | 11 |
| Julio | 4 | 4 | 4 | 9 | 2 | 2 | 3 | 4 | **32** | 14 |
| Andrés | 3 | 4 | 5 | 9 | 3 | 3 | 3 | 3 | **33** | 12 |
| Sofía | 2 | 3 | 4 | 4 | 2 | 1 | 2 | 3 | **21** | 13 |

---

## Sprint 0 — Preparación

Todo lo que se hizo antes de empezar a programar funcionalidades.

| ID | Tarea | Módulo | Estado | Responsable |
|---|---|---|---|---|
| T0.1 | Análisis del enunciado y de la rúbrica | Transversal | ✅ | Todos |
| T0.2 | Documento de requerimientos (RF, RN, RNF, HU) | Transversal | ✅ | Jhorman, Julio |
| T0.3 | Modelo lógico relacional | Base de datos | ✅ | Andrés |
| T0.4 | Diagrama de clases UML | Transversal | ✅ | Julio |
| T0.5 | Definición de arquitectura, paquetes y patrones | Transversal | ✅ | Jhorman |
| T0.6 | Repositorio y flujo de trabajo en GitHub | Transversal | ✅ | Julio, Jhorman |
| T0.7 | Proyecto Maven y esqueleto de paquetes | Setup | ✅ | Jhorman |
| T0.8 | Conexión a la base de datos | Setup | ✅ | Sofía |
| T0.9 | Scripts SQL iniciales | Base de datos | ✅ | Andrés |
| T0.10 | Actualizar `schema.sql` al modelo v1.3 | Base de datos | ✅ | Andrés |

**T0.1 — Análisis del enunciado y de la rúbrica.** Lectura del caso CarMotors y del documento de "Resultado esperado" para identificar módulos, reglas críticas y requisitos de entrega.

**T0.2 — Documento de requerimientos.** Documento maestro v1.3 con 35 RF por módulo, 22 reglas de negocio, RNF, historias de usuario y criterios de aceptación.

**T0.3 — Modelo lógico relacional.** Modelo de 20 tablas: cabecera-detalle de órdenes, relación M:N proveedor-repuesto, lotes, técnicos con especialidad y factura con *snapshots* (copias congeladas de los datos al facturar).

**T0.4 — Diagrama de clases UML.** Clases del dominio derivadas del modelo lógico, con sus atributos y asociaciones.

**T0.5 — Arquitectura, paquetes y patrones.** MVC + capa de servicio + DAO (`repository`), paquetes en inglés bajo `com.carmotors.sgt`, los 7 patrones (Singleton, Builder, Facade, DAO, State, Chain of Responsibility, Observer) y la aplicación de SOLID.

**T0.6 — Repositorio y flujo de trabajo.** Repositorio en GitHub con `main` y `develop`, plantillas de issues y pull requests, CODEOWNERS, vault de documentación y agentes por módulo.

**T0.7 — Proyecto Maven.** `pom.xml` (Java 17, driver MySQL, plugin de ejecución), `.gitignore` y paquetes vacíos de la arquitectura.

**T0.8 — Conexión a la base de datos.** `ConnectionFactory` como Singleton: lee `config.properties` una vez y entrega una conexión nueva en cada llamada. Incluye `config.properties.example` y prueba de conexión.

**T0.9 — Scripts SQL iniciales.** `schema.sql` y `data.sql` con los valores de las tablas de consulta.

**T0.10 — Actualizar `schema.sql` al modelo v1.3.** Completada en PR #3 / Issue #13: el script crea las 20 tablas del modelo v1.3 incluyendo `factura_snapshot`, `factura_detalles`, campos nuevos (`fecha_completado`, `fecha_cancelacion`, campos *snapshot*, `cantidad_actual` en lotes) y el estado *Cancelada* en `servicio_estados`.

---

## Sprint 1 — Piezas comunes y entidades sin dependencias

Ninguna de estas tareas depende de otra tabla del sistema. T1.1, T1.2 y T1.3 son piezas que todos los demás van a usar, así que conviene terminarlas primero.

| ID | Tarea | Módulo | Depende de | Rama | Dificultad | Responsable |
|---|---|---|---|---|:-:|---|
| T1.1 | Utilidades de lectura y validación por consola | Transversal | S0 | `feature/project-setup` | 2 | Julio |
| T1.2 | Excepciones propias y manejo de errores | Transversal | S0 | `feature/project-setup` | 1 | Sofía |
| T1.3 | Esqueleto del menú principal | Transversal | T1.1 | `feature/project-setup` | 2 | Julio |
| T1.4 | Tablas de consulta (catálogos fijos) | Transversal | S0 | `feature/project-setup` | 1 | Sofía |
| T1.5 | CRUD de clientes | Clientes y vehículos | T1.1–T1.3 | `feature/gestion-clientes` | 3 | Jhorman |
| T1.6 | CRUD de proveedores | Proveedores | T1.1–T1.3 | `feature/gestion-proveedores` | 3 | Andrés |

**T1.1 — Utilidades de lectura y validación por consola.** Una clase en `util` que todos los menús usan para pedir datos.
- Métodos para leer enteros, decimales (`BigDecimal`), fechas `yyyy-MM-dd`, textos obligatorios, emails y confirmaciones sí/no.
- Si el dato es inválido, muestra el error y vuelve a pedirlo; nunca lanza una excepción que cierre el programa.
- **Terminada cuando:** cualquier menú puede pedir un dato con una sola llamada (RNF-13).

**T1.2 — Excepciones propias y manejo de errores.** Base para que los errores de negocio lleguen al usuario como mensajes claros.
- Excepción base del sistema y las primeras específicas (por ejemplo, entidad no encontrada o dato duplicado).
- Convención: el servicio lanza la excepción y el controlador la captura y muestra el mensaje.
- **Terminada cuando:** hay un ejemplo documentado de cómo lanzar y capturar una excepción (RNF-12).

**T1.3 — Esqueleto del menú principal.** Solo la estructura, no las opciones de los módulos.
- Ciclo "mostrar opciones → leer opción → ejecutar → volver" con la opción *Salir*.
- Define la forma en que cada módulo se conecta: cada CRUD agrega su propio submenú y una línea en el menú principal.
- `Main` arranca este menú.
- **Terminada cuando:** el programa muestra el menú, permite salir y está documentado cómo agregar un módulo nuevo.

**T1.4 — Tablas de consulta.** Entidades y DAOs de solo lectura para `repuesto_tipos`, `especialidades`, `mantenimiento_tipos` y `servicio_estados`.
- Solo se consultan (listar y buscar por id); sus valores vienen de `data.sql`.
- **Terminada cuando:** otros módulos pueden obtener, por ejemplo, la lista de especialidades para mostrarla en un menú.

**T1.5 — CRUD de clientes** (RF-17, RN-13, RN-17). CRUD completo de clientes.
- Registrar, consultar, actualizar y desactivar (nunca borrar).
- Validar nombre, documento y email obligatorios; documento y email únicos; formato de email.
- **Terminada cuando:** se pueden gestionar clientes desde la opción "Clientes" del menú.

**T1.6 — CRUD de proveedores** (RF-28, RN-17). CRUD completo de proveedores.
- Registrar, consultar, actualizar y desactivar, con teléfono, email y dirección.
- Validar nombre obligatorio y formato de email si se ingresa.
- **Terminada cuando:** se pueden gestionar proveedores desde la opción "Proveedores" del menú.

---

## Sprint 2 — Entidades que dependen de una entidad base

| ID | Tarea | Módulo | Depende de | Rama | Dificultad | Responsable |
|---|---|---|---|---|:-:|---|
| T2.1 | Registro y consulta de vehículos | Clientes y vehículos | T1.5 | `feature/gestion-clientes` | 2 | Sofía |
| T2.2 | Búsqueda por documento y por placa | Clientes y vehículos | T1.5, T2.1 | `feature/gestion-clientes` | 1 | Sofía |
| T2.3 | CRUD de técnicos | Técnicos | T1.4 | `feature/gestion-tecnicos` | 2 | Julio |
| T2.4 | CRUD de repuestos | Inventario | T1.4 | `feature/gestion-inventario` | 2 | Andrés |
| T2.5 | Catálogo de tipos de servicio | Servicios y órdenes | T1.4 | `feature/gestion-servicios` | 2 | Julio |
| T2.6 | Evaluaciones de proveedores | Proveedores | T1.6 | `feature/gestion-proveedores` | 2 | Andrés |
| T2.7 | Ranking de proveedores | Proveedores | T2.6 | `feature/gestion-proveedores` | 3 | Jhorman |

**T2.1 — Registro y consulta de vehículos** (RF-18, RN-13). Vehículos asociados a un cliente.
- La placa se guarda en mayúsculas y sin espacios, y debe ser única.
- Solo se registra si el cliente existe y está activo.
- **Terminada cuando:** desde el submenú de clientes se puede agregar un vehículo y listar los vehículos de un cliente.

**T2.2 — Búsqueda por documento y por placa** (RF-19).
- Buscar cliente por documento y vehículo por placa, con coincidencia exacta tras normalizar la entrada.
- **Terminada cuando:** ambas búsquedas funcionan y muestran un mensaje si no hay resultados.

**T2.3 — CRUD de técnicos** (RF-33, RN-17). CRUD completo de técnicos con su especialidad.
- Documento único; especialidad elegida de la lista de especialidades.
- Desactivar en vez de borrar; un técnico inactivo no se podrá asignar más adelante.
- **Terminada cuando:** se pueden gestionar técnicos desde la opción "Técnicos" del menú.

**T2.4 — CRUD de repuestos** (RF-01, RN-17). CRUD completo de repuestos.
- Nombre, tipo, marca, precio de venta y stock mínimo; sin valores negativos.
- El stock **no** se edita a mano: solo cambia con lotes (S3) y consumos (S5).
- **Terminada cuando:** se pueden gestionar repuestos desde la opción "Inventario" del menú.

**T2.5 — Catálogo de tipos de servicio.** CRUD completo de `servicio_tipos`. No es un RF propio, pero las órdenes lo necesitan.
- Nombre único, tipo de mantenimiento (preventivo o correctivo), especialidad requerida, precio base de mano de obra e intervalo en meses (solo para preventivos).
- **Terminada cuando:** existen tipos como "Cambio de aceite" o "Frenos" que se pueden elegir al crear una orden.

**T2.6 — Evaluaciones de proveedores** (RF-31, RN-16).
- Calificar calidad y puntualidad con enteros de 1 a 5, fecha actual por defecto y comentario opcional.
- **Terminada cuando:** se puede registrar y listar las evaluaciones de un proveedor.

**T2.7 — Ranking de proveedores** (RF-32, RN-16).
- Promedio con un decimal y número de evaluaciones, ordenado de mayor a menor con Streams.
- Proveedores sin evaluar al final como "Sin evaluar"; en empate, orden por nombre.
- **Terminada cuando:** el ranking se muestra desde el submenú de proveedores.

---

## Sprint 3 — Catálogo de proveedores, lotes y stock

Orden interno obligatorio: **T3.1 antes que T3.3**, porque un lote exige que el proveedor ofrezca ese repuesto.

| ID | Tarea | Módulo | Depende de | Rama | Dificultad | Responsable |
|---|---|---|---|---|:-:|---|
| T3.1 | Catálogo proveedor-repuesto | Proveedores | T1.6, T2.4 | `feature/gestion-proveedores` | 3 | Jhorman |
| T3.2 | Comparar proveedores de un repuesto | Proveedores | T3.1, T2.7 | `feature/gestion-proveedores` | 2 | Sofía |
| T3.3 | Ingreso de lotes | Inventario | T3.1 | `feature/gestion-inventario` | 3 | Andrés |
| T3.4 | Trazabilidad de lotes | Inventario | T3.3 | `feature/gestion-inventario` | 2 | Andrés |
| T3.5 | Cálculo de stock | Inventario | T3.3 | `feature/gestion-inventario` | 4 | Jhorman |
| T3.6 | Búsqueda y filtros de repuestos | Inventario | T3.1 | `feature/gestion-inventario` | 2 | Julio |
| T3.7 | Reporte de stock bajo | Inventario | T3.1, T3.5 | `feature/gestion-inventario` | 2 | Julio |
| T3.8 | Reporte de lotes por vencer | Inventario | T3.3 | `feature/gestion-inventario` | 2 | Sofía |

**T3.1 — Catálogo proveedor-repuesto** (RF-29, RN-18). Gestión de la relación M:N: qué repuestos ofrece cada proveedor.
- Costo mayor que cero, tiempo de entrega no negativo, sin repetir el par proveedor-repuesto.
- Marcar un proveedor habitual desmarca al anterior, en una sola transacción.
- **Terminada cuando:** se puede asociar un repuesto a varios proveedores y marcar uno como habitual.

**T3.2 — Comparar proveedores de un repuesto** (RF-30).
- Lista los proveedores activos de un repuesto ordenados por costo, con tiempo de entrega y promedio de evaluación.
- **Terminada cuando:** la comparación se muestra desde el submenú de proveedores.

**T3.3 — Ingreso de lotes** (RF-02, RN-14). Es la única forma de que entre stock al taller.
- Código de lote único, cantidad mayor que cero, caducidad opcional posterior al ingreso.
- El proveedor debe estar activo y ofrecer ese repuesto en el catálogo.
- Se guarda en una transacción.
- **Terminada cuando:** al registrar un lote, el stock del repuesto aumenta exactamente en esa cantidad.

**T3.4 — Trazabilidad de lotes** (RF-03).
- Ver los lotes de un repuesto (primero los que caducan antes) y consultar un lote por código: proveedor, fechas, cantidad inicial y restante.
- **Terminada cuando:** ambas consultas funcionan desde el submenú de inventario.

**T3.5 — Cálculo de stock** (RF-07, sección 2.5 del maestro). Lógica que usarán las reservas, las alertas y el completado de órdenes.
- Calcular stock físico (suma de lotes), stock operativo (sin vencidos), reservado y disponible.
- **Terminada cuando:** existe un método del servicio que devuelve los cuatro valores de un repuesto y se muestran en consola.

**T3.6 — Búsqueda y filtros de repuestos** (RF-04).
- Filtrar por nombre, tipo, marca o proveedor, combinables, sin distinguir mayúsculas, usando Streams.
- **Terminada cuando:** los filtros funcionan desde el submenú de inventario.

**T3.7 — Reporte de stock bajo** (RF-05).
- Repuestos activos cuyo disponible es menor o igual a su stock mínimo, con su proveedor habitual o "No definido".
- **Terminada cuando:** el reporte se muestra y avisa si no hay repuestos bajo mínimo.

**T3.8 — Reporte de lotes por vencer** (RF-06).
- Lotes con unidades que vencen en los próximos N días (30 por defecto) o ya vencidos, distinguiendo ambos casos.
- **Terminada cuando:** el reporte se muestra desde el submenú de inventario.

---

## Sprint 4 — Órdenes: creación, líneas, técnicos y reservas

El sprint más importante. Hay una cadena secuencial: **T4.4 → T4.5 → T4.6**.

| ID | Tarea | Módulo | Depende de | Rama | Dificultad | Responsable |
|---|---|---|---|---|:-:|---|
| T4.1 | Base del patrón State | Servicios y órdenes | T1.4 | `feature/gestion-servicios` | 3 | Andrés |
| T4.2 | Crear orden de servicio | Servicios y órdenes | T2.1, T2.5, T4.1 | `feature/gestion-servicios` | 4 | Jhorman |
| T4.3 | Agregar o quitar líneas | Servicios y órdenes | T4.2 | `feature/gestion-servicios` | 3 | Andrés |
| T4.4 | Carga de trabajo de un técnico | Técnicos | T2.3, T4.2 | `feature/gestion-tecnicos` | 2 | Julio |
| T4.5 | Cadena de sugerencia de técnico | Servicios y órdenes | T4.4 | `feature/gestion-servicios` | 4 | Julio |
| T4.6 | Asignar técnicos a líneas | Servicios y órdenes | T4.5 | `feature/gestion-servicios` | 3 | Julio |
| T4.7 | Reservar repuestos en líneas | Servicios y órdenes | T3.5, T4.2 | `feature/gestion-servicios` | 4 | Jhorman |
| T4.8 | Retirar repuestos de una línea | Servicios y órdenes | T4.7 | `feature/gestion-servicios` | 2 | Sofía |
| T4.9 | Pasar orden a En Proceso | Servicios y órdenes | T4.1, T4.6 | `feature/gestion-servicios` | 2 | Sofía |
| T4.10 | Listar órdenes y ver detalle | Servicios y órdenes | T4.6, T4.7 | `feature/gestion-servicios` | 3 | Andrés |

**T4.1 — Base del patrón State** (RN-06). Estructura que controla qué se puede hacer en cada estado de la orden.
- Interfaz `EstadoOrden` y una clase por estado: Pendiente, En Proceso, Completado, Facturado, Entregado, Cancelada.
- Cada estado sabe a cuál puede pasar; cualquier salto o retroceso lanza `TransicionEstadoInvalidaException`.
- En este sprint solo se deja la estructura; los efectos de cada transición se agregan en las tareas que los usan.
- **Terminada cuando:** intentar una transición inválida lanza la excepción.

**T4.2 — Crear orden de servicio** (RF-08, RN-18). Registro de un vehículo que entra al taller.
- Cabecera (vehículo, descripción del problema, fecha de recepción actual) y al menos una línea (tipo de servicio, mano de obra inicial = precio base del tipo).
- Cabecera y líneas en una sola transacción; la orden queda Pendiente.
- **Terminada cuando:** se puede crear una orden con varias líneas desde la opción "Órdenes" del menú.

**T4.3 — Agregar o quitar líneas** (RF-35, RN-05, RN-18).
- Agregar o quitar líneas en órdenes Pendientes o En Proceso, nunca la última línea.
- En En Proceso, una línea nueva debe quedar con técnico asignado y no se puede quitar una línea con horas registradas.
- Quitar una línea libera sus reservas.
- **Terminada cuando:** se cumplen esas restricciones desde el submenú de órdenes.

**T4.4 — Carga de trabajo de un técnico** (RF-34, RN-21).
- Carga = número de líneas asignadas al técnico en órdenes Pendientes o En Proceso.
- Ver las líneas asignadas a un técnico con orden, placa, tipo de servicio y estado.
- **Terminada cuando:** el cálculo de carga está disponible en el servicio de técnicos y la consulta se ve en su submenú.

**T4.5 — Cadena de sugerencia de técnico** (RN-20, patrón Chain of Responsibility).
- Tres manejadores en orden: especialista sin carga → especialista con menor carga → cualquier técnico con menor carga.
- La cadena se detiene en el primero que encuentra un candidato; si ninguno encuentra, no hay sugerencia.
- **Terminada cuando:** dada una línea, la cadena devuelve el técnico correcto en cada uno de los tres casos.

**T4.6 — Asignar técnicos a líneas** (RF-09, RN-07, RN-19).
- El sistema muestra la sugerencia (nombre, especialidad, carga); Enter la acepta o se escribe otro ID.
- Si la especialidad no coincide, pide confirmación. Rechaza técnicos inactivos o repetidos en la misma línea.
- Registra horas trabajadas (no negativas).
- **Terminada cuando:** se pueden asignar uno o varios técnicos a cada línea.

**T4.7 — Reservar repuestos en líneas** (RF-10, RN-01, RN-02, RN-11).
- Solo si el disponible alcanza; aumenta el reservado sin tocar el stock físico.
- Copia el nombre y el precio de venta vigentes en la línea.
- **Terminada cuando:** al reservar, el disponible del repuesto baja y el stock físico no cambia.

**T4.8 — Retirar repuestos de una línea** (RF-11, RN-05).
- Solo en órdenes Pendientes o En Proceso; libera exactamente la cantidad reservada.
- **Terminada cuando:** al retirar, el reservado vuelve a su valor anterior.

**T4.9 — Pasar orden a En Proceso** (RF-12, RN-07).
- Primera transición real del patrón State: solo si cada línea tiene al menos un técnico activo.
- **Terminada cuando:** la transición funciona y se bloquea si falta un técnico, indicando qué línea.

**T4.10 — Listar órdenes y ver detalle** (RF-15, RF-16).
- Listar con filtros combinables (estado, técnico, vehículo, tipo de servicio, fechas) usando Streams.
- Detalle: cliente, vehículo, estado y, por línea, técnicos, horas, repuestos, mano de obra y subtotal.
- **Terminada cuando:** ambas consultas funcionan desde el submenú de órdenes.

---

## Sprint 5 — Ciclo de vida: completar, cancelar y alertas

| ID | Tarea | Módulo | Depende de | Rama | Dificultad | Responsable |
|---|---|---|---|---|:-:|---|
| T5.1 | Alerta de stock bajo | Inventario | T3.5 | `feature/gestion-inventario` | 2 | Sofía |
| T5.2 | Completar orden y descontar stock | Servicios y órdenes | T4.9, T3.3, T5.1 | `feature/gestion-servicios` | 5 | Jhorman |
| T5.3 | Cancelar orden | Servicios y órdenes | T4.7, T4.9 | `feature/gestion-servicios` | 2 | Julio |
| T5.4 | Fachada del taller | Servicios y órdenes | T4.2, T5.2 | `feature/gestion-servicios` | 3 | Andrés |

**T5.1 — Alerta de stock bajo** (RN-22, patrón Observer).
- Interfaz `StockObserver` y un observador que imprime la alerta en consola: repuesto, disponible y proveedor habitual.
- El servicio de inventario avisa a sus observadores cada vez que descuenta stock.
- **Terminada cuando:** al bajar un repuesto a su mínimo o menos, aparece la alerta.

**T5.2 — Completar orden y descontar stock** (RF-13, RN-03, RN-04, RN-08). La regla crítica del enunciado.
- Valida que cada línea tenga mano de obra y que el stock operativo cubra las reservas.
- Descuenta de los lotes por FEFO (*First Expired, First Out*: primero con caducidad antes que sin caducidad; más próxima primero; empate por fecha de ingreso más antigua; desempate final por menor ID), sin usar lotes vencidos.
- Reduce las reservas, registra la fecha de completado y cambia el estado.
- Todo en una sola transacción: si algo falla, rollback y no cambia nada.
- **Terminada cuando:** al completar, el stock y las reservas bajan correctamente y un error forzado no deja cambios a medias.

**T5.3 — Cancelar orden** (RF-14, RN-05).
- Solo desde Pendiente o En Proceso; libera todas las reservas y registra la fecha de cancelación.
- La orden queda como historial y ya no admite cambios.
- **Terminada cuando:** al cancelar, todas las reservas de la orden se liberan.

**T5.4 — Fachada del taller** (patrón Facade).
- `TallerFacade` ofrece a la vista operaciones de alto nivel, como "crear orden completa" o "completar orden", que por dentro coordinan varios servicios.
- **Terminada cuando:** el submenú de órdenes usa la fachada para esas operaciones y la vista no conoce los servicios internos.

---

## Sprint 6 — Facturación y entrega

| ID | Tarea | Módulo | Depende de | Rama | Dificultad | Responsable |
|---|---|---|---|---|:-:|---|
| T6.1 | Generar factura | Facturación | T5.2 | `feature/facturacion` | 4 | Jhorman |
| T6.2 | Archivo TXT de la factura | Facturación | T6.1 | `feature/facturacion` | 3 | Andrés |
| T6.3 | Consultar y reimprimir factura | Facturación | T6.2 | `feature/facturacion` | 2 | Julio |
| T6.4 | Entregar orden | Facturación | T6.1 | `feature/facturacion` | 1 | Sofía |

**T6.1 — Generar factura** (RF-22, RF-23, RN-09, RN-12).
- Solo para órdenes Completadas sin factura previa.
- En una transacción guarda la cabecera (`facturas`), la copia de datos del cliente y vehículo (`factura_snapshot`) y una fila por cada concepto cobrado (`factura_detalles`: mano de obra por línea y cada repuesto).
- Calcula totales, IVA (tasa fija del 19% = `0.19` registrada en `facturas.tasa_iva`) y total final con `BigDecimal`; la orden pasa a Facturado.
- **Terminada cuando:** la factura queda guardada con totales correctos y la orden en Facturado.

**T6.2 — Archivo TXT de la factura** (RF-22, patrón Builder).
- `FacturaTXTBuilder` arma el texto paso a paso: encabezado, cliente, líneas, repuestos, totales.
- Se escribe `factura_<numero>.txt` en la carpeta configurada **después** del commit y se marca `archivo_generado`.
- Si falla el archivo, la factura sigue registrada y se puede reintentar.
- **Terminada cuando:** el TXT se genera con el desglose completo.

**T6.3 — Consultar y reimprimir factura** (RF-24).
- Reimprime usando solo los datos guardados en las tablas de factura, aunque el catálogo o el cliente hayan cambiado.
- No crea otra factura ni cambia el estado.
- **Terminada cuando:** la reimpresión es idéntica a la original.

**T6.4 — Entregar orden** (RF-12, RN-10).
- Transición Facturado → Entregado; registra la fecha de entrega.
- **Terminada cuando:** solo se puede entregar una orden facturada.

---

## Sprint 7 — Reportes e historial

Todos los reportes usan Lambdas y Stream API (RNF-11) y respetan los criterios de fecha de la sección 4.2 del maestro.

| ID | Tarea | Módulo | Depende de | Rama | Dificultad | Responsable |
|---|---|---|---|---|:-:|---|
| T7.1 | Servicios más solicitados | Reportes | T5.2 | `feature/reportes` | 3 | Andrés |
| T7.2 | Productividad por técnico | Reportes | T5.2 | `feature/reportes` | 4 | Jhorman |
| T7.3 | Consumo de repuestos | Reportes | T5.2 | `feature/reportes` | 3 | Julio |
| T7.4 | Historial del cliente | Clientes y vehículos | T6.1 | `feature/gestion-clientes` | 2 | Sofía |
| T7.5 | Recordatorios de mantenimiento | Reportes | T6.4 | `feature/reportes` | 4 | Jhorman |

**T7.1 — Servicios más solicitados** (RF-25).
- Por tipo de servicio: cantidad de líneas e ingresos (repuestos + mano de obra, antes de impuestos), en un rango de fechas de completado.
- **Terminada cuando:** el reporte se muestra ordenado por cantidad descendente.

**T7.2 — Productividad por técnico** (RF-26).
- Líneas completadas, horas y mano de obra atribuida, repartiendo la mano de obra de cada línea según las horas de cada técnico.
- Incluye técnicos activos sin trabajo con cero.
- **Terminada cuando:** el reporte se muestra para un rango de fechas.

**T7.3 — Consumo de repuestos** (RF-27).
- Unidades y valor comercial por repuesto y por tipo de servicio, solo de órdenes completadas o posteriores (no reservas).
- **Terminada cuando:** el reporte se muestra ordenado por valor descendente.

**T7.4 — Historial del cliente** (RF-20).
- Vehículos del cliente y todas sus órdenes, de la más reciente a la más antigua, con estado, líneas y total facturado si existe factura.
- **Terminada cuando:** el historial se ve desde el submenú de clientes.

**T7.5 — Recordatorios de mantenimiento** (RF-21, RN-15).
- Por vehículo y servicio preventivo: fecha sugerida = fecha de entrega + intervalo en meses. Incluye vencidos y los próximos 30 días.
- Un servicio entregado más reciente del mismo tipo reemplaza al anterior.
- **Terminada cuando:** el reporte muestra contacto del cliente, placa, servicio y fecha sugerida.

---

## Sprint 8 — Cierre y entrega

| ID | Tarea | Módulo | Depende de | Rama | Dificultad | Responsable |
|---|---|---|---|---|:-:|---|
| T8.1 | Pulido del menú principal | Transversal | Todas | `feature/documentacion` | 2 | Julio |
| T8.2 | `data.sql` definitivo | Base de datos | Todas | `feature/documentacion` | 3 | Andrés |
| T8.3 | Diagramas exportados | Documentación | T0.3, T0.4 | `feature/documentacion` | 1 | Sofía |
| T8.4 | README completo | Documentación | Todas | `feature/documentacion` | 3 | Jhorman |
| T8.5 | Prueba en máquina limpia | Transversal | T8.2, T8.4 | `feature/documentacion` | 2 | Sofía |
| T8.6 | Revisión final y versión estable | Transversal | T8.5 | — | 2 | Julio |

**T8.1 — Pulido del menú principal.** Ordenar las opciones, unificar textos y mensajes, y recorrer toda la navegación buscando caminos rotos.

**T8.2 — `data.sql` definitivo.** Juntar y limpiar los datos de prueba de todos los sprints para que cada flujo se pueda demostrar: órdenes en todos los estados, lotes por vencer, repuestos bajo mínimo, clientes con recordatorio pendiente, etc.

**T8.3 — Diagramas exportados.** `diagrama-er.png`, `diagrama-clases.png` y `arquitectura.png` en `docs/`, generados desde las fuentes del vault.

**T8.4 — README completo** (RNF-20). Todas las secciones obligatorias: título, contexto, tecnologías, funcionalidades y reglas clave, modelo de BD con diagrama, instalación y ejecución paso a paso, decisiones de diseño (SOLID y los 7 patrones) y autores.

**T8.5 — Prueba en máquina limpia** (RNF-22). Alguien que no haya configurado el proyecto lo clona, sigue solo el README y lo ejecuta con `mvn clean compile exec:java`. Cualquier paso que falle se corrige en el README.

**T8.6 — Revisión final y versión estable.** Repasar la Definition of Done y el checklist de entrega, fusionar `develop` en `main` y registrar el hash del último commit para la entrega.
