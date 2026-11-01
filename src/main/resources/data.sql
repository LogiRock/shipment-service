INSERT INTO clientes (
    id, nombre, email, telefono, fecha_creacion, fecha_ultima_actualizacion
) VALUES (
             UUID_TO_BIN('11111111-1111-4111-8111-111111111111'), 'Juan Perez', 'juan.perez@email.com', '3884123456', NOW(6), NOW(6)
         ) ON DUPLICATE KEY UPDATE id = id;

INSERT INTO envios (
    id, codigo_seguimiento, peso, ancho, alto, largo, estado,
    origen_calle, origen_numero, origen_ciudad, origen_codigo_postal,
    destino_calle, destino_numero, destino_ciudad, destino_codigo_postal,
    cliente_id, courier_id, fecha_creacion, fecha_ultima_actualizacion
) VALUES (
             UUID_TO_BIN('33333333-3333-4333-8333-333333333333'), 'ENV-000001', 2.5, 20.0, 15.0, 30.0, 'EN_RUTA',
             'San Martin', '123', 'San Salvador de Jujuy', '4600',
             'Belgrano', '456', 'Palpala', '4612',
             UUID_TO_BIN('11111111-1111-4111-8111-111111111111'), NULL, NOW(6), NOW(6)
         ) ON DUPLICATE KEY UPDATE id = id;

INSERT INTO historial_estados_envio (
    id, envio_id, estado_anterior, estado_nuevo, fecha_cambio
) VALUES (
             UUID_TO_BIN('44444444-4444-4444-8444-444444444444'), UUID_TO_BIN('33333333-3333-4333-8333-333333333333'), 'PENDIENTE', 'EN_RUTA', NOW(6)
         ) ON DUPLICATE KEY UPDATE id = id;