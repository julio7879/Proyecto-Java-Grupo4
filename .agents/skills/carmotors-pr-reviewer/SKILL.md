---
name: carmotors-pr-reviewer
description: Revisa y audita Pull Requests para el proyecto CarMotors contra la arquitectura MVC + Service + Repository (DAO), reglas de negocio críticas v1.3 (FEFO de 4 criterios, transacciones JDBC atómicas, snapshots inmutables, BigDecimal con HALF_UP e IVA fijo 0.19), actualización de CHANGELOG.md, Git Flow y compilación Maven antes de aprobar el PR.
---

# CarMotors Pull Request Reviewer Skill

Esta skill actúa como revisor técnico automatizado y auditor de calidad para el proyecto **CarMotors (Sistema de Gestión de Taller)**. Permite tanto al Líder Técnico (`@julio7879` / `CODEOWNERS`) auditar PRs entrantes hacia `develop`, como a cualquier integrante autoevaluar su rama antes de abrir el Pull Request.

---

## Modelo recomendado para esta Skill
- **Auditoría formal de PRs:** Ejecutar preferiblemente con **`Gemini 4 Argon (High)`** o **`Claude Sonnet/Opus 5.5 (High)`** para detectar fugas sutiles de transacciones, errores de redondeo o saltos de capa.
- **Autochequeo rápido pre-PR:** `Gemini 4 Hydrogen (High)`.

---

## 🎯 Cuándo Activar esta Skill

Activa esta skill cuando el usuario pida:
- Revisar, auditar o inspeccionar un Pull Request (ej: *"Revisa el PR #5"*, *"Audita el PR de Jhorman"*).
- Evaluar una rama local antes de abrir un PR hacia `develop`.
- Validar si una implementación cumple con la Definition of Done, el Documento Maestro v1.3 y las reglas de negocio.
- Redactar el feedback o aprobación formal de un PR en GitHub y actualizar `CHANGELOG.md`.

---

## 🛠️ Procedimiento de Auditoría Paso a Paso

### Paso 1: Inspeccionar el PR y los Cambios
1. Obtener metadatos del PR:
   ```bash
   gh pr view <numero_pr>
   ```
2. Obtener el diff completo del código:
   ```bash
   gh pr diff <numero_pr>
   ```
3. Si el PR aún no está abierto y se evalúa una rama local:
   ```bash
   git diff develop...<nombre_rama>
   ```

---

### Paso 2: Validación de Arquitectura y Separación de Capas (v1.3)
Verificar que los archivos modificados respeten estrictamente los paquetes de `com.carmotors.sgt`:

| Capa | Responsabilidad Permitida | Prohibiciones Estrictas |
| :--- | :--- | :--- |
| **Vista** (`com.carmotors.sgt.view`) | Interacción por consola usando utilidades de `com.carmotors.sgt.util`. Muestra menús, tablas y mensajes claros al usuario. | **NUNCA** llama a `repository`. **NUNCA** aplica lógica de negocio ni cálculos financieros. Solo interactúa con `controller`. |
| **Controlador** (`com.carmotors.sgt.controller`) | Recibe entradas de `view`, invoca a `service` (o `TallerFacade`), captura excepciones propias y devuelve el resultado a `view`. | **NUNCA** ejecuta SQL ni abre conexiones JDBC. **NUNCA** llama directamente a `repository`. |
| **Servicio** (`com.carmotors.sgt.service` + `builder`, `chain`, `facade`, `observer`, `state`) | Orquesta la lógica de negocio (RN-01 a RN-22), valida reglas y demarca transacciones JDBC (`commit`/`rollback`). | **NUNCA** lee datos de consola (`System.in`, `Scanner`) ni imprime mensajes directos a consola (salvo `StockObserver` en consola si aplica). |
| **Repositorio / DAO** (`com.carmotors.sgt.repository` y `repository.impl`) | Define interfaces DAO e implementaciones JDBC con `PreparedStatement`. Mapea `ResultSet` a entidades de `model` y viceversa. | **NUNCA** usar paquete `dao` (el paquete oficial es `repository`). **NUNCA** decide reglas de negocio ni abre su propia conexión si participa en una transacción multi-tabla. |

---

### Paso 3: Lista de Comprobación de Reglas de Negocio Críticas (Maestro v1.3)

#### 1. Transacciones JDBC Atómicas (Operaciones Multi-tabla — RN-04, RNF-08)
- Si una operación modifica 2 o más tablas (ingresar lote, marcar proveedor habitual, crear orden, completar orden FEFO, facturar):
  - **DEBE** usar una **única** instancia de `Connection` (`ConnectionFactory.getInstance().getConnection()`).
  - **DEBE** llamar a `connection.setAutoCommit(false)` y pasar la misma `Connection` a todos los repositorios participantes.
  - **DEBE** ejecutar `connection.commit()` al finalizar, `connection.rollback()` en `catch (SQLException e)` y restaurar/cerrar en `finally` o `try-with-resources`.
  - **PROHIBIDO:** Que cada repositorio abra su propia conexión independiente dentro del mismo flujo transaccional.

#### 2. Algoritmo FEFO Determinista en Repuestos (RN-03, §2.6 v1.3)
- Al completar una orden y consumir repuestos:
  - **DEBE** excluir lotes vencidos (`l.fecha_caducidad IS NULL OR l.fecha_caducidad >= CURDATE()`).
  - **DEBE** ordenar con los **4 criterios deterministas** en orden estricto:
    ```sql
    ORDER BY
      CASE WHEN l.fecha_caducidad IS NULL THEN 1 ELSE 0 END ASC,
      l.fecha_caducidad ASC,
      l.fecha_ingreso ASC,
      l.id ASC
    ```
  - **DEBE** reducir `lotes.cantidad_actual` y `repuestos.cantidad_reservada` en la misma transacción.
  - **PROHIBIDO:** Crear o leer una columna redundante `repuestos.cantidad_stock`; los lotes son la única fuente de verdad física.

