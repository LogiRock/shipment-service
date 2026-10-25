package com.logirock.shipment_service.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.logirock.shipment_service.enums.EstadoEnvio;
import com.logirock.shipment_service.models.Envio;
import com.logirock.shipment_service.repositories.EnvioRepository;

import com.logirock.shipment_service.models.Cliente;
import com.logirock.shipment_service.models.HistorialEstadoEnvio;
import com.logirock.shipment_service.repositories.HistorialEstadoEnvioRepository;
import com.logirock.shipment_service.services.ClienteService;

@ExtendWith(MockitoExtension.class)
class EnvioServiceImplTest {

    @Mock
    private EnvioRepository envioRepository;
    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private EnvioServiceImpl envioService;
    @Mock
    private HistorialEstadoEnvioRepository historialRepository;

    private UUID id;
    private Envio envio;

    @BeforeEach
    void setUp() {
        envio = new Envio();
        envio.setId(UUID.randomUUID());
        id = envio.getId();
        envio.setCodigoSeguimiento("ENV-ABC12345");
        envio.setPeso(5.5);
        envio.setAncho(30.0);
        envio.setAlto(20.0);
        envio.setLargo(40.0);
        envio.setEstado(EstadoEnvio.PENDIENTE);
        Cliente cliente = new Cliente();
        cliente.setId(UUID.randomUUID());
        cliente.setNombre("Cliente de prueba");

        envio.setCliente(cliente);
    }

    @Test
    void crearEnvioDebeRetornarEnvio_CuandoDatosSonValidos() {
        UUID clienteId = UUID.randomUUID();

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setNombre("Cliente de prueba");

        when(clienteService.buscarEntidadPorId(clienteId))
                .thenReturn(cliente);

        when(envioRepository.save(any(Envio.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Envio response = envioService.crear(envio, clienteId);

        assertNotNull(response);
        assertNotNull(response.getCodigoSeguimiento());
        assertTrue(response.getCodigoSeguimiento().startsWith("ENV-"));
        assertEquals(EstadoEnvio.PENDIENTE, response.getEstado());
        assertEquals(clienteId, response.getCliente().getId());

        verify(envioRepository).save(any(Envio.class));
        verify(clienteService).buscarEntidadPorId(clienteId);
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

        List<EnvioResponseDto> response = envioService.listarTodos();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(envio.getId(), response.get(0).getId());
        assertEquals("Cliente de prueba", response.get(0).getClienteNombre());

        verify(envioRepository).findAll();
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
        UUID id = UUID.randomUUID();
        envio.setId(id);
        envio.setEstado(EstadoEnvio.PENDIENTE);

        when(envioRepository.findById(id))
                .thenReturn(Optional.of(envio));
        when(envioRepository.save(envio))
                .thenReturn(envio);

        EnvioResponseDto response = envioService.cancelarEnvio(id);

        assertEquals(EstadoEnvio.CANCELADO, response.getEstado());
        assertEquals(id, response.getId());

        verify(envioRepository).save(envio);
        verify(historialRepository).save(any(HistorialEstadoEnvio.class));
    }

    @Test
    void cancelarEnvioLanzaExcepcionCuandoNoExiste() {
        when(envioRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> envioService.cancelarEnvio(id));
        assertEquals(HttpStatus.NOT_FOUND, excepcion.getStatusCode());
        verify(envioRepository, never()).save(envio);
    }

    @Test
    void cancelarEnvioRechazaEstadosDistintosDePendiente() {
        envio.setEstado(EstadoEnvio.EN_RUTA);
        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> envioService.cancelarEnvio(id));
        assertEquals(HttpStatus.CONFLICT, excepcion.getStatusCode());
        verify(envioRepository, never()).save(envio);
    }

    @Test
    void asignarCourierDebePersistirIdLogicoCuandoEnvioEstaPendiente() {
        Long courierId = 123L;
        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));
        when(envioRepository.save(envio)).thenReturn(envio);

        Envio response = envioService.asignarCourier(id, courierId);

        assertEquals(courierId, response.getCourierId());
        verify(envioRepository).save(envio);
    }

