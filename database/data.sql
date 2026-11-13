-- =============================================================
-- CarMotors - Datos iniciales de las tablas de consulta.
-- Ejecutar después de schema.sql.
-- =============================================================

USE carmotors;

-- El orden de inserción define los id: el código puede depender de ellos.
INSERT INTO servicio_estados (nombre) VALUES
    ('Pendiente'),
    ('En Proceso'),
    ('Completado'),
    ('Facturado'),
    ('Entregado');

INSERT INTO mantenimiento_tipos (nombre) VALUES
    ('Preventivo'),
    ('Correctivo');

INSERT INTO repuesto_tipos (nombre) VALUES
    ('Mecánico'),
    ('Eléctrico'),
    ('Carrocería'),
    ('Consumo');

INSERT INTO especialidades (nombre) VALUES
    ('Mecánica general'),
    ('Motor'),
    ('Frenos'),
    ('Eléctrica'),
    ('Suspensión y dirección'),
    ('Carrocería');
