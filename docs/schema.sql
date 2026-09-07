-- ============================================================
-- ms-pagos | Esquema inicial (MySQL 8)
-- ANDAMIAJE: columnas orientativas, ajustar en la fase de diseno.
-- ============================================================

CREATE DATABASE IF NOT EXISTS condominio_pagos
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE condominio_pagos;

-- ------------------------------------------------------------
-- Tabla: cuotas  (cuotas de mantenimiento)
-- unidad_id / residente_id son referencias LOGICAS a ms-residentes:
-- no hay clave foranea ni llamada HTTP a ese microservicio.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cuotas (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    unidad_id      BIGINT        NOT NULL,
    residente_id   BIGINT,
    periodo        CHAR(7)       NOT NULL,   -- 'YYYY-MM'
    concepto       VARCHAR(160)  NOT NULL,
    monto          DECIMAL(10,2) NOT NULL,
    fecha_emision  DATE          NOT NULL,
    fecha_vencim   DATE          NOT NULL,
    estado         ENUM('PENDIENTE','PARCIAL','PAGADA','VENCIDA','ANULADA')
                   NOT NULL DEFAULT 'PENDIENTE',
    creado_en      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_cuotas_unidad_periodo UNIQUE (unidad_id, periodo, concepto),
    CONSTRAINT ck_cuotas_monto CHECK (monto >= 0)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Tabla: pagos  (relacionada con cuotas)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pagos (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    cuota_id       BIGINT        NOT NULL,
    monto_pagado   DECIMAL(10,2) NOT NULL,
    fecha_pago     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    medio_pago     ENUM('EFECTIVO','TRANSFERENCIA','TARJETA','YAPE','PLIN')
                   NOT NULL DEFAULT 'TRANSFERENCIA',
    referencia     VARCHAR(80),
    creado_en      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pagos_cuota
        FOREIGN KEY (cuota_id) REFERENCES cuotas(id),
    CONSTRAINT ck_pagos_monto CHECK (monto_pagado > 0)
) ENGINE=InnoDB;

CREATE INDEX idx_cuotas_unidad ON cuotas(unidad_id);
CREATE INDEX idx_cuotas_estado ON cuotas(estado);
CREATE INDEX idx_pagos_cuota   ON pagos(cuota_id);
CREATE INDEX idx_pagos_fecha   ON pagos(fecha_pago);
