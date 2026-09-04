-- ============================================================
-- ms-pagos | Esquema inicial (PostgreSQL)
-- ANDAMIAJE: columnas orientativas, ajustar en la fase de diseno.
-- ============================================================

-- CREATE DATABASE condominio_pagos;
-- \c condominio_pagos

-- ------------------------------------------------------------
-- Tabla: cuotas  (cuotas de mantenimiento)
-- unidad_id / residente_id son referencias LOGICAS a ms-residentes:
-- se validan por HTTP, no por clave foranea.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cuotas (
    id             BIGSERIAL PRIMARY KEY,
    unidad_id      BIGINT        NOT NULL,
    residente_id   BIGINT,
    periodo        CHAR(7)       NOT NULL,   -- 'YYYY-MM'
    concepto       VARCHAR(160)  NOT NULL,
    monto          NUMERIC(10,2) NOT NULL CHECK (monto >= 0),
    fecha_emision  DATE          NOT NULL DEFAULT CURRENT_DATE,
    fecha_vencim   DATE          NOT NULL,
    estado         VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',
    creado_en      TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_cuotas_unidad_periodo UNIQUE (unidad_id, periodo, concepto),
    CONSTRAINT ck_cuotas_estado
        CHECK (estado IN ('PENDIENTE', 'PARCIAL', 'PAGADA', 'VENCIDA', 'ANULADA'))
);

-- ------------------------------------------------------------
-- Tabla: pagos  (relacionada con cuotas)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pagos (
    id             BIGSERIAL PRIMARY KEY,
    cuota_id       BIGINT        NOT NULL,
    monto_pagado   NUMERIC(10,2) NOT NULL CHECK (monto_pagado > 0),
    fecha_pago     TIMESTAMP     NOT NULL DEFAULT NOW(),
    medio_pago     VARCHAR(30)   NOT NULL DEFAULT 'TRANSFERENCIA',
    referencia     VARCHAR(80),
    creado_en      TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_pagos_cuota
        FOREIGN KEY (cuota_id) REFERENCES cuotas(id),
    CONSTRAINT ck_pagos_medio
        CHECK (medio_pago IN ('EFECTIVO', 'TRANSFERENCIA', 'TARJETA', 'YAPE', 'PLIN'))
);

CREATE INDEX IF NOT EXISTS idx_cuotas_unidad  ON cuotas(unidad_id);
CREATE INDEX IF NOT EXISTS idx_cuotas_estado  ON cuotas(estado);
CREATE INDEX IF NOT EXISTS idx_pagos_cuota    ON pagos(cuota_id);
CREATE INDEX IF NOT EXISTS idx_pagos_fecha    ON pagos(fecha_pago);
