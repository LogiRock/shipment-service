package com.logirock.shipment_service.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import com.logirock.shipment_service.dto.ErrorResponseDto;

import jakarta.persistence.EntityNotFoundException;

/**
 * Pruebas unitarias puras para el manejador global de excepciones.
 * Utiliza Mockito para validar la lógica de transformación de errores sin depender de un entorno web.
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    /**
     * Interceptor global real que se está probando.
     */
    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    /**
     * Simulación de la petición web para inyectar la ruta en la respuesta de error sin levantar Tomcat.
     */
    @Mock
    private WebRequest webRequest;

    /**
     * Verifica que una EntityNotFoundException genere una respuesta con estado HTTP 404.
     */
    @Test
    void deberiaRetornarNotFound_CuandoLanzaEntityNotFoundException() {
        EntityNotFoundException excepcion = new EntityNotFoundException("Recurso no encontrado");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/clientes/1");

        ResponseEntity<ErrorResponseDto> respuesta = globalExceptionHandler.handleEntityNotFoundException(excepcion, webRequest);

        assertNotNull(respuesta);
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(404, respuesta.getBody().getStatus());
        assertEquals("Recurso no encontrado", respuesta.getBody().getMessage());
        assertEquals("/api/v1/clientes/1", respuesta.getBody().getPath());
    }

    /**
     * Verifica que una IllegalArgumentException genere una respuesta con estado HTTP 409 Conflict.
     */
    @Test
    void deberiaRetornarConflict_CuandoLanzaIllegalArgumentException() {
        IllegalArgumentException excepcion = new IllegalArgumentException("El email ya existe");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/clientes");

        ResponseEntity<ErrorResponseDto> respuesta = globalExceptionHandler.handleIllegalArgumentException(excepcion, webRequest);

        assertNotNull(respuesta);
        assertEquals(HttpStatus.CONFLICT, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(409, respuesta.getBody().getStatus());
        assertEquals("El email ya existe", respuesta.getBody().getMessage());
    }

    /**
     * Verifica que una MethodArgumentNotValidException capture los errores de los campos
     * y genere una respuesta con estado HTTP 400 Bad Request.
     */
    @Test
    void deberiaRetornarBadRequest_CuandoLanzaMethodArgumentNotValidException() {
        MethodArgumentNotValidException excepcionSimulada = mock(MethodArgumentNotValidException.class);
        BindingResult resultadoSimulado = mock(BindingResult.class);
        FieldError errorCampo = new FieldError("cliente", "email", "El formato no es válido");

        when(excepcionSimulada.getBindingResult()).thenReturn(resultadoSimulado);
        when(resultadoSimulado.getFieldErrors()).thenReturn(List.of(errorCampo));
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/clientes");

        ResponseEntity<ErrorResponseDto> respuesta = globalExceptionHandler.handleValidationExceptions(excepcionSimulada, webRequest);

        assertNotNull(respuesta);
        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(400, respuesta.getBody().getStatus());
        assertEquals("Error en la validación de los datos enviados.", respuesta.getBody().getMessage());
    }
}