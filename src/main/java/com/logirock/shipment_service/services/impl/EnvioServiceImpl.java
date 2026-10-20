package com.logirock.shipment_service.services.impl;

import com.logirock.shipment_service.dto.EnvioResponseDto;
import com.logirock.shipment_service.enums.EstadoEnvio;
import com.logirock.shipment_service.enums.EstadoRepartidor;
import com.logirock.shipment_service.models.Envio;
import com.logirock.shipment_service.models.Repartidor;
import com.logirock.shipment_service.repositories.EnvioRepository;
import com.logirock.shipment_service.repositories.RepartidorRepository;
import com.logirock.shipment_service.models.Cliente;
import com.logirock.shipment_service.models.HistorialEstadoEnvio;
import com.logirock.shipment_service.repositories.HistorialEstadoEnvioRepository;
import com.logirock.shipment_service.services.ClienteService;

import com.logirock.shipment_service.services.EnvioService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
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
    private final ClienteService clienteService;

    private final RepartidorRepository repartidorRepository;

    @Override
    public Envio crear(Envio envio, UUID clienteId) {

        Cliente cliente = buscarCliente(clienteId);

        envio.setCliente(cliente);
        envio.setEstado(EstadoEnvio.PENDIENTE);
        envio.setCodigoSeguimiento(generarCodigoSeguimiento());

        Envio envioGuardado = envioRepository.save(envio);

        return envioGuardado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnvioResponseDto> listarTodos() {
        return envioRepository.findAll()
                .stream()
                .map(this::mapearAResponseDto)
                .toList();
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

        if (datos.getCliente() != null
                && datos.getCliente().getId() != null) {

            Cliente cliente = buscarCliente(datos.getCliente().getId());
            envio.setCliente(cliente);
        }

        envio.setPeso(datos.getPeso());
        envio.setAncho(datos.getAncho());
        envio.setAlto(datos.getAlto());
        envio.setLargo(datos.getLargo());
        envio.setOrigen(datos.getOrigen());
        envio.setDestino(datos.getDestino());

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
        return envioRepository.findByEstado(estado);
    }

    @Override
    public EnvioResponseDto cancelarEnvio(UUID id) {
        return mapearAResponseDto(cambiarEstado(id, EstadoEnvio.CANCELADO));
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
                envio.getRepartidor() == null ? null : envio.getRepartidor().getId(),

                envio.getCliente().getId(),
                envio.getCliente().getNombre(),
                envio.getOrigen(),
                envio.getDestino());
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
                && (nuevoEstado == EstadoEnvio.CANCELADO
                        || (nuevoEstado == EstadoEnvio.EN_RUTA && envio.getRepartidor() != null)))
                || (estadoActual == EstadoEnvio.EN_RUTA
                        && nuevoEstado == EstadoEnvio.ENTREGADO);

        if (!transicionPermitida) {
            throw new ResponseStatusException(
                    CONFLICT,
                    estadoActual == EstadoEnvio.PENDIENTE
                            && nuevoEstado == EstadoEnvio.EN_RUTA
                            && envio.getRepartidor() == null
                                    ? "No se puede poner el envío EN_RUTA sin asignarle un repartidor."
                                    : "Transición de estado no permitida: " + estadoActual + " -> " + nuevoEstado);
        }

        registrarCambioEstado(envio, nuevoEstado);

        return envioRepository.save(envio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialEstadoEnvio> consultarHistorial(UUID envioId) {
        buscarPorId(envioId);

        return historialRepository
                .findByEnvio_IdOrderByFechaCambioDesc(envioId);
    }

    private Cliente buscarCliente(UUID clienteId) {
        if (clienteId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El ID del cliente es obligatorio.");
        }

        try {
            return clienteService.buscarEntidadPorId(clienteId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No existe un cliente con el ID: " + clienteId);
        }

    }

    private void registrarCambioEstado(
            Envio envio,
            EstadoEnvio nuevoEstado) {

        HistorialEstadoEnvio historial = new HistorialEstadoEnvio();

        historial.setEnvio(envio);
        historial.setEstadoAnterior(envio.getEstado());
        historial.setEstadoNuevo(nuevoEstado);

        historialRepository.save(historial);
        envio.setEstado(nuevoEstado);
    }

    private String generarCodigoSeguimiento() {

        String codigo;

        do {
            codigo = "ENV-" + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();
        } while (envioRepository.existsByCodigoSeguimiento(codigo));

        return codigo;
    }

}
