package com.logirock.shipment_service.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.logirock.shipment_service.clients.CourierClient;
import com.logirock.shipment_service.dto.EnvioMapper;
import com.logirock.shipment_service.dto.EnvioRequestDto;
import com.logirock.shipment_service.dto.DisponibilidadRepartidorResponseDto;
import com.logirock.shipment_service.dto.EnvioResponseDto;
import com.logirock.shipment_service.enums.EstadoEnvio;
import com.logirock.shipment_service.exceptions.CourierServiceNoDisponibleException;
import com.logirock.shipment_service.exceptions.EnvioNoEncontradoException;
import com.logirock.shipment_service.exceptions.RepartidorNoEncontradoException;
import com.logirock.shipment_service.exceptions.TransicionInvalidaException;
import com.logirock.shipment_service.models.Cliente;
import com.logirock.shipment_service.models.Envio;
import com.logirock.shipment_service.models.HistorialEstadoEnvio;
import com.logirock.shipment_service.repositories.EnvioRepository;
import com.logirock.shipment_service.repositories.HistorialEstadoEnvioRepository;
import com.logirock.shipment_service.services.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class EnvioServiceImplTest {
    @Mock private EnvioRepository envioRepository;
    @Mock private HistorialEstadoEnvioRepository historialRepository;
    @Mock private ClienteService clienteService;
    @Mock private CourierClient courierClient;
    @Mock private EnvioAssignmentWriter envioAssignmentWriter;
    @InjectMocks private EnvioServiceImpl envioService;

    private UUID id;
    private Envio envio;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(envioService, "envioMapper", new EnvioMapper());
        id = UUID.randomUUID();
        cliente = new Cliente();
        cliente.setId(UUID.randomUUID());
        cliente.setNombre("Cliente de prueba");
        envio = new Envio();
        envio.setId(id);
        envio.setCodigoSeguimiento("ENV-ABC12345");
        envio.setCliente(cliente);
        envio.setPeso(5.5);
        envio.setAncho(30.0);
        envio.setAlto(20.0);
        envio.setLargo(40.0);
        envio.setEstado(EstadoEnvio.PENDIENTE);
    }

    @Test
    void crearDevuelveDtoYGeneraCodigoConPrefijo() {
        UUID userId = UUID.randomUUID();
        when(clienteService.buscarPorUserId(userId)).thenReturn(cliente);
        when(envioRepository.existsByCodigoSeguimiento(any())).thenReturn(false);
        when(envioRepository.save(any(Envio.class))).thenAnswer(call -> {
            Envio saved = call.getArgument(0);
            saved.setId(id);
            return saved;
        });

        EnvioResponseDto response = envioService.crear(
                EnvioRequestDto.builder().peso(2.0).ancho(3.0).alto(4.0).largo(5.0).build(), userId);

        assertEquals(id, response.getId());
        assertTrue(response.getCodigoSeguimiento().startsWith(EnvioServiceImpl.PREFIJO_SEGUIMIENTO));
        assertEquals(EstadoEnvio.PENDIENTE, response.getEstado());
        assertEquals(cliente.getId(), response.getClienteId());
    }

    @Test
    void consultasDevuelvenDtos() {
        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));
        when(envioRepository.findByCodigoSeguimiento(envio.getCodigoSeguimiento())).thenReturn(Optional.of(envio));
        when(envioRepository.findAll()).thenReturn(List.of(envio));

        assertEquals(id, envioService.buscarPorId(id).getId());
        assertEquals(envio.getCodigoSeguimiento(), envioService.buscarPorCodigoSeguimiento(envio.getCodigoSeguimiento()).getCodigoSeguimiento());
        assertEquals("Cliente de prueba", envioService.listarTodos().get(0).getClienteNombre());
    }

    @Test
    void buscarPorIdInexistenteLanzaExcepcionDeDominio() {
        when(envioRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EnvioNoEncontradoException.class, () -> envioService.buscarPorId(id));
    }

    @Test
    void actualizarModificaDatosSinReemplazarCliente() {
        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));
        when(envioRepository.save(envio)).thenReturn(envio);

        EnvioResponseDto response = envioService.actualizar(id,
                EnvioRequestDto.builder().peso(8.0).ancho(35.0).alto(25.0).largo(45.0).build());

        assertEquals(8.0, response.getPeso());
        assertEquals("ENV-ABC12345", response.getCodigoSeguimiento());
        assertEquals(cliente.getId(), response.getClienteId());
        verify(envioRepository).save(envio);
    }

    @Test
    void cancelarRegistraHistorialYDevuelveDto() {
        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));
        when(envioRepository.save(envio)).thenReturn(envio);

        EnvioResponseDto response = envioService.cancelarEnvio(id);

        assertEquals(EstadoEnvio.CANCELADO, response.getEstado());
        verify(historialRepository).save(any(HistorialEstadoEnvio.class));
    }

    @Test
    void transicionInvalidaLanzaExcepcionDeDominio() {
        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));
        assertThrows(TransicionInvalidaException.class,
                () -> envioService.cambiarEstado(id, EstadoEnvio.ENTREGADO));
        verify(envioRepository, never()).save(any());
    }

    @Test
    void iniciarRecorridoRequiereCourierAsignado() {
        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));
        assertThrows(TransicionInvalidaException.class,
                () -> envioService.cambiarEstado(id, EstadoEnvio.EN_RUTA));
    }

    @Test
    void asignarCourierActivoConsultaFeignYPersisteIdLogico() {
        UUID courierId = UUID.randomUUID();
        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));
        when(courierClient.validarDisponibilidad(courierId))
                .thenReturn(new DisponibilidadRepartidorResponseDto(true, null));
        EnvioResponseDto asignado = new EnvioResponseDto();
        asignado.setId(id);
        asignado.setCourierId(courierId);
        when(envioAssignmentWriter.guardarAsignacion(id, courierId)).thenReturn(asignado);

        EnvioResponseDto response = envioService.asignarCourier(id, courierId, "jwt");

        assertEquals(courierId, response.getCourierId());
        verify(courierClient).validarDisponibilidad(courierId);
        verify(envioAssignmentWriter).guardarAsignacion(id, courierId);
    }

    @Test
    void asignarCourierInexistentePropagaNotFoundYNoModificaEnvio() {
        UUID courierId = UUID.randomUUID();
        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));
        when(courierClient.validarDisponibilidad(courierId))
                .thenThrow(new RepartidorNoEncontradoException("El courier solicitado no existe."));

        assertThrows(RepartidorNoEncontradoException.class,
                () -> envioService.asignarCourier(id, courierId, "jwt"));
        verify(envioAssignmentWriter, never()).guardarAsignacion(any(), any());
        assertNull(envio.getCourierId());
    }

    @Test
    void asignarCourierInactivoDevuelveConflictoYNoModificaEnvio() {
        UUID courierId = UUID.randomUUID();
        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));
        when(courierClient.validarDisponibilidad(courierId))
                .thenReturn(new DisponibilidadRepartidorResponseDto(false, "El repartidor está INACTIVO."));

        assertThrows(TransicionInvalidaException.class,
                () -> envioService.asignarCourier(id, courierId, "jwt"));
        verify(envioAssignmentWriter, never()).guardarAsignacion(any(), any());
        assertNull(envio.getCourierId());
    }

    @Test
    void courierCaidoPropagaServiceUnavailableYNoModificaEnvio() {
        UUID courierId = UUID.randomUUID();
        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));
        when(courierClient.validarDisponibilidad(courierId))
                .thenThrow(new CourierServiceNoDisponibleException("Courier Service no está disponible."));

        assertThrows(CourierServiceNoDisponibleException.class,
                () -> envioService.asignarCourier(id, courierId, "jwt"));
        verify(envioAssignmentWriter, never()).guardarAsignacion(any(), any());
        assertNull(envio.getCourierId());
    }
}
