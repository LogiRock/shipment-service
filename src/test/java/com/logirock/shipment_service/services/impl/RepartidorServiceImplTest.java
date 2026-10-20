package com.logirock.shipment_service.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.logirock.shipment_service.dto.RepartidorRequestDto;
import com.logirock.shipment_service.dto.RepartidorResponseDto;
import com.logirock.shipment_service.enums.EstadoRepartidor;
import com.logirock.shipment_service.models.Repartidor;
import com.logirock.shipment_service.repositories.RepartidorRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * Pruebas unitarias para validar la lógica del servicio de repartidores.
 */
@ExtendWith(MockitoExtension.class)
class RepartidorServiceImplTest {

    /**
     * Dependencia simulada del repositorio.
     */
    @Mock
    private RepartidorRepository repartidorRepository;

    /**
     * Instancia real del servicio a probar.
     */
    @InjectMocks
    private RepartidorServiceImpl repartidorService;

    /**
     * Verifica el registro exitoso de un repartidor.
     */
    @Test
    void registrarRepartidor_DebeRetornarRepartidor_CuandoDatosSonValidos() {
        RepartidorRequestDto requestDto = new RepartidorRequestDto("Juan Gomez", "Camioneta", EstadoRepartidor.ACTIVO);
        Repartidor repartidorSimulado = new Repartidor();
        repartidorSimulado.setId(UUID.randomUUID());
        repartidorSimulado.setNombre("Juan Gomez");
        repartidorSimulado.setTipoVehiculo("Camioneta");
        repartidorSimulado.setEstado(EstadoRepartidor.ACTIVO);

        when(repartidorRepository.save(any(Repartidor.class))).thenReturn(repartidorSimulado);

        RepartidorResponseDto response = repartidorService.registrarRepartidor(requestDto);

        assertNotNull(response);
        assertEquals("Juan Gomez", response.getNombre());
        verify(repartidorRepository, times(1)).save(any(Repartidor.class));
    }

    /**
     * Verifica que se lance una excepción si se solicita un ID inexistente.
     */
    @Test
    void obtenerRepartidor_DebeLanzarExcepcion_CuandoRepartidorNoExiste() {
        UUID idAleatorio = UUID.randomUUID();
        when(repartidorRepository.findById(idAleatorio)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> repartidorService.obtenerRepartidor(idAleatorio));
    }
}
