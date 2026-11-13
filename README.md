# CarMotors — Sistema de Gestión de Taller (SGT)

Proyecto académico de cinco días (Campusland, Java 17 · Maven · MySQL 8 · JDBC · consola) para administrar el taller **CarMotors**: inventario por lotes, órdenes de servicio con reserva y consumo FEFO, facturación con snapshots y reportes gerenciales.

## Estado actual

| Fase | Alcance | Estado |
|---|---|---|
| 1 | GitHub base: Project #6, etiquetas, plantillas, ruleset de `main` y `develop` | ✅ |
| 2 | Bóveda Obsidian + colección QMD `carmotors-vault` | ✅ |
| 3 | Documentación por módulo derivada del documento maestro | ✅ |
| 4 | Agentes especialistas y skills compartidas | ✅ |
| 5 | Issues y reparto de trabajo en el tablero | ⏳ pendiente |
| 6 | Implementación Java | ⏳ pendiente |

## Documentación

- **Documento maestro (fuente única de verdad):** [docs/vault/00-Documento-Maestro-v1.3.md](docs/vault/00-Documento-Maestro-v1.3.md) — 35 RF, 22 RN, 16 HU y 25 RNF.
- **Bóveda Obsidian:** [docs/vault/](docs/vault/) — abrir esa carpeta como bóveda. Empieza por [00-Indice-General.md](docs/vault/00-MOC/00-Indice-General.md).
- **Modelo relacional y diagrama de clases:** [Modelo-Relacional.md](docs/vault/02-Arquitectura/Modelo-Relacional.md) · [Diagrama-Clases.md](docs/vault/02-Arquitectura/Diagrama-Clases.md).
- **Historias de usuario con criterios Dado/Cuando/Entonces:** [docs/vault/04-Historias/](docs/vault/04-Historias/).
- **Tablero:** [CarMotors #6](https://github.com/users/julio7879/projects/6).

Los documentos marcados como *derivados* se generan desde el maestro y no se editan a mano:

```powershell
powershell -NoProfile -File ./scripts/sync-vault-from-master.ps1   # regenera derivados
powershell -NoProfile -File ./scripts/check-traceability.ps1        # falla si algo no está trazado
```

## Búsqueda con IA (QMD)

La bóveda está indexada en QMD, así que cualquier agente puede consultar la especificación sin inventarla:

```bash
qmd search "FEFO" -c carmotors-vault          # búsqueda por palabras clave (BM25)
qmd query "como se reserva un repuesto" -c carmotors-vault   # expansión semántica
```

## Agentes y skills

- [.agents/README.md](.agents/README.md) — 9 agentes especialistas, uno por módulo.
- [.agents/skills/carmotors-qmd-consultant](.agents/skills/carmotors-qmd-consultant/SKILL.md) — consulta de la bóveda.
- [.agents/skills/carmotors-rules-validator](.agents/skills/carmotors-rules-validator/SKILL.md) — checklist de FEFO, transacciones, `BigDecimal` y snapshots.

## Flujo de trabajo

- Ramas: `main` (estable), `develop` (integración), `feature/*` desde `develop`. Ver [Git-Flow.md](docs/vault/03-Gestion/Git-Flow.md).
- Todo cambio entra a `main` o `develop` por Pull Request con revisión de [CODEOWNERS](.github/CODEOWNERS).
- Criterios de cierre en [Definition-of-Done.md](docs/vault/03-Gestion/Definition-of-Done.md).
- Commits en inglés con Conventional Commits (`feat:`, `fix:`, `docs:`…).

## Estructura

```text
.agents/            agentes y skills (Markdown, portable entre herramientas)
.github/            plantillas de Issue/PR y CODEOWNERS
docs/vault/         bóveda Obsidian: MOC, módulos, arquitectura, gestión, historias
scripts/            sincronización y verificación de trazabilidad
```

## Equipo

Julio (`@julio7879`, líder y arquitectura) · Jhorman (`@JopsWtD`) · Andrés Jiménez (`@AlmX4`) · Sofía (`@Sofi11017`).

> Pendiente de implementación: `pom.xml`, `database/schema.sql`, `database/data.sql` y el código en `src/`. Hasta que existan, las instrucciones de instalación y ejecución quedan en el documento maestro (§5.2 y §7).
