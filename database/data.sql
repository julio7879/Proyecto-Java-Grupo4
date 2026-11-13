-- =====================================================================
-- CarMotors - Sistema de Gestión de Taller (SGT)
-- data.sql : datos de prueba (ejecutar DESPUÉS de schema.sql)
-- =====================================================================

USE carmotors;

-- ---------------------------------------------------------------------
-- 1. TABLAS DE CONSULTA
-- ---------------------------------------------------------------------

INSERT INTO servicio_estados (id, nombre) VALUES
    (1, 'Pendiente'),
    (2, 'En Proceso'),
    (3, 'Completado'),
    (4, 'Facturado'),
    (5, 'Entregado'),
    (6, 'Cancelada');   -- Solo si el equipo conserva RF-14; si no, eliminar esta fila

INSERT INTO mantenimiento_tipos (id, nombre) VALUES
    (1, 'Preventivo'),
    (2, 'Correctivo');

INSERT INTO repuesto_tipos (id, nombre) VALUES
    (1, 'Mecánico'),
    (2, 'Eléctrico'),
    (3, 'Carrocería'),
    (4, 'Consumo');

INSERT INTO especialidades (id, nombre) VALUES
    (1, 'Mecánica general'),
    (2, 'Motor'),
    (3, 'Frenos'),
    (4, 'Eléctrica'),
    (5, 'Suspensión y dirección'),
    (6, 'Carrocería');

-- ---------------------------------------------------------------------
-- 2. CLIENTES Y VEHÍCULOS
-- ---------------------------------------------------------------------

INSERT INTO clientes (id, nombre, documento_identidad, telefono, email, activo) VALUES
    (1, 'María Fernanda Rojas',      '1094567801',    '3105551101', 'maria.rojas@correo.com',          TRUE),
    (2, 'Carlos Andrés Peña',        '1090123402',    '3115551102', 'carlos.pena@correo.com',          TRUE),
    (3, 'Luisa Marcela Díaz',        '1093456703',    '3125551103', 'luisa.diaz@correo.com',           TRUE),
    (4, 'Jorge Eduardo Castillo',    '88245604',      '3135551104', 'jorge.castillo@correo.com',       TRUE),
    (5, 'Sandra Milena Vega',        '60345205',      '3145551105', 'sandra.vega@correo.com',          TRUE),
    (6, 'Transportes Rápidos S.A.S.','900123456-7',   '3155551106', 'contacto@transportesrapidos.com', TRUE),
    (7, 'Pablo Ortiz',               '1091234507',    '3165551107', 'pablo.ortiz@correo.com',          FALSE);

INSERT INTO vehiculos (id, cliente_id, placa, marca, modelo) VALUES
    (1, 1, 'ABC123', 'Chevrolet', 'Spark GT'),
    (2, 2, 'DEF456', 'Renault',   'Logan'),
    (3, 3, 'GHI789', 'Mazda',     '3'),
    (4, 4, 'JKL012', 'Kia',       'Rio'),
    (5, 5, 'MNO345', 'Toyota',    'Hilux'),
    (6, 6, 'PQR678', 'Chevrolet', 'NPR'),
    (7, 6, 'STU901', 'Hyundai',   'H100'),
    (8, 1, 'VWX234', 'Renault',   'Duster');

-- ---------------------------------------------------------------------
-- 3. TÉCNICOS Y CATÁLOGO DE SERVICIOS
-- ---------------------------------------------------------------------

INSERT INTO tecnicos (id, nombre, documento_identidad, especialidad_id, telefono, activo) VALUES
    (1, 'Pedro Ramírez',   '1090456123', 3, '3205550001', TRUE),   -- Frenos
    (2, 'Juan Pérez',      '1090456124', 2, '3205550002', TRUE),   -- Motor
    (3, 'Laura Gómez',     '1090456125', 4, '3205550003', TRUE),   -- Eléctrica
    (4, 'Carlos Mendoza',  '1090456126', 5, '3205550004', TRUE),   -- Suspensión y dirección
    (5, 'Andrea Torres',   '1090456127', 1, '3205550005', TRUE),   -- Mecánica general
    (6, 'Luis Ortega',     '1090456128', 6, '3205550006', FALSE),  -- Carrocería (inactivo)
    (7, 'Diego Suárez',    '1090456129', 3, '3205550007', TRUE);   -- Frenos

