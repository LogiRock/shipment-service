package com.logirock.shipment_service.dto;

import java.util.UUID;

import com.logirock.shipment_service.models.Direccion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotNull(message = "El peso es obligatorio")
    @Positive(message = "El peso debe ser mayor que cero")
    private Double peso;

    @NotNull(message = "El ancho es obligatorio")
    @Positive(message = "El ancho debe ser mayor que cero")
    private Double ancho;

    @NotNull(message = "El alto es obligatorio")
    @Positive(message = "El alto debe ser mayor que cero")
    private Double alto;

    @NotNull(message = "El largo es obligatorio")
    @Positive(message = "El largo debe ser mayor que cero")
    private Double largo;

    @Valid
    @NotNull(message = "La dirección de origen es obligatoria")
    private Direccion origen;

    @Valid
    @NotNull(message = "La dirección de destino es obligatoria")
    private Direccion destino;

    @NotNull(message = "El cliente es obligatorio")
    private UUID clienteId;

}
