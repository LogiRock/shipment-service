package com.logirock.shipment_service.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.logirock.shipment_service.dto.ClienteRequestDto;
import com.logirock.shipment_service.dto.ClienteResponseDto;
import com.logirock.shipment_service.models.Cliente;
import com.logirock.shipment_service.repositories.ClienteRepository;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private ClienteRequestDto requestDto;
    private Cliente clienteGuardado;

    @BeforeEach
    void setUp() {
        requestDto = new ClienteRequestDto("Mateo Pérez", "mateo@ejemplo.com", "+5493881234567");
        clienteGuardado = new Cliente();
        clienteGuardado.setId(UUID.randomUUID());
        clienteGuardado.setNombre("Mateo Pérez");
        clienteGuardado.setEmail("mateo@ejemplo.com");
        clienteGuardado.setTelefono("+5493881234567");
    }


//     @Test
//     void registrarClienteDebeRetornarCliente_CuandoDatosSonValidos() {
//         when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
//         when(clienteRepository.existsByTelefono(anyString())).thenReturn(false);
//         when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteGuardado);
//         ClienteResponseDto response = clienteService.registrarCliente(requestDto);
//         assertNotNull(response);
//         assertEquals(requestDto.getEmail(), response.getEmail());
//         verify(clienteRepository, times(1)).save(any(Cliente.class)); // Verificamos que se llamó al save
//     }

//     @Test
//     void registrarClienteDebeLanzarExcepcion_CuandoEmailYaExiste() {
//         when(clienteRepository.existsByEmail(anyString())).thenReturn(true);
// IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//             clienteService.registrarCliente(requestDto);
//         });
//         assertEquals("El email ingresado ya se encuentra registrado.", exception.getMessage());
//         verify(clienteRepository, never()).save(any(Cliente.class)); // Verificamos que JAMÁS intentó guardar
//     }
}