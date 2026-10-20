package com.logirock.shipment_service.services.impl;

import com.logirock.shipment_service.dto.EnvioResponseDto;
import com.logirock.shipment_service.enums.EstadoEnvio;
import com.logirock.shipment_service.models.Envio;
import com.logirock.shipment_service.models.HistorialEstadoEnvio;
import com.logirock.shipment_service.repositories.EnvioRepository;
import com.logirock.shipment_service.repositories.HistorialEstadoEnvioRepository;
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
    private final HistorialEstadoEnvioRepository historialRepository;

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

    private EnvioResponseDto mapearAResponseDto(Envio envio) {
        return new EnvioResponseDto(
                envio.getId(),
                envio.getCodigoSeguimiento(),
                envio.getPeso(),
                envio.getAncho(),
                envio.getAlto(),
                envio.getLargo(),
                envio.getEstado());
    }

    @Override
    public Envio cambiarEstado(UUID id, EstadoEnvio nuevoEstado) {
        Envio envio = buscarPorId(id);

        if (nuevoEstado == null) {
            throw new ResponseStatusException(
                    BAD_REQUEST, "El nuevo estado es obligatorio");
        }

        EstadoEnvio estadoActual = envio.getEstado();

        boolean transicionPermitida = (estadoActual == EstadoEnvio.PENDIENTE
                && (nuevoEstado == EstadoEnvio.EN_RUTA
                        || nuevoEstado == EstadoEnvio.CANCELADO))
                || (estadoActual == EstadoEnvio.EN_RUTA
                        && nuevoEstado == EstadoEnvio.ENTREGADO);

        if (!transicionPermitida) {
            throw new ResponseStatusException(
                    CONFLICT,
                    "Transición de estado no permitida: "
                            + estadoActual + " -> " + nuevoEstado);
        }

        HistorialEstadoEnvio historial = new HistorialEstadoEnvio();
        historial.setEnvio(envio);
        historial.setEstadoAnterior(estadoActual);
        historial.setEstadoNuevo(nuevoEstado);

        historialRepository.save(historial);

        envio.setEstado(nuevoEstado);

        return envioRepository.save(envio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialEstadoEnvio> consultarHistorial(UUID envioId) {
        buscarPorId(envioId);

        return historialRepository
                .findByEnvio_IdOrderByFechaCambioDesc(envioId);
    }

}