INSERT INTO servicio_tipos (id, nombre, mantenimiento_tipo_id, especialidad_id, precio_mano_obra_base, intervalo_meses) VALUES
    (1,  'Cambio de aceite y filtro',               1, 1,  60000.00, 6),
    (2,  'Revisión y cambio de pastillas de freno', 2, 3,  90000.00, NULL),
    (3,  'Alineación y balanceo',                   1, 5,  70000.00, 12),
    (4,  'Cambio de batería',                       2, 4,  40000.00, NULL),
    (5,  'Afinación de motor',                      1, 2, 150000.00, 12),
    (6,  'Reparación de suspensión',                2, 5, 180000.00, NULL),
    (7,  'Pintura y latonería parcial',             2, 6, 250000.00, NULL),
    (8,  'Revisión del sistema eléctrico',          1, 4,  50000.00, 12),
    (9,  'Cambio de filtro de aire y cabina',       1, 1,  30000.00, 12),
    (10, 'Cambio de líquido de frenos',             1, 3,  55000.00, 24);

-- ---------------------------------------------------------------------
-- 4. PROVEEDORES
-- ---------------------------------------------------------------------

INSERT INTO proveedores (id, nombre, telefono, email, direccion, activo) VALUES
    (1, 'AutoPartes del Norte',      '5775550101', 'ventas@autopartesnorte.com',  'Av. 5 # 12-30, Cúcuta',      TRUE),
    (2, 'Frenos y Más',              '5775550102', 'pedidos@frenosymas.com',      'Calle 10 # 4-25, Cúcuta',    TRUE),
    (3, 'Lubricantes Andinos',       '5775550103', 'comercial@lubriandinos.com',  'Zona Industrial, Cúcuta',    TRUE),
    (4, 'ElectroCar Colombia',       '6015550104', 'info@electrocar.co',          'Cra. 30 # 15-10, Bogotá',    TRUE),
    (5, 'Importadora Sur',           '6025550105', NULL,                          'Calle 9 # 20-15, Cali',      TRUE);

-- ---------------------------------------------------------------------
-- 5. REPUESTOS
--    El stock de cada repuesto = suma de cantidad_actual de sus lotes (ver sección 6)
--    cantidad_reservada = suma de lo asignado en órdenes Pendiente / En Proceso
-- ---------------------------------------------------------------------

INSERT INTO repuestos (id, repuesto_tipo_id, nombre, marca, precio_venta, cantidad_reservada, stock_minimo, activo) VALUES
    (1,  4, 'Aceite sintético 10W-40 (litro)',  'Mobil',   38000.00, 4, 10, TRUE),   -- stock en lotes: 56
    (2,  4, 'Filtro de aceite',                 'Fram',    25000.00, 1,  5, TRUE),   -- 18
    (3,  4, 'Filtro de aire',                   'Mann',    32000.00, 0,  4, TRUE),   -- 15
    (4,  1, 'Pastillas de freno delanteras',    'Bosch',  120000.00, 1,  4, TRUE),   -- 10
    (5,  1, 'Disco de freno delantero',         'Brembo', 210000.00, 0,  4, TRUE),   -- 3 (stock bajo)
    (6,  4, 'Líquido de frenos DOT4 (500 ml)',  'Bosch',   28000.00, 1,  4, TRUE),   -- 14
    (7,  2, 'Batería 12V 45Ah',                 'Varta',  380000.00, 0,  2, TRUE),   -- 5
    (8,  2, 'Bujía de iridio',                  'NGK',     45000.00, 0,  8, TRUE),   -- 20
    (9,  1, 'Amortiguador delantero',           'Monroe', 260000.00, 0,  2, TRUE),   -- 8
    (10, 3, 'Kit de pintura de retoque',        'Sikkens', 90000.00, 0,  2, TRUE),   -- 6
    (11, 1, 'Correa de distribución',           'Gates',  150000.00, 0,  2, FALSE),  -- 5 (inactivo)
    (12, 4, 'Filtro de cabina',                 'Mann',    35000.00, 0,  3, TRUE);   -- 10

-- Catálogo proveedor - repuesto (un solo proveedor habitual por repuesto)
INSERT INTO proveedor_repuestos (proveedor_id, repuesto_id, costo, tiempo_entrega_dias, es_habitual) VALUES
    (3, 1,   24000.00, 2, TRUE),
    (1, 1,   26000.00, 3, FALSE),
    (2, 1,   25500.00, 4, FALSE),
    (3, 2,   15000.00, 2, TRUE),
    (1, 3,   20000.00, 3, TRUE),
    (2, 4,   78000.00, 3, TRUE),
    (1, 4,   85000.00, 4, FALSE),
    (2, 5,  140000.00, 4, TRUE),
    (2, 6,   16000.00, 2, TRUE),
    (4, 7,  260000.00, 3, TRUE),
    (4, 8,   28000.00, 3, TRUE),
    (1, 9,  170000.00, 5, TRUE),
    (1, 10,  55000.00, 4, TRUE),
    (1, 11,  95000.00, 6, TRUE),
    (1, 12,  22000.00, 3, TRUE);

