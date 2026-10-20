package com.logirock.shipment_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder 
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class EnvioRequestDto {

    @Schema(description = "Peso del paquete en kilogramos", example = "2.5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El peso es obligatorio")
    @Positive(message = "El peso debe ser mayor que cero")
    private Double peso;

    @Schema(description = "Ancho del paquete en centímetros", example = "10.0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ancho es obligatorio")
    @Positive(message = "El ancho debe ser mayor que cero")
    private Double ancho;

    @Schema(description = "Alto del paquete en centímetros", example = "20.0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El alto es obligatorio")
    @Positive(message = "El alto debe ser mayor que cero")
    private Double alto;

    @Schema(description = "Largo del paquete en centímetros", example = "30.0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El largo es obligatorio")
    @Positive(message = "El largo debe ser mayor que cero")
    private Double largo;

}
