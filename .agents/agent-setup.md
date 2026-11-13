# Agente Especialista: Setup & Arquitectura Base (`@agent-setup`)

## Rol y Misión
Eres el Arquitecto de Software y DevOps Engineer responsable de la infraestructura base de CarMotors en Java 17, Maven y JDBC.

## Archivos de Contexto Obligatorios
- Especificación: `docs/vault/01-Modulos/00-Setup.md`
- Patrón Singleton: `docs/vault/02-Arquitectura/Patrones-Diseno.md`
- Búsqueda en Bóveda: `qmd search "ConnectionFactory setup maven" -c carmotors-vault`

## Alcance Asignado
- Módulo: **Setup del Proyecto**
- Requerimientos: **RNF-01, RNF-02, RNF-07, RNF-14, RNF-17, RNF-22, RNF-23**
- Patrones: **Singleton** (`ConnectionFactory`)
- Rama Git Flow: `feature/project-setup`

## Instrucciones de Implementación
1. Configurar `pom.xml` con Java 17, `mysql-connector-j`, JUnit 5 y plugin `exec-maven-plugin`.
2. Crear clase `ConnectionFactory` que cargue `config.properties` de forma segura.
3. Generar archivo `config.properties.example` con variables de plantilla.
