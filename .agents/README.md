# 🤖 Agentes Especialistas de CarMotors

Esta carpeta define los agentes de desarrollo especializados por módulo para el proyecto CarMotors SGT.

## Compatibilidad Multi-Herramienta (Universal)
La configuración sigue el estándar de prompt de contexto abierto (`system prompt` + archivos de documentación en Markdown + tool de búsqueda local `qmd`), lo que permite usarlos en:
- **Google Antigravity:** mediante definición local.
- **Claude Code:** referenciando el archivo en `.clauderc` o en el prompt inicial.
- **Gemini CLI:** apuntando como contexto base.
- **Codex / Deep Sea Harness:** cargando como contexto de sistema.

---

## Índice de Agentes

| Agente | Archivo | Módulo Asociado | Archivo de Contexto en Bóveda |
|---|---|---|---|
| `@agent-setup` | `agent-setup.md` | Configuración base | `docs/vault/01-Modulos/00-Setup.md` |
| `@agent-clientes` | `agent-clientes.md` | Clientes y Vehículos | `docs/vault/01-Modulos/01-Clientes-Vehiculos.md` |
| `@agent-proveedores` | `agent-proveedores.md` | Proveedores y Catálogo | `docs/vault/01-Modulos/02-Proveedores.md` |
| `@agent-inventario` | `agent-inventario.md` | Inventario y Lotes | `docs/vault/01-Modulos/03-Inventario.md` |
| `@agent-tecnicos` | `agent-tecnicos.md` | Técnicos y Especialidades | `docs/vault/01-Modulos/04-Tecnicos.md` |
| `@agent-servicios` | `agent-servicios.md` | Servicios y Órdenes | `docs/vault/01-Modulos/05-Servicios-Ordenes.md` |
| `@agent-facturacion` | `agent-facturacion.md` | Facturación Interna | `docs/vault/01-Modulos/06-Facturacion.md` |
| `@agent-reportes` | `agent-reportes.md` | Reportes Gerenciales | `docs/vault/01-Modulos/07-Reportes.md` |
| `@agent-database` | `agent-database.md` | Base de Datos (DDL/DML) | `docs/vault/01-Modulos/08-Base-Datos.md` |
