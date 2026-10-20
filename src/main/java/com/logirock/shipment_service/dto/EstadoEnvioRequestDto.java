package com.logirock.shipment_service.dto;

import com.logirock.shipment_service.enums.EstadoEnvio;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EstadoEnvioRequestDto {

    @NotNull(message = "El estado es obligatorio")
    private EstadoEnvio estado;
}
