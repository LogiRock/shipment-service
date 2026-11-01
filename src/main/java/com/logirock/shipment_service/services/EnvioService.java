package com.logirock.shipment_service.services;

import com.logirock.shipment_service.dto.EnvioRequestDto;
import com.logirock.shipment_service.dto.EnvioResponseDto;
import com.logirock.shipment_service.dto.HistorialEstadoEnvioResponseDto;
import com.logirock.shipment_service.enums.EstadoEnvio;

import java.util.List;
import java.util.UUID;

public interface EnvioService {

    EnvioResponseDto crear(EnvioRequestDto peticion, UUID userId);

    List<EnvioResponseDto> listarTodos();

    List<EnvioResponseDto> findByEstado(EstadoEnvio estado);

    EnvioResponseDto buscarPorId(UUID id);

    EnvioResponseDto buscarPorCodigoSeguimiento(String codigoSeguimiento);

    EnvioResponseDto actualizar(UUID id, EnvioRequestDto datos);

    void eliminar(UUID id);

    EnvioResponseDto cancelarEnvio(UUID id);

    EnvioResponseDto asignarCourier(UUID envioId, UUID courierId, String token);

    EnvioResponseDto cambiarEstado(UUID id, EstadoEnvio nuevoEstado);

    List<HistorialEstadoEnvioResponseDto> consultarHistorial(UUID envioId);

    List<EnvioResponseDto> listarPorUsuario(UUID userId);

    EnvioResponseDto buscarPorIdParaUsuario(UUID id, UUID userId);

    EnvioResponseDto buscarPorCodigoParaUsuario(String codigo, UUID userId);

    List<EnvioResponseDto> listarPorRepartidor(String token);

    EnvioResponseDto cambiarEstadoParaRepartidor(UUID envioId, EstadoEnvio nuevoEstado, String token);
}
