package com.logirock.shipment_service.models;

import java.util.UUID;

import com.logirock.shipment_service.enums.EstadoRepartidor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa a un repartidor en el sistema y mapea con la tabla de repartidores.
 * Extiende de Auditable para el registro automático de fechas de creación y modificación.
 */
@Entity
@Table(name = "repartidores")
@Getter
@Setter
@NoArgsConstructor
public class Repartidor extends Auditable {

    /**
     * Identificador único autogenerado para el repartidor.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Nombre completo del repartidor.
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombre;

    /**
     * Descripción del vehículo que utiliza el repartidor.
     */
    @NotBlank(message = "El tipo de vehículo es obligatorio")
    @Column(name = "tipo_vehiculo", nullable = false, length = 50)
    private String tipoVehiculo;

    /**
     * Estado actual del repartidor, almacenado como cadena de texto en la base de datos.
     */
    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoRepartidor estado;
}