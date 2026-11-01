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
    courier_id VARCHAR(36),

    fecha_creacion DATETIME(6),
    fecha_ultima_actualizacion DATETIME(6),

    PRIMARY KEY (id),

    UNIQUE KEY uk_envios_codigo_seguimiento (codigo_seguimiento),

    CONSTRAINT fk_envios_clientes
        FOREIGN KEY (cliente_id)
        REFERENCES clientes(id)
);

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

