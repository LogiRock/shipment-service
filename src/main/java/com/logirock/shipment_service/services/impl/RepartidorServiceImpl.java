package com.logirock.shipment_service.services.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logirock.shipment_service.dto.RepartidorRequestDto;
import com.logirock.shipment_service.dto.RepartidorResponseDto;
import com.logirock.shipment_service.models.Repartidor;
import com.logirock.shipment_service.repositories.RepartidorRepository;
import com.logirock.shipment_service.services.RepartidorService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación de la lógica de negocio para la administración de repartidores.
 * Utiliza transaccionalidad de Spring y el Logger de SLF4J para auditoría interna.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RepartidorServiceImpl implements RepartidorService {

    /**
     * Repositorio inyectado para la persistencia de la entidad Repartidor.
     */
    private final RepartidorRepository repartidorRepository;

    @Override
    @Transactional
    public RepartidorResponseDto registrarRepartidor(RepartidorRequestDto request) {
        log.info("Registrando nuevo repartidor con nombre: {}", request.getNombre());

        Repartidor repartidor = new Repartidor();
        repartidor.setNombre(request.getNombre());
        repartidor.setTipoVehiculo(request.getTipoVehiculo());
        repartidor.setEstado(request.getEstado());

        repartidor = repartidorRepository.save(repartidor);
        return mapearAResponseDto(repartidor);
    }

    @Override
    @Transactional(readOnly = true)
    public RepartidorResponseDto obtenerRepartidor(UUID id) {
        Repartidor repartidor = buscarRepartidorPorId(id);
        return mapearAResponseDto(repartidor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepartidorResponseDto> listarRepartidores() {
        return repartidorRepository.findAll()
                .stream()
                .map(this::mapearAResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RepartidorResponseDto modificarRepartidor(UUID id, RepartidorRequestDto request) {
        log.info("Modificando repartidor con ID: {}", id);

        Repartidor repartidor = buscarRepartidorPorId(id);
        repartidor.setNombre(request.getNombre());
        repartidor.setTipoVehiculo(request.getTipoVehiculo());
        repartidor.setEstado(request.getEstado());

        repartidor = repartidorRepository.save(repartidor);
        return mapearAResponseDto(repartidor);
    }

    @Override
    @Transactional
    public void eliminarRepartidor(UUID id) {
        log.info("Eliminando repartidor con ID: {}", id);

        Repartidor repartidor = buscarRepartidorPorId(id);
        repartidorRepository.delete(repartidor);
    }

    /**
     * Método auxiliar privado para buscar un repartidor y lanzar excepción estandarizada si no existe.
     *
     * @param id Identificador único del repartidor.
     * @return Entidad Repartidor localizada.
     */
    private Repartidor buscarRepartidorPorId(UUID id) {
        return repartidorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró ningún repartidor con el ID: " + id));
    }

    /**
     * Método auxiliar privado para convertir la entidad en un DTO de respuesta.
     *
     * @param repartidor Entidad a transformar.
     * @return DTO resultante.
     */
    private RepartidorResponseDto mapearAResponseDto(Repartidor repartidor) {
        return new RepartidorResponseDto(
                repartidor.getId(),
                repartidor.getNombre(),
                repartidor.getTipoVehiculo(),
                repartidor.getEstado()
        );
    }
}