package com.logirock.shipment_service.services;

import com.logirock.shipment_service.dto.EnvioResponseDto;
import com.logirock.shipment_service.enums.EstadoEnvio;
import com.logirock.shipment_service.models.Envio;
import com.logirock.shipment_service.models.HistorialEstadoEnvio;

import java.util.List;
import java.util.UUID;

public interface EnvioService {

    Envio crear(Envio envio, UUID clienteId);

    List<EnvioResponseDto> listarTodos();

    List<Envio> findByEstado(EstadoEnvio estado);

    Envio buscarPorId(UUID id);

    Envio buscarPorCodigoSeguimiento(String codigoSeguimiento);

    Envio actualizar(UUID id, Envio datos);

    void eliminar(UUID id);

    EnvioResponseDto cancelarEnvio(UUID id);

    Envio asignarRepartidor(UUID envioId, UUID repartidorId);
    Envio cambiarEstado(UUID id, EstadoEnvio nuevoEstado);

    List<HistorialEstadoEnvio> consultarHistorial(UUID envioId);
}
