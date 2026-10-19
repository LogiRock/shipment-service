package com.logirock.shipment_service.models;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Value Object que representa una dirección física en el dominio del sistema.
 * Al ser Embeddable, sus atributos se integrarán en la tabla de la entidad que la contenga.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Direccion {
    /**
     * Nombre de la calle de la dirección.
     */
    @NotBlank(message = "La calle es obligatoria")
    private String calle;

    /**
     * Número o altura de la calle.
     */
    @NotBlank(message = "El número es obligatorio")
    private String numero;

    /**
     * Ciudad donde se ubica la dirección.
     */
    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;

    /**
     * Código postal correspondiente a la ciudad o zona.
     */
    @NotBlank(message = "El código postal es obligatorio")
    private String codigoPostal;
}