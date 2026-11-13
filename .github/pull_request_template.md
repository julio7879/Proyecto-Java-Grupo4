## Descripción

<!-- ¿Qué cambia y por qué? -->

## Issue relacionado

Closes #

## Requerimientos cubiertos

<!-- RF-xx, RN-xx, HU-xx -->

## Tipo de cambio

- [ ] feat — nueva funcionalidad
- [ ] fix — corrección
- [ ] docs — documentación
- [ ] refactor
- [ ] test
- [ ] chore

## Checklist (Definition of Done)

- [ ] La rama sale de `develop` y el PR apunta a `develop` (salvo `release/*` o `hotfix/*` hacia `main`).
- [ ] Cumple todos los criterios de aceptación del issue.
- [ ] La vista no aplica reglas, el controlador no accede a DAOs y el DAO no decide negocio.
- [ ] Operaciones multi-tabla usan una sola transacción y la misma `Connection`.
- [ ] Montos con `BigDecimal`, dos decimales y `HALF_UP`.
- [ ] Entradas inválidas muestran mensajes claros y no cierran la aplicación.
- [ ] Errores relevantes registrados en logs, sin secretos.
- [ ] `data.sql` / README actualizados si aplica.
- [ ] Compila con `mvn clean compile`.
- [ ] Commits en Conventional Commits en inglés.

## Cómo probarlo

<!-- Pasos y datos de prueba -->
