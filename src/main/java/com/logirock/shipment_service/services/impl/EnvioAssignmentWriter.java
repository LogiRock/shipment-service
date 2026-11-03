package com.logirock.shipment_service.services.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logirock.shipment_service.dto.EnvioMapper;
import com.logirock.shipment_service.dto.EnvioResponseDto;
import com.logirock.shipment_service.exceptions.EnvioNoEncontradoException;
import com.logirock.shipment_service.models.Envio;
import com.logirock.shipment_service.repositories.EnvioRepository;

import lombok.RequiredArgsConstructor;

/** Persiste la asignación dentro de una transacción corta, después de validar Courier Service. */
@Service
@RequiredArgsConstructor
public class EnvioAssignmentWriter {

    private final EnvioRepository envioRepository;
    private final EnvioMapper envioMapper;

    @Transactional
    public EnvioResponseDto guardarAsignacion(UUID envioId, UUID courierId) {
        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new EnvioNoEncontradoException("No se encontró el envío con ID: " + envioId));
        envio.setCourierId(courierId);
        return envioMapper.toDto(envioRepository.save(envio));
    }
}