-- ---------------------------------------------------------------------
-- 6. LOTES (el proveedor de cada lote ofrece ese repuesto - RN-14)
-- ---------------------------------------------------------------------

INSERT INTO lotes (id, codigo_lote, proveedor_id, repuesto_id, fecha_ingreso, fecha_caducidad, cantidad_inicial, cantidad_actual) VALUES
    (1,  'LOT-ACE-2026-001', 3, 1,  '2026-03-10', '2028-03-10', 40, 32),
    (2,  'LOT-ACE-2026-002', 3, 1,  '2026-09-15', '2028-09-15', 24, 24),
    (3,  'LOT-FAC-2026-001', 3, 2,  '2026-04-02', NULL,         20, 18),
    (4,  'LOT-FAR-2026-001', 1, 3,  '2026-05-12', NULL,         15, 15),
    (5,  'LOT-PAS-2026-001', 2, 4,  '2026-06-18', NULL,         10, 10),
    (6,  'LOT-DIS-2026-001', 2, 5,  '2026-07-22', NULL,          3,  3),
    (7,  'LOT-LIQ-2026-001', 2, 6,  '2026-06-01', '2026-12-01', 12, 12),   -- caduca en ~18 días
    (8,  'LOT-LIQ-2024-001', 2, 6,  '2024-10-01', '2026-10-01',  6,  2),   -- ya caducado
    (9,  'LOT-BAT-2026-001', 4, 7,  '2026-08-20', NULL,          6,  5),
    (10, 'LOT-BUJ-2026-001', 4, 8,  '2026-05-30', NULL,         24, 20),
    (11, 'LOT-AMO-2026-001', 1, 9,  '2026-07-05', NULL,          8,  8),
    (12, 'LOT-PIN-2026-001', 1, 10, '2026-08-01', '2027-08-01',  6,  6),
    (13, 'LOT-COR-2026-001', 1, 11, '2026-04-20', NULL,          5,  5),
    (14, 'LOT-FCA-2026-001', 1, 12, '2026-09-02', NULL,         10, 10);

-- ---------------------------------------------------------------------
-- 7. EVALUACIONES DE PROVEEDORES (la Importadora Sur queda sin evaluar)
-- ---------------------------------------------------------------------

INSERT INTO evaluaciones_proveedor (proveedor_id, fecha, calidad, puntualidad, comentario) VALUES
    (1, '2026-06-20', 4, 5, 'Buen servicio y entregas a tiempo'),
    (1, '2026-09-10', 5, 4, NULL),
    (2, '2026-08-05', 5, 5, 'Excelente calidad en frenos'),
    (3, '2026-07-12', 4, 3, 'Entrega con un día de retraso'),
    (4, '2026-09-01', 3, 4, 'Producto correcto, empaque dañado');

-- ---------------------------------------------------------------------
-- 8. ÓRDENES DE SERVICIO (una por cada estado)
-- ---------------------------------------------------------------------

INSERT INTO ordenes_servicio (id, vehiculo_id, estado_id, fecha_recepcion, fecha_completado, fecha_entrega, fecha_cancelacion, descripcion_problema) VALUES
    (1, 1, 1, '2026-11-12 08:30:00', NULL,                  NULL,                  NULL,                  'Cambio de aceite por kilometraje'),
    (2, 2, 2, '2026-11-11 09:15:00', NULL,                  NULL,                  NULL,                  'Ruido al frenar y vibración en el volante'),
    (3, 3, 3, '2026-11-10 10:00:00', '2026-11-12 16:45:00', NULL,                  NULL,                  'El vehículo no enciende en las mañanas'),
    (4, 4, 4, '2026-11-05 08:00:00', '2026-11-06 15:30:00', NULL,                  NULL,                  'Mantenimiento general de los 20.000 km'),
    (5, 5, 5, '2026-05-27 09:00:00', '2026-05-28 11:00:00', '2026-05-29 10:00:00', NULL,                  'Cambio de aceite programado'),
    (6, 6, 6, '2026-10-20 14:00:00', NULL,                  NULL,                  '2026-10-21 09:00:00', 'Alineación; el cliente canceló la visita');

INSERT INTO orden_servicio_detalle (id, orden_id, servicio_tipo_id, nombre_servicio_snapshot, costo_mano_obra, observaciones) VALUES
    (1, 1, 1, 'Cambio de aceite y filtro',               60000.00, NULL),
    (2, 2, 2, 'Revisión y cambio de pastillas de freno', 90000.00, 'Revisar también los discos'),
    (3, 2, 3, 'Alineación y balanceo',                   70000.00, NULL),
    (4, 3, 4, 'Cambio de batería',                       40000.00, 'Batería sulfatada'),
    (5, 4, 1, 'Cambio de aceite y filtro',               60000.00, NULL),
    (6, 4, 5, 'Afinación de motor',                     150000.00, 'Incluye cambio de bujías'),
    (7, 5, 1, 'Cambio de aceite y filtro',               60000.00, NULL),
    (8, 6, 3, 'Alineación y balanceo',                   70000.00, NULL);

