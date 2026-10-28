package com.logirock.shipment_service.dto;

public record RegistroUsuarioRequest(
        String username,
        String password
) {
}