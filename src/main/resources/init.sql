CREATE DATABASE IF NOT EXISTS logitrack_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE logitrack_db;

CREATE TABLE IF NOT EXISTS clientes (
    id BINARY(16) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,

    fecha_creacion DATETIME(6),
    fecha_ultima_actualizacion DATETIME(6),

    PRIMARY KEY (id),
    UNIQUE KEY uk_clientes_email (email)
);

CREATE TABLE IF NOT EXISTS envios (
    id BINARY(16) NOT NULL,
    codigo_seguimiento VARCHAR(20) NOT NULL,

    peso DOUBLE NOT NULL,
    ancho DOUBLE NOT NULL,
    alto DOUBLE NOT NULL,
    largo DOUBLE NOT NULL,

    estado VARCHAR(20) NOT NULL,

    -- Dirección de origen
    origen_calle VARCHAR(255) NOT NULL,
    origen_numero VARCHAR(255) NOT NULL,
    origen_ciudad VARCHAR(255) NOT NULL,
    origen_codigo_postal VARCHAR(255) NOT NULL,

    -- Dirección de destino
    destino_calle VARCHAR(255) NOT NULL,
    destino_numero VARCHAR(255) NOT NULL,
    destino_ciudad VARCHAR(255) NOT NULL,
    destino_codigo_postal VARCHAR(255) NOT NULL,

    cliente_id BINARY(16) NOT NULL,
    courier_id BIGINT,

    fecha_creacion DATETIME(6),
    fecha_ultima_actualizacion DATETIME(6),

    PRIMARY KEY (id),

    UNIQUE KEY uk_envios_codigo_seguimiento (codigo_seguimiento),

    CONSTRAINT fk_envios_clientes
        FOREIGN KEY (cliente_id)
        REFERENCES clientes(id)
);

-- Migra instalaciones existentes que todavía tenían la relación local con repartidores.
-- Las asignaciones UUID anteriores se eliminan: deben reasignarse usando IDs de courier-service.
SET @drop_repartidores_fk = (
    SELECT IF(COUNT(*) > 0,
        'ALTER TABLE envios DROP FOREIGN KEY fk_envios_repartidores',
        'SELECT 1')
    FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'envios'
      AND CONSTRAINT_NAME = 'fk_envios_repartidores'
);
PREPARE migration_stmt FROM @drop_repartidores_fk;
EXECUTE migration_stmt;
DEALLOCATE PREPARE migration_stmt;

SET @add_courier_id = (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE envios ADD COLUMN courier_id BIGINT NULL',
        'SELECT 1')
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'envios'
      AND COLUMN_NAME = 'courier_id'
);
PREPARE migration_stmt FROM @add_courier_id;
EXECUTE migration_stmt;
DEALLOCATE PREPARE migration_stmt;

SET @drop_repartidor_id = (
    SELECT IF(COUNT(*) > 0,
        'ALTER TABLE envios DROP COLUMN repartidor_id',
        'SELECT 1')
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'envios'
      AND COLUMN_NAME = 'repartidor_id'
);
PREPARE migration_stmt FROM @drop_repartidor_id;
EXECUTE migration_stmt;
DEALLOCATE PREPARE migration_stmt;

DROP TABLE IF EXISTS repartidores;

CREATE TABLE IF NOT EXISTS historial_estados_envio (
    id BINARY(16) NOT NULL,
    envio_id BINARY(16) NOT NULL,

    estado_anterior VARCHAR(20) NOT NULL,
    estado_nuevo VARCHAR(20) NOT NULL,

    fecha_cambio DATETIME(6) NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_historial_envio
        FOREIGN KEY (envio_id)
        REFERENCES envios(id)
);

INSERT INTO clientes (
    id,
    nombre,
    email,
    telefono,
    fecha_creacion,
    fecha_ultima_actualizacion
) VALUES (
    UUID_TO_BIN('11111111-1111-4111-8111-111111111111'),
    'Juan Perez',
    'juan.perez@email.com',
    '3884123456',
    NOW(6),
    NOW(6)
)
ON DUPLICATE KEY UPDATE id = id;


INSERT INTO envios (
    id,
    codigo_seguimiento,
    peso,
    ancho,
    alto,
    largo,
    estado,
    origen_calle,
    origen_numero,
    origen_ciudad,
    origen_codigo_postal,
    destino_calle,
    destino_numero,
    destino_ciudad,
    destino_codigo_postal,
    cliente_id,
    courier_id,
    fecha_creacion,
    fecha_ultima_actualizacion
) VALUES (
    UUID_TO_BIN('33333333-3333-4333-8333-333333333333'),
    'ENV-000001',
    2.5,
    20.0,
    15.0,
    30.0,
    'EN_RUTA',
    'San Martin',
    '123',
    'San Salvador de Jujuy',
    '4600',
    'Belgrano',
    '456',
    'Palpala',
    '4612',
    UUID_TO_BIN('11111111-1111-4111-8111-111111111111'),
    1,
    NOW(6),
    NOW(6)
)
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO historial_estados_envio (
    id,
    envio_id,
    estado_anterior,
    estado_nuevo,
    fecha_cambio
) VALUES (
    UUID_TO_BIN('44444444-4444-4444-8444-444444444444'),
    UUID_TO_BIN('33333333-3333-4333-8333-333333333333'),
    'PENDIENTE',
    'EN_RUTA',
    NOW(6)
)
ON DUPLICATE KEY UPDATE id = id;