#### 3. Inmutabilidad y Snapshots (Facturación y Órdenes — RN-09, RN-11, §2.7 v1.3)
- En `orden_servicio_detalle` y `detalle_repuestos`:
  - **DEBEN** persistir `nombre_servicio_snapshot`, `nombre_repuesto_snapshot` y `precio_unitario_venta`.
  - **PROHIBIDO:** Modificar o retirar líneas/repuestos cuando la orden está en `Completado`, `Facturado`, `Entregado` o `Cancelada`.
- En facturación:
  - **DEBEN** persistirse en una sola transacción: `facturas`, exactamente un registro en `factura_snapshot` (1:1 con datos de cliente y vehículo) y al menos un registro en `factura_detalles` (`MANO_OBRA` con `repuesto_id = NULL`, o `REPUESTO` con `(orden_detalle_id, repuesto_id)` válido), verificando `>= 1` detalle y `1` snapshot antes del `COMMIT`.
  - El TXT `output/factura_<numero>.txt` (UTF-8) se genera **después** del `COMMIT` con `FacturaTXTBuilder` (reintento idempotente si falla la escritura).

#### 4. Precisión Monetaria e IVA (`BigDecimal` — RN-12, RNF-15, §2.8 v1.3)
- Cualquier cálculo de precio, costo, subtotal, IVA o total:
  - **DEBE** usar `java.math.BigDecimal`, escala `2` y `RoundingMode.HALF_UP` (`.setScale(2, RoundingMode.HALF_UP)`).
  - **PROHIBIDO:** Uso de `float` o `double` para dinero.
  - **Tasa IVA:** Constante formal fija del 19% (`new BigDecimal("0.19")`), guardada en `facturas.tasa_iva` (no proviene de `config.properties`).

#### 5. Jerarquía de Excepciones y Manejo de Errores (RNF-12, RNF-23)
- Usar `ConfiguracionException` (setup) y la jerarquía de excepciones propias del proyecto (`StockInsuficienteException`, `TransicionEstadoInvalidaException`, etc. definidas en `T1.2`).
- **PROHIBIDO:** `System.exit()` en `service` o `repository`, y bloques `catch` vacíos o con solo `e.printStackTrace()` sin registrar ni propagar/notificar.
- Ante entradas inválidas, la vista debe mostrar un mensaje explicativo y volver al menú sin colapsar.

---

### Paso 4: Validación de Compilación, Documentación y Git Flow
1. Ejecutar compilación Maven:
   ```bash
   mvn clean compile
   ```
   *(Debe arrojar `BUILD SUCCESS` con 0 errores).*
2. Verificar que no existan credenciales ni `src/main/resources/config.properties` rastreados (solo `config.properties.example`).
3. Verificar *Conventional Commits* en inglés (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`).
4. Verificar que el PR apunte a `develop` (salvo `hotfix/*` o `release/*` autorizados) y tenga vinculado su issue (`Closes #X`).
5. **Protección de `README.md` y actualización de `CHANGELOG.md`:**
   - **PROHIBIDO** modificar `README.md` en PRs intermedios antes de la tarea `T8.4`.
   - Si el PR es aprobado/integrado, verificar o redactar la nueva entrada correspondiente en `CHANGELOG.md` con autor, rama, PR/issue y resumen de cambios.
   - Si se modificó `docs/vault/`, verificar `powershell -NoProfile -File ./scripts/check-traceability.ps1` y recordar correr `qmd update`.

---

### Paso 5: Formato del Reporte de Revisión

```markdown
# 🔍 Reporte de Revisión Técnica — PR #[Número]

**Título:** [Título del PR]
**Autor:** @[usuario]
**Rama:** `[rama]` ➔ `develop`
**Issue Relacionado:** Closes #[Issue]
**Veredicto:** ✅ APROBADO / ⚠️ CAMBIOS REQUERIDOS / ❌ BLOQUEADO

---

## 📋 Resumen de Cambios Evaluados
- [Resumen conciso de archivos y módulos afectados]

---

## 🛡️ Matriz de Cumplimiento Técnico

| Criterio | Estado | Observación |
| :--- | :---: | :--- |
| **Separación de Capas (`view/controller/service/repository`)** | ✅ / ⚠️ / ❌ | [Detalle] |
| **Transacciones Atómicas JDBC** | ✅ / N/A / ❌ | [Detalle] |
| **Reglas de Negocio (FEFO 4 criterios / Snapshots / IVA 0.19)** | ✅ / N/A / ❌ | [Detalle] |
| **Uso de `BigDecimal` (`HALF_UP`, 2 dec)** | ✅ / N/A / ❌ | [Detalle] |
| **Manejo de Excepciones Propias y Logs** | ✅ / ⚠️ / ❌ | [Detalle] |
| **Compilación Maven (`BUILD SUCCESS`)** | ✅ / ❌ | [0 errores] |
| **Git Flow, `CHANGELOG.md` & Protección de `README.md`** | ✅ / ⚠️ / ❌ | [Detalle] |

---

## 📌 Hallazgos y Correcciones Requeridas
### 🔴 Bloqueantes (Críticos)
- [Archivo, línea aproximada y solución esperada]

### 🟡 Mejoras Recomendadas (No bloqueantes)
- [Sugerencias de legibilidad o diseño]

---

## 💬 Comentario Listo para Publicar en GitHub
[Texto formal en Markdown listo para `gh pr review`]
```
