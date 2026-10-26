package com.logirock.shipment_service.dto;

import java.util.UUID;

public class CourierDto {
    private UUID id;
    private String estado;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
