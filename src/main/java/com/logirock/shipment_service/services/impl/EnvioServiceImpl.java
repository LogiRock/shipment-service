package com.logirock.shipment_service.services.impl;

import com.logirock.shipment_service.dto.EnvioResponseDto;
import com.logirock.shipment_service.enums.EstadoEnvio;
import com.logirock.shipment_service.enums.EstadoRepartidor;
import com.logirock.shipment_service.models.Envio;
import com.logirock.shipment_service.models.Repartidor;
import com.logirock.shipment_service.repositories.EnvioRepository;
import com.logirock.shipment_service.repositories.RepartidorRepository;
import com.logirock.shipment_service.services.EnvioService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class EnvioServiceImpl implements EnvioService {

    private final EnvioRepository envioRepository;
    private final RepartidorRepository repartidorRepository;

    @Override
    public Envio crear(Envio envio) {
        String codigo;

        do {
            codigo = "ENV-" + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();
        } while (envioRepository.existsByCodigoSeguimiento(codigo));

        envio.setCodigoSeguimiento(codigo);

        return envioRepository.save(envio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Envio> listarTodos() {
        return envioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Envio buscarPorId(UUID id) {
        return envioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "No se encontró el envío"));
    }

    @Override
    @Transactional(readOnly = true)
    public Envio buscarPorCodigoSeguimiento(String codigoSeguimiento) {
        return envioRepository.findByCodigoSeguimiento(codigoSeguimiento)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "No se encontró el envío"));
    }

    @Override
    public Envio actualizar(UUID id, Envio datos) {
        Envio envio = buscarPorId(id);

        envio.setPeso(datos.getPeso());
        envio.setAncho(datos.getAncho());
        envio.setAlto(datos.getAlto());
        envio.setLargo(datos.getLargo());

        return envioRepository.save(envio);
    }

    @Override
    public void eliminar(UUID id) {
        Envio envio = buscarPorId(id);
        envioRepository.delete(envio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Envio> findByEstado(EstadoEnvio estado) {
        // TODO Auto-generated method stub
        return envioRepository.findByEstado(estado);
    }

    @Override
    @Transactional
    public EnvioResponseDto cancelarEnvio(UUID id) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró ningún envío con el ID: " + id));

        if (envio.getEstado() != EstadoEnvio.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden cancelar envíos en estado PENDIENTE.");
        }

        envio.setEstado(EstadoEnvio.CANCELADO);
        Envio envioCancelado = envioRepository.save(envio);
        return mapearAResponseDto(envioCancelado);
    }

    @Override
    public Envio asignarRepartidor(UUID envioId, UUID repartidorId) {
        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró ningún envío con el ID: " + envioId));

        Repartidor repartidor = repartidorRepository.findById(repartidorId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró ningún repartidor con el ID: " + repartidorId));

        if (repartidor.getEstado() != EstadoRepartidor.ACTIVO) {
            throw new IllegalArgumentException("No se puede asignar un repartidor INACTIVO.");
        }

        if (envio.getEstado() != EstadoEnvio.PENDIENTE) {
            throw new IllegalArgumentException("Solo se puede asignar un repartidor a un envío PENDIENTE.");
        }

        if (envio.getRepartidor() != null && !envio.getRepartidor().getId().equals(repartidorId)) {
            throw new IllegalArgumentException("El envío ya tiene asignado otro repartidor.");
        }

        envio.setRepartidor(repartidor);
        return envioRepository.save(envio);
    }

    private EnvioResponseDto mapearAResponseDto(Envio envio) {
        return new EnvioResponseDto(
                envio.getId(),
                envio.getCodigoSeguimiento(),
                envio.getPeso(),
                envio.getAncho(),
                envio.getAlto(),
                envio.getLargo(),
                envio.getEstado(),
                envio.getRepartidor() == null ? null : envio.getRepartidor().getId());
    }
}
