# Definition of Done (DoD) & Calidad

Para que una Historia de Usuario o Tarea se considere oficialmente **DONE** y sea aprobada por el líder en el Pull Request:

1. [ ] **Rama Git Flow:** Salió de `develop` y el Pull Request apunta a `develop`.
2. [ ] **Criterios de Aceptación:** Cumple el 100% de los criterios Dado / Cuando / Entonces de la historia.
3. [ ] **Separación de Capas:**
   - La vista (`view`) no contiene reglas de negocio ni cálculos.
   - El controlador (`controller`) no llama a DAOs directamente.
   - La persistencia reside únicamente en DAOs (`repository`).
   - Las validaciones y cálculos residen en servicios (`service`).
4. [ ] **Transacciones JDBC:** Si la operación modifica más de una tabla (ej: completar orden, facturar, ingresar lote), utiliza una única `Connection` compartida y `setAutoCommit(false)` con `commit` y `rollback`.
5. [ ] **Manejo Financiero:** Montos en `BigDecimal`, 2 decimales y `RoundingMode.HALF_UP`. Nunca `float` ni `double`.
6. [ ] **Excepciones Propias:** Lanza y captura excepciones de dominio (`StockInsuficienteException`, `TransicionEstadoInvalidaException`) mostrando mensajes claros al operador sin cerrar la aplicación de consola.
7. [ ] **Logs:** Los errores técnicos se registran en logs (`logs/sgt.log`) con contexto suficiente, sin exponer contraseñas.
8. [ ] **Compilación limpia:** Compila con `mvn clean compile` sin errores ni warnings críticos.
9. [ ] **Commits formales:** Redactados en inglés siguiendo Conventional Commits (`feat:`, `fix:`, etc.).