    @Test
    void asignarCourierDebeRechazarIdNuloONoPositivo() {
        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));

        assertThrows(IllegalArgumentException.class, () -> envioService.asignarCourier(id, 0L));
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void asignarCourierDebeRechazarEnvioInexistente() {
        when(envioRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> envioService.asignarCourier(id, 123L));
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void asignarCourierDebeRechazarEnvioQueYaNoEstaPendiente() {
        envio.setEstado(EstadoEnvio.EN_RUTA);
        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));

        assertThrows(IllegalArgumentException.class, () -> envioService.asignarCourier(id, 123L));
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void asignarCourierDebeRechazarReemplazoDeCourier() {
        envio.setCourierId(111L);
        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));

        assertThrows(IllegalArgumentException.class, () -> envioService.asignarCourier(id, 123L));
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void cambiarEstadoDePendienteAEnRutaEsPermitidoCuandoTieneCourierId() {
        envio.setEstado(EstadoEnvio.PENDIENTE);
        envio.setCourierId(123L);
        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));
        when(envioRepository.save(envio)).thenReturn(envio);

        Envio resultado = envioService.cambiarEstado(id, EstadoEnvio.EN_RUTA);

        assertEquals(EstadoEnvio.EN_RUTA, resultado.getEstado());
        verify(historialRepository).save(any(HistorialEstadoEnvio.class));
        verify(envioRepository).save(envio);
    }

    @Test
    void cambiarEstadoDePendienteAEnRutaSeRechazaCuandoNoTieneCourierId() {
        envio.setEstado(EstadoEnvio.PENDIENTE);
        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> envioService.cambiarEstado(id, EstadoEnvio.EN_RUTA));

        assertEquals(HttpStatus.CONFLICT, excepcion.getStatusCode());
        verify(envioRepository, never()).save(any(Envio.class));
        verify(historialRepository, never()).save(any(HistorialEstadoEnvio.class));
    }

    @Test
    void cambiarEstadoDePendienteACanceladoEsPermitido() {
        envio.setEstado(EstadoEnvio.PENDIENTE);

        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));
        when(envioRepository.save(envio)).thenReturn(envio);

        Envio resultado = envioService.cambiarEstado(id, EstadoEnvio.CANCELADO);

        assertEquals(EstadoEnvio.CANCELADO, resultado.getEstado());
        verify(historialRepository).save(any(HistorialEstadoEnvio.class));
        verify(envioRepository).save(envio);
    }

    @Test
    void cambiarEstadoDeEnRutaAEntregadoEsPermitido() {
        envio.setEstado(EstadoEnvio.EN_RUTA);

        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));
        when(envioRepository.save(envio)).thenReturn(envio);

        Envio resultado = envioService.cambiarEstado(id, EstadoEnvio.ENTREGADO);

        assertEquals(EstadoEnvio.ENTREGADO, resultado.getEstado());
        verify(historialRepository).save(any(HistorialEstadoEnvio.class));
        verify(envioRepository).save(envio);
    }

    @Test
    void cambiarEstadoDePendienteAEntregadoLanzaExcepcion() {
        envio.setEstado(EstadoEnvio.PENDIENTE);

        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> envioService.cambiarEstado(id, EstadoEnvio.ENTREGADO));

        assertEquals(HttpStatus.CONFLICT, excepcion.getStatusCode());
        verify(envioRepository, never()).save(any(Envio.class));
        verify(historialRepository, never()).save(any(HistorialEstadoEnvio.class));
    }

    @Test
    void cambiarEstadoDeEnRutaACanceladoLanzaExcepcion() {
        envio.setEstado(EstadoEnvio.EN_RUTA);

        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> envioService.cambiarEstado(id, EstadoEnvio.CANCELADO));

        assertEquals(HttpStatus.CONFLICT, excepcion.getStatusCode());
        verify(envioRepository, never()).save(any(Envio.class));
        verify(historialRepository, never()).save(any(HistorialEstadoEnvio.class));
    }

    @Test
    void cambiarEstadoConEstadoNuloLanzaExcepcion() {
        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> envioService.cambiarEstado(id, null));

        assertEquals(HttpStatus.BAD_REQUEST, excepcion.getStatusCode());
        verify(envioRepository, never()).save(any(Envio.class));
        verify(historialRepository, never()).save(any(HistorialEstadoEnvio.class));
    }

}
