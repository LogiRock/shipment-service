package com.logirock.shipment_service.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import com.logirock.shipment_service.dto.EnvioResponseDto;
import com.logirock.shipment_service.enums.EstadoEnvio;
import com.logirock.shipment_service.models.Cliente;
import com.logirock.shipment_service.models.Envio;
import com.logirock.shipment_service.models.Direccion;
import com.logirock.shipment_service.services.EnvioService;

@ExtendWith(MockitoExtension.class)
class EnvioControllerTest {
    @Mock
    private EnvioService envioService;

    @InjectMocks
    private EnvioController envioController;

    @Test
    void asignarCourierDevuelve200CuandoLaAsignacionEsExitosa() {
        UUID envioId = UUID.randomUUID();
        UUID courierId = UUID.randomUUID();
        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("token-de-prueba");
        EnvioResponseDto responseDto = new EnvioResponseDto();
        responseDto.setId(envioId);
        responseDto.setCourierId(courierId);
        when(envioService.asignarCourier(envioId, courierId, "token-de-prueba")).thenReturn(responseDto);

        ResponseEntity<EnvioResponseDto> response = envioController.asignarCourier(envioId, courierId, jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(courierId, response.getBody().getCourierId());
    }

    @Test
    void cancelarEnvioRetornaRespuestaExitosaConEstadoCancelado() {
        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        Direccion origen = new Direccion();
        Direccion destino = new Direccion();
        EnvioResponseDto responseDto = new EnvioResponseDto(
                id, "TRK-123", 2.0, 10.0, 20.0, 30.0, EstadoEnvio.CANCELADO,
                null, clienteId, "Cliente de prueba", origen, destino);
        when(envioService.cancelarEnvio(id)).thenReturn(responseDto);

        ResponseEntity<EnvioResponseDto> response = envioController.cancelarEnvio(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(EstadoEnvio.CANCELADO, response.getBody().getEstado());
    }

}
