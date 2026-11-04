package com.logirock.shipment_service.dto;


public record DisponibilidadRepartidorResponseDto(
        boolean disponible,
        String motivo
) {
}