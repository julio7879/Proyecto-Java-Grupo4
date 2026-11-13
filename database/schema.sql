-- =============================================================
-- CarMotors - Sistema de Gestión de Taller
-- Esquema de base de datos (MySQL 8). Crea todo desde cero.
-- =============================================================

DROP DATABASE IF EXISTS carmotors;
CREATE DATABASE carmotors CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE carmotors;

-- -------------------------------------------------------------
-- Tablas de consulta
-- -------------------------------------------------------------

CREATE TABLE repuesto_tipos (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
) ENGINE = InnoDB;

CREATE TABLE servicio_estados (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
) ENGINE = InnoDB;

CREATE TABLE mantenimiento_tipos (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
) ENGINE = InnoDB;

CREATE TABLE especialidades (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
) ENGINE = InnoDB;

-- -------------------------------------------------------------
-- Clientes y vehículos
-- -------------------------------------------------------------

CREATE TABLE clientes (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    nombre              VARCHAR(100) NOT NULL,
    documento_identidad VARCHAR(20)  NOT NULL UNIQUE,
    telefono            VARCHAR(20),
    email               VARCHAR(100) NOT NULL UNIQUE,
    activo              BOOLEAN      NOT NULL DEFAULT TRUE
) ENGINE = InnoDB;

CREATE TABLE vehiculos (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT         NOT NULL,
    placa      VARCHAR(10) NOT NULL UNIQUE,
    marca      VARCHAR(50),
    modelo     VARCHAR(50),
    CONSTRAINT fk_vehiculos_cliente
        FOREIGN KEY (cliente_id) REFERENCES clientes (id)
) ENGINE = InnoDB;

-- -------------------------------------------------------------
-- Proveedores e inventario
-- -------------------------------------------------------------

CREATE TABLE proveedores (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL,
    telefono  VARCHAR(20),
    email     VARCHAR(100),
    direccion VARCHAR(200),
    activo    BOOLEAN      NOT NULL DEFAULT TRUE
) ENGINE = InnoDB;

CREATE TABLE repuestos (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    nombre             VARCHAR(100)  NOT NULL,
    repuesto_tipo_id   INT,
    marca              VARCHAR(50),
    precio_compra      DECIMAL(10, 2),
    precio_venta       DECIMAL(10, 2),
    cantidad_stock     INT           NOT NULL DEFAULT 0,
    cantidad_reservada INT           NOT NULL DEFAULT 0,
    stock_minimo       INT           NOT NULL,
    activo             BOOLEAN       NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_repuestos_tipo
        FOREIGN KEY (repuesto_tipo_id) REFERENCES repuesto_tipos (id),
    CONSTRAINT chk_repuestos_stock      CHECK (cantidad_stock >= 0),
    CONSTRAINT chk_repuestos_reservada  CHECK (cantidad_reservada >= 0),
    CONSTRAINT chk_repuestos_reserva_ok CHECK (cantidad_reservada <= cantidad_stock)
) ENGINE = InnoDB;

-- Relación muchos a muchos entre proveedores y repuestos.
CREATE TABLE proveedor_repuestos (
    proveedor_id        INT            NOT NULL,
    repuesto_id         INT            NOT NULL,
    costo               DECIMAL(10, 2) NOT NULL,
    tiempo_entrega_dias INT,
    es_habitual         BOOLEAN        NOT NULL DEFAULT FALSE,
    PRIMARY KEY (proveedor_id, repuesto_id),
    CONSTRAINT fk_prov_rep_proveedor
        FOREIGN KEY (proveedor_id) REFERENCES proveedores (id),
    CONSTRAINT fk_prov_rep_repuesto
        FOREIGN KEY (repuesto_id) REFERENCES repuestos (id)
) ENGINE = InnoDB;

CREATE TABLE lotes (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    codigo_lote         VARCHAR(50) NOT NULL UNIQUE,
    repuesto_id         INT         NOT NULL,
    proveedor_id        INT         NOT NULL,
    fecha_ingreso       DATE        NOT NULL,
    fecha_caducidad     DATE        NULL,
    cantidad_inicial    INT         NOT NULL,
    cantidad_disponible INT         NOT NULL,
    CONSTRAINT fk_lotes_repuesto
        FOREIGN KEY (repuesto_id) REFERENCES repuestos (id),
    CONSTRAINT fk_lotes_proveedor
        FOREIGN KEY (proveedor_id) REFERENCES proveedores (id)
) ENGINE = InnoDB;

