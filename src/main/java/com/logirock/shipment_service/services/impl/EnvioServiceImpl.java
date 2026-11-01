package com.logirock.shipment_service.services.impl;

import com.logirock.shipment_service.dto.CourierDto;
import com.logirock.shipment_service.dto.EnvioMapper;
import com.logirock.shipment_service.dto.EnvioRequestDto;
import com.logirock.shipment_service.dto.EnvioResponseDto;
import com.logirock.shipment_service.dto.HistorialEstadoEnvioResponseDto;
import com.logirock.shipment_service.enums.EstadoEnvio;
import com.logirock.shipment_service.exceptions.EnvioNoEncontradoException;
import com.logirock.shipment_service.exceptions.TransicionInvalidaException;
import com.logirock.shipment_service.models.Envio;
import com.logirock.shipment_service.models.Cliente;
import com.logirock.shipment_service.models.HistorialEstadoEnvio;
import com.logirock.shipment_service.repositories.EnvioRepository;
import com.logirock.shipment_service.repositories.HistorialEstadoEnvioRepository;
import com.logirock.shipment_service.services.ClienteService;
import com.logirock.shipment_service.services.EnvioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class EnvioServiceImpl implements EnvioService {

    public static final String PREFIJO_SEGUIMIENTO = "ENV-";

    private final EnvioRepository envioRepository;
    private final HistorialEstadoEnvioRepository historialRepository;
    private final ClienteService clienteService;
    private final EnvioMapper envioMapper;
    private final RestTemplate restTemplate;

    @Value("${courier.service.url:http://localhost:8082}")
    private String courierUrl;

    @Override
    public EnvioResponseDto crear(EnvioRequestDto peticion, UUID userId) {
        Cliente cliente = clienteService.buscarPorUserId(userId);

        Envio envio = new Envio();
        envio.setCliente(cliente);
        envio.setEstado(EstadoEnvio.PENDIENTE);
        envio.setCodigoSeguimiento(generarCodigoSeguimiento());
        envio.setPeso(peticion.getPeso());
        envio.setAncho(peticion.getAncho());
        envio.setAlto(peticion.getAlto());
        envio.setLargo(peticion.getLargo());
        envio.setOrigen(peticion.getOrigen());
        envio.setDestino(peticion.getDestino());

        return envioMapper.toDto(envioRepository.save(envio));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnvioResponseDto> listarTodos() {
        return envioRepository.findAll()
                .stream()
                .map(envioMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EnvioResponseDto buscarPorId(UUID id) {
        return envioMapper.toDto(obtenerEntidadPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public EnvioResponseDto buscarPorCodigoSeguimiento(String codigoSeguimiento) {
        return envioMapper.toDto(obtenerEntidadPorCodigo(codigoSeguimiento));
    }

    @Override
    public EnvioResponseDto actualizar(UUID id, EnvioRequestDto datos) {
        Envio envio = obtenerEntidadPorId(id);

        envio.setPeso(datos.getPeso());
        envio.setAncho(datos.getAncho());
        envio.setAlto(datos.getAlto());
        envio.setLargo(datos.getLargo());
        envio.setOrigen(datos.getOrigen());
        envio.setDestino(datos.getDestino());

        return envioMapper.toDto(envioRepository.save(envio));
    }

    @Override
    public void eliminar(UUID id) {
        Envio envio = obtenerEntidadPorId(id);
        envioRepository.delete(envio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnvioResponseDto> findByEstado(EstadoEnvio estado) {
        return envioRepository.findByEstado(estado)
                .stream()
                .map(envioMapper::toDto)
                .toList();
    }

    @Override
    public EnvioResponseDto cancelarEnvio(UUID id) {
        return cambiarEstado(id, EstadoEnvio.CANCELADO);
    }

    @Override
    public EnvioResponseDto asignarCourier(UUID envioId, UUID courierId, String token) {
        Envio envio = obtenerEntidadPorId(envioId);

        if (courierId == null) {
            throw new IllegalArgumentException("El ID del courier es obligatorio.");
        }
        if (envio.getEstado() != EstadoEnvio.PENDIENTE) {
            throw new TransicionInvalidaException("Solo se puede asignar un courier a un envío PENDIENTE.");
        }
        if (envio.getCourierId() != null && !envio.getCourierId().equals(courierId)) {
            throw new TransicionInvalidaException("El envío ya tiene asignado otro courier.");
        }

        verificarCourier(courierId, token);

        envio.setCourierId(courierId);
        return envioMapper.toDto(envioRepository.save(envio));
    }

    @Override
    public EnvioResponseDto cambiarEstado(UUID id, EstadoEnvio nuevoEstado) {
        Envio envio = obtenerEntidadPorId(id);

        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado es obligatorio");
        }

        EstadoEnvio estadoActual = envio.getEstado();

        boolean transicionPermitida = (estadoActual == EstadoEnvio.PENDIENTE
                && (nuevoEstado == EstadoEnvio.CANCELADO
                || (nuevoEstado == EstadoEnvio.EN_RUTA && envio.getCourierId() != null)))
                || (estadoActual == EstadoEnvio.EN_RUTA
                && nuevoEstado == EstadoEnvio.ENTREGADO);

        if (!transicionPermitida) {
            String motivo = (estadoActual == EstadoEnvio.PENDIENTE && nuevoEstado == EstadoEnvio.EN_RUTA && envio.getCourierId() == null)
                    ? "No se puede poner el envío EN_RUTA sin asignarle un courier."
                    : "Transición de estado no permitida: " + estadoActual + " -> " + nuevoEstado;
            throw new TransicionInvalidaException(motivo);
        }

        registrarCambioEstado(envio, nuevoEstado);
        return envioMapper.toDto(envioRepository.save(envio));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialEstadoEnvioResponseDto> consultarHistorial(UUID envioId) {
        obtenerEntidadPorId(envioId);
        return historialRepository.findByEnvio_IdOrderByFechaCambioDesc(envioId)
                .stream()
                .map(historial -> new HistorialEstadoEnvioResponseDto(
                        historial.getId(),
                        historial.getEstadoAnterior(),
                        historial.getEstadoNuevo(),
                        historial.getFechaCambio()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnvioResponseDto> listarPorUsuario(UUID userId) {
        Cliente cliente = clienteService.buscarPorUserId(userId);
        return envioRepository.findByCliente_Id(cliente.getId())
                .stream()
                .map(envioMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EnvioResponseDto buscarPorIdParaUsuario(UUID id, UUID userId) {
        Envio envio = obtenerEntidadPorId(id);
        verificarPropietario(envio, userId);
        return envioMapper.toDto(envio);
    }

    @Override
    @Transactional(readOnly = true)
    public EnvioResponseDto buscarPorCodigoParaUsuario(String codigo, UUID userId) {
        Envio envio = obtenerEntidadPorCodigo(codigo);
        verificarPropietario(envio, userId);
        return envioMapper.toDto(envio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnvioResponseDto> listarPorRepartidor(String token) {
        UUID courierId = obtenerCourierIdAutenticado(token);
        return envioRepository.findByCourierId(courierId)
                .stream()
                .map(envioMapper::toDto)
                .toList();
    }

    @Override
    public EnvioResponseDto cambiarEstadoParaRepartidor(UUID envioId, EstadoEnvio nuevoEstado, String token) {
        UUID courierId = obtenerCourierIdAutenticado(token);
        Envio envio = obtenerEntidadPorId(envioId);

        if (envio.getCourierId() == null || !envio.getCourierId().equals(courierId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para modificar un envío que no tenés asignado");
        }

        return cambiarEstado(envioId, nuevoEstado);
    }

    // Métodos Privados Auxiliares

    private Envio obtenerEntidadPorId(UUID id) {
        return envioRepository.findById(id)
                .orElseThrow(() -> new EnvioNoEncontradoException("No se encontró el envío con ID: " + id));
    }

    private Envio obtenerEntidadPorCodigo(String codigo) {
        return envioRepository.findByCodigoSeguimiento(codigo)
                .orElseThrow(() -> new EnvioNoEncontradoException("No se encontró el envío con código: " + codigo));
    }

    private void verificarPropietario(Envio envio, UUID userId) {
        if (envio.getCliente() == null || envio.getCliente().getUserId() == null || !envio.getCliente().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para consultar este envío");
        }
    }

    private void registrarCambioEstado(Envio envio, EstadoEnvio nuevoEstado) {
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
            codigo = PREFIJO_SEGUIMIENTO + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (envioRepository.existsByCodigoSeguimiento(codigo));
        return codigo;
    }

    private void verificarCourier(UUID courierId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<CourierDto> response = restTemplate.exchange(
                    courierUrl + "/api/v1/repartidores/" + courierId,
                    HttpMethod.GET, request, CourierDto.class);

            CourierDto courier = response.getBody();
            if (courier == null || !courierId.equals(courier.getId())) {
                throw new EnvioNoEncontradoException("El repartidor no existe o la respuesta no es válida.");
            }
            if (!"ACTIVO".equalsIgnoreCase(courier.getEstado())) {
                throw new TransicionInvalidaException("El repartidor está INACTIVO; no se puede asignar el envío.");
            }
        } catch (HttpClientErrorException.NotFound e) {
            throw new EnvioNoEncontradoException("El repartidor no existe en courier-service");
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo conectar con courier-service");
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "courier-service rechazó la consulta");
        }
    }

    private UUID obtenerCourierIdAutenticado(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<CourierDto> response = restTemplate.exchange(
                    courierUrl + "/api/v1/repartidores/me",
                    HttpMethod.GET, request, CourierDto.class);

            CourierDto courier = response.getBody();
            if (courier == null || courier.getId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo identificar al repartidor autenticado");
            }
            return courier.getId();
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo conectar con courier-service");
        }
    }
}
