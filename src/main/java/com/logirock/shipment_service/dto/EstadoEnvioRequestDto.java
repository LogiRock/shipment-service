package com.logirock.shipment_service.dto;

import com.logirock.shipment_service.enums.EstadoEnvio;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EstadoEnvioRequestDto {

    @Schema(description = "Nuevo estado solicitado. Solo se admiten las transiciones del ciclo de vida del envío.", example = "EN_RUTA", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El estado es obligatorio")
    private EstadoEnvio estado;
}
