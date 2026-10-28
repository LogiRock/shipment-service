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

import com.logirock.shipment_service.dto.ClienteRequestDto;
import com.logirock.shipment_service.dto.ClienteResponseDto;
import com.logirock.shipment_service.services.ClienteService;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    // @Test
    // void registrarCliente_DebeRetornarEstado201_CuandoPeticionEsValida() {
    //     // Arrange: Preparamos los datos
    //     ClienteRequestDto requestDto = new ClienteRequestDto("Mateo Pérez", "mateo@ejemplo.com", "+5493881234567");
    //     ClienteResponseDto responseDto = new ClienteResponseDto(UUID.randomUUID(), "Mateo Pérez", "mateo@ejemplo.com", "+5493881234567");

    //     // Simulamos lo que respondería el servicio
    //     when(clienteService.registrarCliente(any(ClienteRequestDto.class))).thenReturn(responseDto);

    //     // Act: Llamamos directamente al método de Java, sin simular peticiones HTTP
    //     ResponseEntity<ClienteResponseDto> response = clienteController.registrarCliente(requestDto);

    //     // Assert: Verificamos que el código HTTP sea 201 y los datos coincidan
    //     assertNotNull(response);
    //     assertEquals(HttpStatus.CREATED, response.getStatusCode());
    //     assertNotNull(response.getBody());
    //     assertEquals("mateo@ejemplo.com", response.getBody().getEmail());
    // }
}