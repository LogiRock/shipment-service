package com.logirock.shipment_service.dto;

import java.util.UUID;

public record UsuarioResponseDto(
        UUID id,
        String username,
        String rol
) {
}