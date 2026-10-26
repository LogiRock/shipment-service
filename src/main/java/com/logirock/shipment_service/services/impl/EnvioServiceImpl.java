package com.logirock.shipment_service.services.impl;

import com.logirock.shipment_service.dto.CourierDto;
import com.logirock.shipment_service.dto.EnvioResponseDto;
import com.logirock.shipment_service.enums.EstadoEnvio;
import com.logirock.shipment_service.models.Envio;
import com.logirock.shipment_service.repositories.EnvioRepository;
import com.logirock.shipment_service.models.Cliente;
import com.logirock.shipment_service.models.HistorialEstadoEnvio;
import com.logirock.shipment_service.repositories.HistorialEstadoEnvioRepository;
import com.logirock.shipment_service.services.ClienteService;

import com.logirock.shipment_service.services.EnvioService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
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

    private final RestTemplate restTemplate;
    @Value("${courier.service.url:http://localhost:8082}")
    private String courierUrl = "http://localhost:8082";

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
    public Envio asignarCourier(UUID envioId, UUID courierId) {
        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró ningún envío con el ID: " + envioId));

        if (courierId == null) {
            throw new IllegalArgumentException("El ID del courier es obligatorio.");
        }
        if (envio.getEstado() != EstadoEnvio.PENDIENTE) {
            throw new IllegalArgumentException("Solo se puede asignar un courier a un envío PENDIENTE.");
        }
        if (envio.getCourierId() != null && !envio.getCourierId().equals(courierId)) {
            throw new IllegalArgumentException("El envío ya tiene asignado otro courier.");
        }

        try {
            String url = courierUrl + "/api/v1/repartidores/" + courierId;
            CourierDto courier = restTemplate.getForObject(url, CourierDto.class);

            if (courier == null || !courierId.equals(courier.getId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El repartidor no existe o la respuesta de courier-service no es válida.");
            }
            if (!"ACTIVO".equalsIgnoreCase(courier.getEstado())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El repartidor está INACTIVO; no se puede asignar el envío.");
            }
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El repartidor no existe en courier-service");
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo conectar con courier-service; no se asignó el envío.");
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "courier-service rechazó la consulta del repartidor.");
        }

        envio.setCourierId(courierId);
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
                envio.getCourierId(),

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
                        || (nuevoEstado == EstadoEnvio.EN_RUTA && envio.getCourierId() != null)))
                || (estadoActual == EstadoEnvio.EN_RUTA
                        && nuevoEstado == EstadoEnvio.ENTREGADO);

        if (!transicionPermitida) {
            throw new ResponseStatusException(
                    CONFLICT,
                    estadoActual == EstadoEnvio.PENDIENTE
                            && nuevoEstado == EstadoEnvio.EN_RUTA
                            && envio.getCourierId() == null
                                    ? "No se puede poner el envío EN_RUTA sin asignarle un courier."
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
