package com.logirock.shipment_service.services;

import java.util.UUID;
import com.logirock.shipment_service.dto.ClienteRequestDto;
import com.logirock.shipment_service.dto.ClienteResponseDto;

public interface ClienteService {
    ClienteResponseDto registrarCliente(ClienteRequestDto request);
    ClienteResponseDto obtenerCliente(UUID id);
    ClienteResponseDto modificarCliente(UUID id, ClienteRequestDto request);
    void eliminarCliente(UUID id);
}