CREATE TABLE evaluaciones_proveedor (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    proveedor_id INT     NOT NULL,
    fecha        DATE    NOT NULL,
    calidad      TINYINT,
    puntualidad  TINYINT,
    comentario   VARCHAR(500),
    CONSTRAINT fk_evaluaciones_proveedor
        FOREIGN KEY (proveedor_id) REFERENCES proveedores (id),
    CONSTRAINT chk_evaluaciones_calidad     CHECK (calidad BETWEEN 1 AND 5),
    CONSTRAINT chk_evaluaciones_puntualidad CHECK (puntualidad BETWEEN 1 AND 5)
) ENGINE = InnoDB;

-- -------------------------------------------------------------
-- Técnicos y catálogo de servicios
-- -------------------------------------------------------------

CREATE TABLE tecnicos (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    nombre              VARCHAR(100) NOT NULL,
    documento_identidad VARCHAR(20)  NOT NULL UNIQUE,
    especialidad_id     INT,
    telefono            VARCHAR(20),
    activo              BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_tecnicos_especialidad
        FOREIGN KEY (especialidad_id) REFERENCES especialidades (id)
) ENGINE = InnoDB;

CREATE TABLE servicio_tipos (
    id                    INT AUTO_INCREMENT PRIMARY KEY,
    nombre                VARCHAR(100)   NOT NULL UNIQUE,
    mantenimiento_tipo_id INT,
    especialidad_id       INT,
    precio_mano_obra_base DECIMAL(10, 2),
    intervalo_meses       INT            NULL,
    CONSTRAINT fk_servicio_tipos_mantenimiento
        FOREIGN KEY (mantenimiento_tipo_id) REFERENCES mantenimiento_tipos (id),
    CONSTRAINT fk_servicio_tipos_especialidad
        FOREIGN KEY (especialidad_id) REFERENCES especialidades (id)
) ENGINE = InnoDB;

-- -------------------------------------------------------------
-- Órdenes de servicio
-- -------------------------------------------------------------

CREATE TABLE ordenes_servicio (
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    vehiculo_id          INT      NOT NULL,
    fecha_recepcion      DATETIME NOT NULL,
    fecha_entrega        DATETIME NULL,
    descripcion_problema TEXT,
    estado_id            INT      NOT NULL,
    CONSTRAINT fk_ordenes_vehiculo
        FOREIGN KEY (vehiculo_id) REFERENCES vehiculos (id),
    CONSTRAINT fk_ordenes_estado
        FOREIGN KEY (estado_id) REFERENCES servicio_estados (id)
) ENGINE = InnoDB;

CREATE TABLE orden_servicio_detalle (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    orden_id         INT            NOT NULL,
    servicio_tipo_id INT            NOT NULL,
    costo_mano_obra  DECIMAL(10, 2),
    observaciones    VARCHAR(500),
    CONSTRAINT fk_detalle_orden
        FOREIGN KEY (orden_id) REFERENCES ordenes_servicio (id),
    CONSTRAINT fk_detalle_servicio_tipo
        FOREIGN KEY (servicio_tipo_id) REFERENCES servicio_tipos (id)
) ENGINE = InnoDB;

CREATE TABLE detalle_tecnicos (
    detalle_id       INT          NOT NULL,
    tecnico_id       INT          NOT NULL,
    horas_trabajadas DECIMAL(5, 2),
    PRIMARY KEY (detalle_id, tecnico_id),
    CONSTRAINT fk_det_tec_detalle
        FOREIGN KEY (detalle_id) REFERENCES orden_servicio_detalle (id),
    CONSTRAINT fk_det_tec_tecnico
        FOREIGN KEY (tecnico_id) REFERENCES tecnicos (id)
) ENGINE = InnoDB;

CREATE TABLE detalle_repuestos (
    detalle_id            INT            NOT NULL,
    repuesto_id           INT            NOT NULL,
    cantidad_usada        INT            NOT NULL,
    precio_unitario_venta DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (detalle_id, repuesto_id),
    CONSTRAINT fk_det_rep_detalle
        FOREIGN KEY (detalle_id) REFERENCES orden_servicio_detalle (id),
    CONSTRAINT fk_det_rep_repuesto
        FOREIGN KEY (repuesto_id) REFERENCES repuestos (id),
    CONSTRAINT chk_det_rep_cantidad CHECK (cantidad_usada > 0)
) ENGINE = InnoDB;

-- -------------------------------------------------------------
-- Facturación
-- -------------------------------------------------------------

CREATE TABLE facturas (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    numero_factura  VARCHAR(20)    NOT NULL UNIQUE,
    orden_id        INT            NOT NULL UNIQUE,
    fecha_emision   DATETIME       NOT NULL,
    total_repuestos DECIMAL(10, 2),
    total_mano_obra DECIMAL(10, 2),
    impuestos       DECIMAL(10, 2),
    total_final     DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_facturas_orden
        FOREIGN KEY (orden_id) REFERENCES ordenes_servicio (id)
) ENGINE = InnoDB;
