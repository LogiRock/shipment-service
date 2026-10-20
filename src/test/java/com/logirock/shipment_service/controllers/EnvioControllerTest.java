package com.logirock.shipment_service.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.logirock.shipment_service.dto.EnvioResponseDto;
import com.logirock.shipment_service.enums.EstadoEnvio;
import com.logirock.shipment_service.models.Direccion;
import com.logirock.shipment_service.services.EnvioService;

@ExtendWith(MockitoExtension.class)

public class EnvioControllerTest {
    @Mock
    private EnvioService envioService;

    @InjectMocks
    private EnvioController envioController;

    @Test
    void cancelarEnvioRetornaRespuestaExitosaConEstadoCancelado() {
        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();

        Direccion origen = new Direccion(/* datos requeridos */);
        Direccion destino = new Direccion(/* datos requeridos */);

        EnvioResponseDto responseDto = new EnvioResponseDto(
                id,
                "TRK-123",
                2.0,
                10.0,
                20.0,
                30.0,
                EstadoEnvio.CANCELADO,
                clienteId,
                "Cliente de prueba",
                origen,
                destino);
        when(envioService.cancelarEnvio(id)).thenReturn(responseDto);

        ResponseEntity<EnvioResponseDto> response = envioController.cancelarEnvio(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(EstadoEnvio.CANCELADO, response.getBody().getEstado());
    }
}
