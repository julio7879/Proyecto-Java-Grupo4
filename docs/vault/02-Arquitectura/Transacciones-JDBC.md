# Transacciones JDBC & Concurrencia

Regla de oro: una unidad de trabajo = una `Connection` = un `commit` o un `rollback`. Ver también [[Definition-of-Done]] y [[05-Servicios-Ordenes]].

> [!warning] Documento derivado (no editar a mano)
> Generado desde [[00-Documento-Maestro-v1.2]] por `scripts/sync-vault-from-master.ps1`.
> Cualquier corrección se hace en el maestro y se regenera: así no hay dos versiones de la verdad.

### 5.6 Precisiones de JDBC y transacciones

- `ConnectionFactory` puede ser Singleton como proveedor de configuración y conexiones, pero `getConnection()` debe obtener una conexión nueva para cada unidad de trabajo. No se debe compartir una misma `Connection` global entre llamadas.
- Una operación transaccional obtiene una `Connection`, ejecuta `setAutoCommit(false)`, entrega esa misma conexión a los DAOs participantes, realiza `commit` si todo resulta bien y `rollback` ante cualquier excepción. Finalmente cierra la conexión con `try-with-resources`.
- Si cada DAO abre su propia conexión durante una operación compuesta, el `commit` / `rollback` no abarcará todas las tablas. Por eso la misma conexión debe propagarse durante la transacción.
- La transacción de MySQL garantiza atomicidad de las operaciones en la base de datos, pero no incluye automáticamente el sistema de archivos. Por eso el TXT se genera después del `commit` y puede reintentarse desde los datos históricos guardados.
- Para `completarOrden`, `cancelarOrden`, `registrarLote`, `crearOrden` y `generarFactura`, la consistencia entre tablas es prioritaria; si una parte falla, no debe quedar un estado intermedio.
- La concurrencia de hilos no es un requerimiento de esta versión: existe un solo administrador y no se definieron procesos simultáneos. Las transacciones siguen siendo obligatorias aunque no se creen hilos Java.
