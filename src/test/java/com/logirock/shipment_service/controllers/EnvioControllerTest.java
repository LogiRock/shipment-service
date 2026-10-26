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

    @Test
    void asignarCourierRetornaElEnvioConElIdLogicoAsignado() {
        UUID envioId = UUID.randomUUID();
        UUID courierId = UUID.randomUUID();
        Envio envio = new Envio();
        envio.setId(envioId);
        envio.setCodigoSeguimiento("ENV-ABC12345");
        envio.setPeso(2.0);
        envio.setAncho(10.0);
        envio.setAlto(20.0);
        envio.setLargo(30.0);
        envio.setEstado(EstadoEnvio.PENDIENTE);
        envio.setCourierId(courierId);
        Cliente cliente = new Cliente();
        cliente.setId(UUID.randomUUID());
        cliente.setNombre("Cliente de prueba");
        envio.setCliente(cliente);
        when(envioService.asignarCourier(envioId, courierId)).thenReturn(envio);

        ResponseEntity<EnvioResponseDto> response = envioController.asignarCourier(envioId, courierId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(courierId, response.getBody().getCourierId());
    }
}
