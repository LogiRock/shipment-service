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
import com.logirock.shipment_service.enums.EstadoRepartidor;
import com.logirock.shipment_service.models.Envio;
import com.logirock.shipment_service.models.Repartidor;
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
        EnvioResponseDto responseDto = new EnvioResponseDto(id, "TRK-123", 2.0, 10.0, 20.0, 30.0,
                EstadoEnvio.CANCELADO);
        when(envioService.cancelarEnvio(id)).thenReturn(responseDto);

        ResponseEntity<EnvioResponseDto> response = envioController.cancelarEnvio(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(EstadoEnvio.CANCELADO, response.getBody().getEstado());
    }

    @Test
    void asignarRepartidorRetornaElEnvioConElRepartidorAsignado() {
        UUID envioId = UUID.randomUUID();
        UUID repartidorId = UUID.randomUUID();
        Envio envio = new Envio();
        envio.setId(envioId);
        envio.setCodigoSeguimiento("ENV-ABC12345");
        envio.setPeso(2.0);
        envio.setAncho(10.0);
        envio.setAlto(20.0);
        envio.setLargo(30.0);
        envio.setEstado(EstadoEnvio.PENDIENTE);
        Repartidor repartidor = new Repartidor();
        repartidor.setId(repartidorId);
        repartidor.setEstado(EstadoRepartidor.ACTIVO);
        envio.setRepartidor(repartidor);
        when(envioService.asignarRepartidor(envioId, repartidorId)).thenReturn(envio);

        ResponseEntity<EnvioResponseDto> response = envioController.asignarRepartidor(envioId, repartidorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(repartidorId, response.getBody().getRepartidorId());
    }
}
