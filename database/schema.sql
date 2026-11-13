-- =====================================================================
-- CarMotors - Sistema de Gestión de Taller (SGT)
-- Requiere MySQL 8.0.16 o superior (usa restricciones CHECK)
-- =====================================================================

DROP DATABASE IF EXISTS carmotors;
CREATE DATABASE carmotors
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;
USE carmotors;

-- ---------------------------------------------------------------------
-- 1. TABLAS DE CONSULTA (lookup)
-- ---------------------------------------------------------------------

CREATE TABLE servicio_estados (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL,
    CONSTRAINT uk_servicio_estados_nombre UNIQUE (nombre)
) ENGINE=InnoDB;

CREATE TABLE mantenimiento_tipos (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL,
    CONSTRAINT uk_mantenimiento_tipos_nombre UNIQUE (nombre)
) ENGINE=InnoDB;

CREATE TABLE repuesto_tipos (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    CONSTRAINT uk_repuesto_tipos_nombre UNIQUE (nombre)
) ENGINE=InnoDB;

CREATE TABLE especialidades (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    CONSTRAINT uk_especialidades_nombre UNIQUE (nombre)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 2. CLIENTES Y VEHÍCULOS
-- ---------------------------------------------------------------------

CREATE TABLE clientes (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    nombre              VARCHAR(100) NOT NULL,
    documento_identidad VARCHAR(30)  NOT NULL,
    telefono            VARCHAR(20),
    email               VARCHAR(100) NOT NULL,
    activo              BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_clientes_documento UNIQUE (documento_identidad),
    CONSTRAINT uk_clientes_email     UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE vehiculos (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT         NOT NULL,
    placa      VARCHAR(10) NOT NULL,
    marca      VARCHAR(50) NOT NULL,
    modelo     VARCHAR(50) NOT NULL,
    CONSTRAINT uk_vehiculos_placa UNIQUE (placa),
    CONSTRAINT fk_vehiculos_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 3. TÉCNICOS Y CATÁLOGO DE SERVICIOS
-- ---------------------------------------------------------------------

CREATE TABLE tecnicos (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    nombre              VARCHAR(100) NOT NULL,
    documento_identidad VARCHAR(30)  NOT NULL,
    especialidad_id     INT          NOT NULL,
    telefono            VARCHAR(20),
    activo              BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_tecnicos_documento UNIQUE (documento_identidad),
    CONSTRAINT fk_tecnicos_especialidad FOREIGN KEY (especialidad_id) REFERENCES especialidades (id)
) ENGINE=InnoDB;

CREATE TABLE servicio_tipos (
    id                    INT AUTO_INCREMENT PRIMARY KEY,
    nombre                VARCHAR(100)  NOT NULL,
    mantenimiento_tipo_id INT           NOT NULL,
    especialidad_id       INT           NOT NULL,
    precio_mano_obra_base DECIMAL(12,2) NOT NULL,
    intervalo_meses       INT NULL,
    CONSTRAINT uk_servicio_tipos_nombre UNIQUE (nombre),
    CONSTRAINT fk_servicio_tipos_mantenimiento FOREIGN KEY (mantenimiento_tipo_id) REFERENCES mantenimiento_tipos (id),
    CONSTRAINT fk_servicio_tipos_especialidad  FOREIGN KEY (especialidad_id)       REFERENCES especialidades (id),
    CONSTRAINT ck_servicio_tipos_precio    CHECK (precio_mano_obra_base >= 0),
    CONSTRAINT ck_servicio_tipos_intervalo CHECK (intervalo_meses IS NULL OR intervalo_meses > 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 4. PROVEEDORES, REPUESTOS Y LOTES
-- ---------------------------------------------------------------------

CREATE TABLE proveedores (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL,
    telefono  VARCHAR(20),
    email     VARCHAR(100),
    direccion VARCHAR(150),
    activo    BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE repuestos (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    repuesto_tipo_id   INT           NOT NULL,
    nombre             VARCHAR(100)  NOT NULL,
    marca              VARCHAR(50)   NOT NULL,
    precio_venta       DECIMAL(12,2) NOT NULL,
    cantidad_reservada INT           NOT NULL DEFAULT 0,
    stock_minimo       INT           NOT NULL DEFAULT 0,
    activo             BOOLEAN       NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_repuestos_tipo FOREIGN KEY (repuesto_tipo_id) REFERENCES repuesto_tipos (id),
    CONSTRAINT ck_repuestos_precio    CHECK (precio_venta >= 0),
    CONSTRAINT ck_repuestos_reservada CHECK (cantidad_reservada >= 0),
    CONSTRAINT ck_repuestos_minimo    CHECK (stock_minimo >= 0)
) ENGINE=InnoDB;

-- Relación M:N repuesto-proveedor (que haya un solo proveedor habitual por repuesto
-- se valida en la capa de servicio)
CREATE TABLE proveedor_repuestos (
    proveedor_id        INT           NOT NULL,
    repuesto_id         INT           NOT NULL,
    costo               DECIMAL(12,2) NOT NULL,
    tiempo_entrega_dias INT           NOT NULL DEFAULT 0,
    es_habitual         BOOLEAN       NOT NULL DEFAULT FALSE,
    PRIMARY KEY (proveedor_id, repuesto_id),
    CONSTRAINT fk_prov_rep_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores (id),
    CONSTRAINT fk_prov_rep_repuesto  FOREIGN KEY (repuesto_id)  REFERENCES repuestos (id),
    CONSTRAINT ck_prov_rep_costo   CHECK (costo > 0),
    CONSTRAINT ck_prov_rep_entrega CHECK (tiempo_entrega_dias >= 0)
) ENGINE=InnoDB;

CREATE TABLE lotes (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    codigo_lote       VARCHAR(40) NOT NULL,
    proveedor_id      INT         NOT NULL,
    repuesto_id       INT         NOT NULL,
    fecha_ingreso     DATE        NOT NULL,
    fecha_caducidad   DATE NULL,
    cantidad_inicial  INT         NOT NULL,
    cantidad_actual   INT         NOT NULL,
    CONSTRAINT uk_lotes_codigo UNIQUE (codigo_lote),
    CONSTRAINT fk_lotes_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores (id),
    CONSTRAINT fk_lotes_repuesto  FOREIGN KEY (repuesto_id)  REFERENCES repuestos (id),
    CONSTRAINT ck_lotes_inicial   CHECK (cantidad_inicial > 0),
    CONSTRAINT ck_lotes_actual    CHECK (cantidad_actual >= 0 AND cantidad_actual <= cantidad_inicial),
    CONSTRAINT ck_lotes_caducidad CHECK (fecha_caducidad IS NULL OR fecha_caducidad > fecha_ingreso)
) ENGINE=InnoDB;

CREATE TABLE evaluaciones_proveedor (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    proveedor_id INT  NOT NULL,
    fecha        DATE NOT NULL,
    calidad      INT  NOT NULL,
    puntualidad  INT  NOT NULL,
    comentario   VARCHAR(255),
    CONSTRAINT fk_evaluaciones_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores (id),
    CONSTRAINT ck_evaluaciones_calidad     CHECK (calidad BETWEEN 1 AND 5),
    CONSTRAINT ck_evaluaciones_puntualidad CHECK (puntualidad BETWEEN 1 AND 5)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 5. ÓRDENES DE SERVICIO (cabecera - detalle)
-- ---------------------------------------------------------------------

CREATE TABLE ordenes_servicio (
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    vehiculo_id          INT      NOT NULL,
    estado_id            INT      NOT NULL,
    fecha_recepcion      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_completado     DATETIME NULL,
    fecha_entrega        DATETIME NULL,
    fecha_cancelacion    DATETIME NULL,
    descripcion_problema VARCHAR(255) NOT NULL,
    CONSTRAINT fk_ordenes_vehiculo FOREIGN KEY (vehiculo_id) REFERENCES vehiculos (id),
    CONSTRAINT fk_ordenes_estado   FOREIGN KEY (estado_id)   REFERENCES servicio_estados (id)
) ENGINE=InnoDB;

CREATE TABLE orden_servicio_detalle (
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    orden_id                INT           NOT NULL,
    servicio_tipo_id        INT           NOT NULL,
    nombre_servicio_snapshot VARCHAR(100) NOT NULL,
    costo_mano_obra         DECIMAL(12,2) NOT NULL,
    observaciones           VARCHAR(255),
    CONSTRAINT fk_osd_orden         FOREIGN KEY (orden_id)         REFERENCES ordenes_servicio (id),
    CONSTRAINT fk_osd_servicio_tipo FOREIGN KEY (servicio_tipo_id) REFERENCES servicio_tipos (id),
    CONSTRAINT ck_osd_costo CHECK (costo_mano_obra >= 0)
) ENGINE=InnoDB;

CREATE TABLE detalle_tecnicos (
    detalle_id       INT           NOT NULL,
    tecnico_id       INT           NOT NULL,
    horas_trabajadas DECIMAL(5,2)  NOT NULL DEFAULT 0,
    PRIMARY KEY (detalle_id, tecnico_id),
    CONSTRAINT fk_dt_detalle FOREIGN KEY (detalle_id) REFERENCES orden_servicio_detalle (id) ON DELETE CASCADE,
    CONSTRAINT fk_dt_tecnico FOREIGN KEY (tecnico_id) REFERENCES tecnicos (id),
    CONSTRAINT ck_dt_horas CHECK (horas_trabajadas >= 0)
) ENGINE=InnoDB;

CREATE TABLE detalle_repuestos (
    detalle_id              INT           NOT NULL,
    repuesto_id             INT           NOT NULL,
    cantidad_asignada       INT           NOT NULL,
    nombre_repuesto_snapshot VARCHAR(100) NOT NULL,
    precio_unitario_venta   DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (detalle_id, repuesto_id),
    CONSTRAINT fk_dr_detalle  FOREIGN KEY (detalle_id)  REFERENCES orden_servicio_detalle (id) ON DELETE CASCADE,
    CONSTRAINT fk_dr_repuesto FOREIGN KEY (repuesto_id) REFERENCES repuestos (id),
    CONSTRAINT ck_dr_cantidad CHECK (cantidad_asignada > 0),
    CONSTRAINT ck_dr_precio   CHECK (precio_unitario_venta >= 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 6. FACTURACIÓN
-- ---------------------------------------------------------------------

CREATE TABLE facturas (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    numero_factura   VARCHAR(30)   NOT NULL,
    orden_id         INT           NOT NULL,
    fecha_emision    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_repuestos  DECIMAL(14,2) NOT NULL,
    total_mano_obra  DECIMAL(14,2) NOT NULL,
    subtotal         DECIMAL(14,2) NOT NULL,
    tasa_iva         DECIMAL(5,4)  NOT NULL,   -- 0.1900 = 19 %
    impuestos        DECIMAL(14,2) NOT NULL,
    total_final      DECIMAL(14,2) NOT NULL,
    archivo_generado BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_facturas_numero UNIQUE (numero_factura),
    CONSTRAINT uk_facturas_orden  UNIQUE (orden_id),          -- orden 1:1 factura
    CONSTRAINT fk_facturas_orden  FOREIGN KEY (orden_id) REFERENCES ordenes_servicio (id),
    CONSTRAINT ck_facturas_tasa   CHECK (tasa_iva >= 0),
    CONSTRAINT ck_facturas_totales CHECK (total_repuestos >= 0 AND total_mano_obra >= 0 AND total_final >= 0)
) ENGINE=InnoDB;

-- Copia de los datos del cliente y del vehículo al momento de facturar
CREATE TABLE factura_snapshot (
    factura_id                INT          PRIMARY KEY,
    cliente_nombre_snapshot   VARCHAR(100) NOT NULL,
    cliente_documento_snapshot VARCHAR(30) NOT NULL,
    cliente_telefono_snapshot VARCHAR(20),
    cliente_email_snapshot    VARCHAR(100),
    vehiculo_placa_snapshot   VARCHAR(10)  NOT NULL,
    vehiculo_marca_snapshot   VARCHAR(50)  NOT NULL,
    vehiculo_modelo_snapshot  VARCHAR(50)  NOT NULL,
    CONSTRAINT fk_factura_snapshot_factura FOREIGN KEY (factura_id) REFERENCES facturas (id)
) ENGINE=InnoDB;

CREATE TABLE factura_detalles (
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    factura_id           INT           NOT NULL,
    orden_detalle_id     INT           NOT NULL,
    repuesto_id          INT NULL,                              -- NULL cuando es MANO_OBRA
    tipo_concepto        ENUM('MANO_OBRA','REPUESTO') NOT NULL,
    descripcion_snapshot VARCHAR(150)  NOT NULL,
    cantidad             INT           NOT NULL,
    precio_unitario      DECIMAL(12,2) NOT NULL,
    subtotal             DECIMAL(14,2) NOT NULL,
    CONSTRAINT fk_fd_factura FOREIGN KEY (factura_id)       REFERENCES facturas (id),
    CONSTRAINT fk_fd_orden_detalle FOREIGN KEY (orden_detalle_id) REFERENCES orden_servicio_detalle (id),
    -- FK compuesta opcional: solo se valida cuando repuesto_id no es NULL
    CONSTRAINT fk_fd_detalle_repuesto FOREIGN KEY (orden_detalle_id, repuesto_id)
        REFERENCES detalle_repuestos (detalle_id, repuesto_id),
    CONSTRAINT ck_fd_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_fd_concepto CHECK (
        (tipo_concepto = 'MANO_OBRA' AND repuesto_id IS NULL) OR
        (tipo_concepto = 'REPUESTO'  AND repuesto_id IS NOT NULL)
    )
) ENGINE=InnoDB;
