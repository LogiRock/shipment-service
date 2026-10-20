package com.logirock.shipment_service.models;

import java.util.UUID;

import com.logirock.shipment_service.enums.EstadoEnvio;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table (name = "envios")
@Getter 
@Setter 
@NoArgsConstructor 
public class Envio extends Auditable{

    @Id 
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank (message = "El código de seguimiento es obligatorio")
    @Column (name = "codigo_seguimiento", nullable = false, unique = true, length = 20)
    private String codigoSeguimiento;

    @NotNull(message = "El peso es obligatorio")
    @Positive (message = "El peso debe ser mayor que cero")
    @Column(nullable = false)
    private Double peso;

    @NotNull(message = "El ancho es obligatorio")
    @Positive(message = "El ancho debe ser mayor que cero")
    @Column(nullable = false)
    private Double ancho;

    @NotNull(message = "El alto es obligatorio")
    @Positive(message = "El alto debe ser mayor que cero")
    @Column(nullable = false)
    private Double alto;

    @NotNull(message = "El largo es obligatorio")
    @Positive(message = "El largo debe ser mayor que cero")
    @Column(nullable = false)
    private Double largo;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEnvio estado;

    /**
     * Dirección de origen desde donde parte el envío.
     * Se sobreescriben los nombres de las columnas con el prefijo origen_.
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "calle", column = @Column(name = "origen_calle", nullable = false)),
            @AttributeOverride(name = "numero", column = @Column(name = "origen_numero", nullable = false)),
            @AttributeOverride(name = "ciudad", column = @Column(name = "origen_ciudad", nullable = false)),
            @AttributeOverride(name = "codigoPostal", column = @Column(name = "origen_codigo_postal", nullable = false))
    })
    private Direccion origen;

    /**
     * Dirección de destino hacia donde se dirige el envío.
     * Se sobreescriben los nombres de las columnas con el prefijo destino_.
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "calle", column = @Column(name = "destino_calle", nullable = false)),
            @AttributeOverride(name = "numero", column = @Column(name = "destino_numero", nullable = false)),
            @AttributeOverride(name = "ciudad", column = @Column(name = "destino_ciudad", nullable = false)),
            @AttributeOverride(name = "codigoPostal", column = @Column(name = "destino_codigo_postal", nullable = false))
    })
    private Direccion destino;

    @PrePersist
    private void inicializarEstado() {
        if (estado == null) {
            estado = EstadoEnvio.PENDIENTE;
        }
    }
}