-- Técnicos por línea (las líneas de órdenes Pendiente / Cancelada no tienen técnico)
INSERT INTO detalle_tecnicos (detalle_id, tecnico_id, horas_trabajadas) VALUES
    (2, 1, 2.00),
    (3, 4, 1.50),
    (4, 3, 1.00),
    (5, 5, 1.00),
    (6, 2, 3.00),
    (7, 5, 1.00);

-- Repuestos por línea (el precio queda copiado al asignar - RN-11)
INSERT INTO detalle_repuestos (detalle_id, repuesto_id, cantidad_asignada, nombre_repuesto_snapshot, precio_unitario_venta) VALUES
    (1, 1, 4, 'Aceite sintético 10W-40 (litro)', 38000.00),
    (1, 2, 1, 'Filtro de aceite',                25000.00),
    (2, 4, 1, 'Pastillas de freno delanteras',  120000.00),
    (2, 6, 1, 'Líquido de frenos DOT4 (500 ml)', 28000.00),
    (4, 7, 1, 'Batería 12V 45Ah',               380000.00),
    (5, 1, 4, 'Aceite sintético 10W-40 (litro)', 38000.00),
    (5, 2, 1, 'Filtro de aceite',                25000.00),
    (6, 8, 4, 'Bujía de iridio',                 45000.00),
    (7, 1, 4, 'Aceite sintético 10W-40 (litro)', 38000.00),
    (7, 2, 1, 'Filtro de aceite',                25000.00);

-- ---------------------------------------------------------------------
-- 9. FACTURAS (IVA 19 %, RN-12)
--    Factura 1: orden 5 -> repuestos 177000 + mano obra 60000 = 237000; IVA 45030; total 282030
--    Factura 2: orden 4 -> repuestos 357000 + mano obra 210000 = 567000; IVA 107730; total 674730
-- ---------------------------------------------------------------------

INSERT INTO facturas (id, numero_factura, orden_id, fecha_emision, total_repuestos, total_mano_obra, subtotal, tasa_iva, impuestos, total_final, archivo_generado) VALUES
    (1, 'F-2026-0001', 5, '2026-05-28 12:00:00', 177000.00,  60000.00, 237000.00, 0.1900,  45030.00, 282030.00, TRUE),
    (2, 'F-2026-0002', 4, '2026-11-06 16:00:00', 357000.00, 210000.00, 567000.00, 0.1900, 107730.00, 674730.00, TRUE);

INSERT INTO factura_snapshot (factura_id, cliente_nombre_snapshot, cliente_documento_snapshot, cliente_telefono_snapshot, cliente_email_snapshot, vehiculo_placa_snapshot, vehiculo_marca_snapshot, vehiculo_modelo_snapshot) VALUES
    (1, 'Sandra Milena Vega',     '60345205', '3145551105', 'sandra.vega@correo.com',    'MNO345', 'Toyota', 'Hilux'),
    (2, 'Jorge Eduardo Castillo', '88245604', '3135551104', 'jorge.castillo@correo.com', 'JKL012', 'Kia',    'Rio');

INSERT INTO factura_detalles (id, factura_id, orden_detalle_id, repuesto_id, tipo_concepto, descripcion_snapshot, cantidad, precio_unitario, subtotal) VALUES
    -- Factura 1 (orden 5)
    (1, 1, 7, NULL, 'MANO_OBRA', 'Cambio de aceite y filtro',         1,  60000.00,  60000.00),
    (2, 1, 7, 1,    'REPUESTO',  'Aceite sintético 10W-40 (litro)',   4,  38000.00, 152000.00),
    (3, 1, 7, 2,    'REPUESTO',  'Filtro de aceite',                  1,  25000.00,  25000.00),
    -- Factura 2 (orden 4)
    (4, 2, 5, NULL, 'MANO_OBRA', 'Cambio de aceite y filtro',         1,  60000.00,  60000.00),
    (5, 2, 5, 1,    'REPUESTO',  'Aceite sintético 10W-40 (litro)',   4,  38000.00, 152000.00),
    (6, 2, 5, 2,    'REPUESTO',  'Filtro de aceite',                  1,  25000.00,  25000.00),
    (7, 2, 6, NULL, 'MANO_OBRA', 'Afinación de motor',                1, 150000.00, 150000.00),
    (8, 2, 6, 8,    'REPUESTO',  'Bujía de iridio',                   4,  45000.00, 180000.00);
