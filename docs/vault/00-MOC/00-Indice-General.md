# 🚗 CarMotors SGT — Bóveda de Documentación (MOC)

Bienvenido a la base de conocimiento y documentación técnica del **Sistema de Gestión de Taller (SGT) de CarMotors**.

- **Versión del documento maestro:** 1.2 — consolidada
- **Stack:** Java 17 · Maven · MySQL 8 · JDBC · Aplicación de Consola
- **Repositorio:** [julio7879/Proyecto-Java-Grupo4](https://github.com/julio7879/Proyecto-Java-Grupo4)
- **Tablero GitHub Projects:** [CarMotors Board #6](https://github.com/users/julio7879/projects/6)
- **Metodología:** Git Flow (`main`, `develop`, `feature/*`, `fix/*`)

---

## 🗺️ Mapa de Módulos del Sistema

Cada módulo cuenta con su especificación detallada, entidades, reglas de negocio, requerimientos funcionales, historias de usuario y agente especialista asignado:

| Módulo | Documento | Alcance principal | Agente Especialista |
|---|---|---|---|
| **00. Configuración & Setup** | [[00-Setup]] | Maven, JDBC ConnectionFactory, .gitignore, config.properties | `@agent-setup` |
| **01. Clientes y Vehículos** | [[01-Clientes-Vehiculos]] | RF-17 a RF-21 · HU-09 a HU-11 · Clientes, placas únicas, historial | `@agent-clientes` |
| **02. Proveedores** | [[02-Proveedores]] | RF-28 a RF-32 · HU-14, HU-15 · Catálogo M:N, evaluaciones, ranking | `@agent-proveedores` |
| **03. Inventario & Lotes** | [[03-Inventario]] | RF-01 a RF-07 · HU-01 a HU-03 · Lotes, stock derivado, alertas | `@agent-inventario` |
| **04. Técnicos & Especialidades** | [[04-Tecnicos]] | RF-33, RF-34 · HU-16 · Carga de trabajo, sugerencia por especialidad | `@agent-tecnicos` |
| **05. Servicios & Órdenes** | [[05-Servicios-Ordenes]] | RF-08 a RF-16, RF-35 · HU-04 a HU-08 · Cabecera-detalle, FEFO, estados | `@agent-servicios` |
| **06. Facturación** | [[06-Facturacion]] | RF-22 a RF-24 · HU-12 · Snapshots inmutables, TXT UTF-8, IVA 19% | `@agent-facturacion` |
| **07. Reportes Gerenciales** | [[07-Reportes]] | RF-25 a RF-27 · HU-13 · Streams, productividad, servicios, consumo | `@agent-reportes` |
| **08. Base de Datos** | [[08-Base-Datos]] | Modelo relacional, schema.sql, data.sql (dataset recomendado) | `@agent-database` |

---

## 🏛️ Arquitectura y Diseño Técnico

- [[02-Arquitectura/Modelo-Relacional|Modelo Relacional & Diccionario de Datos]]
- [[02-Arquitectura/Patrones-Diseno|Patrones de Diseño (GoF + DAO)]]
- [[02-Arquitectura/Principios-SOLID|Principios SOLID aplicados]]
- [[02-Arquitectura/Transacciones-JDBC|Manejo de Transacciones JDBC & Concurrencia]]
- [[02-Arquitectura/Reglas-Negocio|Catálogo Maestro de Reglas de Negocio (RN-01 a RN-22)]]

---

## 👥 Equipo y Gestión

- **Líder de Proyecto & Arquitectura:** Julio (`@julio7879`) — *Aprobador obligatorio en CODEOWNERS*
- **Backend Developer:** Jhorman (`@JopsWtD`)
- **Backend Developer:** Andrés Jiménez (`@AlmX4`)
- **Backend Developer:** Sofía (`@Sofi11017`) — *Tareas sencillas, good first issues*

Guías de trabajo:
- [[03-Gestion/Git-Flow|Convenciones de Git Flow & Commits]]
- [[03-Gestion/Definition-of-Done|Definition of Done (DoD) & Checklist de Calidad]]
- [[03-Gestion/Plan-Iteraciones|Plan de 9 Ramas Feature & Dependencias]]
