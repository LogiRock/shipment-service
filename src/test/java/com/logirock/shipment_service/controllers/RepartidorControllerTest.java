package com.logirock.shipment_service.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.logirock.shipment_service.dto.RepartidorRequestDto;
import com.logirock.shipment_service.dto.RepartidorResponseDto;
import com.logirock.shipment_service.enums.EstadoRepartidor;
import com.logirock.shipment_service.services.RepartidorService;

/**
 * Pruebas unitarias para validar las respuestas HTTP del controlador de repartidores.
 */
@ExtendWith(MockitoExtension.class)
class RepartidorControllerTest {

    /**
     * Dependencia simulada del servicio de lógica de negocio.
     */
    @Mock
    private RepartidorService repartidorService;

    /**
     * Instancia real del controlador a probar.
     */
    @InjectMocks
    private RepartidorController repartidorController;

    /**
     * Verifica que el controlador devuelva el código 201 Created tras un alta correcta.
     */
    @Test
    void registrarRepartidor_DebeRetornarEstado201_CuandoPeticionEsValida() {
        RepartidorRequestDto requestDto = new RepartidorRequestDto("Juan Gomez", "Moto", EstadoRepartidor.ACTIVO);
        RepartidorResponseDto responseDto = new RepartidorResponseDto(UUID.randomUUID(), "Juan Gomez", "Moto", EstadoRepartidor.ACTIVO);

        when(repartidorService.registrarRepartidor(any(RepartidorRequestDto.class))).thenReturn(responseDto);

        ResponseEntity<RepartidorResponseDto> response = repartidorController.registrarRepartidor(requestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Juan Gomez", response.getBody().getNombre());
    }
}