package com.logirock.shipment_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class ClienteRequestDto {

    @Schema(description = "Nombre completo del cliente", example = "Ana Pérez", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "Correo electrónico único del cliente", example = "ana@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;

    @Schema(description = "Teléfono de contacto", example = "+5491100000000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;
}
