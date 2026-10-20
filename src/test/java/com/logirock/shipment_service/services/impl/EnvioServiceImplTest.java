
package com.logirock.shipment_service.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.logirock.shipment_service.dto.EnvioResponseDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.logirock.shipment_service.enums.EstadoEnvio;
import com.logirock.shipment_service.models.Envio;
import com.logirock.shipment_service.repositories.EnvioRepository;

@ExtendWith(MockitoExtension.class)
class EnvioServiceImplTest {

    @Mock
    private EnvioRepository envioRepository;

    @InjectMocks
    private EnvioServiceImpl envioService;

    private UUID id;
    private Envio envio;

    @BeforeEach
    void setUp() {
        envio = new Envio();
        envio.setId(UUID.randomUUID());
        envio.setCodigoSeguimiento("ENV-ABC12345");
        envio.setPeso(5.5);
        envio.setAncho(30.0);
        envio.setAlto(20.0);
        envio.setLargo(40.0);
        envio.setEstado(EstadoEnvio.PENDIENTE);
    }

    @Test
    void crearEnvioDebeRetornarEnvio_CuandoDatosSonValidos() {
        when(envioRepository.existsByCodigoSeguimiento(anyString()))
                .thenReturn(false);

        when(envioRepository.save(any(Envio.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Envio response = envioService.crear(envio);

        assertNotNull(response);
        assertNotNull(response.getCodigoSeguimiento());
        assertTrue(response.getCodigoSeguimiento().startsWith("ENV-"));

        verify(envioRepository, times(1)).save(any(Envio.class));
    }

    @Test
    void buscarEnvioDebeRetornarEnvio_CuandoIdExiste() {
        UUID id = envio.getId();

        when(envioRepository.findById(id))
                .thenReturn(Optional.of(envio));

        Envio response = envioService.buscarPorId(id);

        assertNotNull(response);
        assertEquals(id, response.getId());
        verify(envioRepository, times(1)).findById(id);
    }

    @Test
    void buscarEnvioDebeLanzarExcepcion_CuandoIdNoExiste() {
        UUID id = UUID.randomUUID();

        when(envioRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class,
                () -> envioService.buscarPorId(id));

        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void actualizarEnvioDebeConservarCodigoYEstado() {
        UUID id = envio.getId();

        Envio datos = new Envio();
        datos.setPeso(8.0);
        datos.setAncho(35.0);
        datos.setAlto(25.0);
        datos.setLargo(45.0);

        when(envioRepository.findById(id))
                .thenReturn(Optional.of(envio));

        when(envioRepository.save(any(Envio.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Envio response = envioService.actualizar(id, datos);

        assertEquals("ENV-ABC12345", response.getCodigoSeguimiento());
        assertEquals(EstadoEnvio.PENDIENTE, response.getEstado());
        assertEquals(8.0, response.getPeso());

        verify(envioRepository, times(1)).save(envio);
    }

    @Test
    void eliminarEnvioDebeEliminarlo_CuandoIdExiste() {
        UUID id = envio.getId();

        when(envioRepository.findById(id))
                .thenReturn(Optional.of(envio));

        envioService.eliminar(id);

        verify(envioRepository, times(1)).delete(envio);
    }

    @Test
    void listarEnviosDebeRetornarLista_CuandoHayEnvios() {
        when(envioRepository.findAll()).thenReturn(List.of(envio));

        List<Envio> response = envioService.listarTodos();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(envio.getId(), response.get(0).getId());

        verify(envioRepository, times(1)).findAll();
    }

    @Test
    void buscarEnvioDebeRetornarEnvio_CuandoCodigoExiste() {
        String codigo = envio.getCodigoSeguimiento();

        when(envioRepository.findByCodigoSeguimiento(codigo))
                .thenReturn(Optional.of(envio));

        Envio response = envioService.buscarPorCodigoSeguimiento(codigo);

        assertNotNull(response);
        assertEquals(codigo, response.getCodigoSeguimiento());

        verify(envioRepository, times(1))
                .findByCodigoSeguimiento(codigo);
    }

    @Test
    void cancelarEnvioActualizaEstadoYRetornaEnvioCancelado() {
        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));
        when(envioRepository.save(envio)).thenReturn(envio);

        EnvioResponseDto response = envioService.cancelarEnvio(id);

        assertEquals(EstadoEnvio.CANCELADO, response.getEstado());
        assertEquals(id, response.getId());
        verify(envioRepository).save(envio);
    }

    @Test
    void cancelarEnvioLanzaExcepcionCuandoNoExiste() {
        when(envioRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> envioService.cancelarEnvio(id));
        verify(envioRepository, never()).save(envio);
    }

    @Test
    void cancelarEnvioRechazaEstadosDistintosDePendiente() {
        envio.setEstado(EstadoEnvio.EN_RUTA);
        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));

        assertThrows(IllegalStateException.class, () -> envioService.cancelarEnvio(id));
        verify(envioRepository, never()).save(envio);
    }

}
