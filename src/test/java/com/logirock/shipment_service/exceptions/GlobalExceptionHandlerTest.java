package com.logirock.shipment_service.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import com.logirock.shipment_service.dto.ErrorResponseDto;
import com.logirock.shipment_service.exceptions.CourierServiceNoDisponibleException;
import com.logirock.shipment_service.exceptions.RepartidorNoEncontradoException;
import com.logirock.shipment_service.exceptions.TransicionInvalidaException;

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

    @Test
    void deberiaRetornarNotFound_CuandoCourierNoExiste() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/envios/1/courier/2");

        ResponseEntity<ErrorResponseDto> respuesta = globalExceptionHandler
                .handleRepartidorNoEncontradoException(
                        new RepartidorNoEncontradoException("El courier solicitado no existe."), webRequest);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertEquals(404, respuesta.getBody().getStatus());
        assertEquals("El courier solicitado no existe.", respuesta.getBody().getMessage());
    }

    @Test
    void deberiaRetornarConflict_CuandoCourierEstaInactivo() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/envios/1/courier/2");

        ResponseEntity<ErrorResponseDto> respuesta = globalExceptionHandler
                .handleTransicionInvalidaException(
                        new TransicionInvalidaException("El courier está inactivo; no se puede asignar el envío."), webRequest);

        assertEquals(HttpStatus.CONFLICT, respuesta.getStatusCode());
        assertEquals(409, respuesta.getBody().getStatus());
        assertTrue(respuesta.getBody().getMessage().contains("inactivo"));
    }

    @Test
    void deberiaRetornarServiceUnavailable_CuandoCourierServiceNoResponde() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/envios/1/courier/2");

        ResponseEntity<ErrorResponseDto> respuesta = globalExceptionHandler
                .handleCourierServiceNoDisponibleException(
                        new CourierServiceNoDisponibleException("Courier Service no está disponible."), webRequest);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, respuesta.getStatusCode());
        assertEquals(503, respuesta.getBody().getStatus());
        assertEquals("Courier Service no está disponible.", respuesta.getBody().getMessage());
    }

    /**
     * Verifica que una IllegalArgumentException genere una respuesta con estado HTTP 400.
     */
    @Test
    void deberiaRetornarBadRequest_CuandoLanzaIllegalArgumentException() {
        IllegalArgumentException excepcion = new IllegalArgumentException("El email ya existe");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/clientes");

        ResponseEntity<ErrorResponseDto> respuesta = globalExceptionHandler.handleIllegalArgumentException(excepcion, webRequest);

        assertNotNull(respuesta);
        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(400, respuesta.getBody().getStatus());
        assertEquals("El email ya existe", respuesta.getBody().getMessage());
    }

    @Test
    void deberiaConservarElEstadoHttp_CuandoLanzaResponseStatusException() {
        ResponseStatusException excepcion = new ResponseStatusException(
                HttpStatus.CONFLICT, "Transición de estado no permitida");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/envios/1/estado");

        ResponseEntity<ErrorResponseDto> respuesta = globalExceptionHandler
                .handleResponseStatusException(excepcion, webRequest);

        assertEquals(HttpStatus.CONFLICT, respuesta.getStatusCode());
        assertEquals(409, respuesta.getBody().getStatus());
        assertEquals("Transición de estado no permitida", respuesta.getBody().getMessage());
    }

    @Test
    void deberiaRetornarBadRequest_CuandoLanzaIllegalStateException() {
        IllegalStateException excepcion = new IllegalStateException("El estado actual no permite la operación");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/envios/1/cancelacion");

        ResponseEntity<ErrorResponseDto> respuesta = globalExceptionHandler
                .handleIllegalArgumentException(excepcion, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(400, respuesta.getBody().getStatus());
        assertEquals("El estado actual no permite la operación", respuesta.getBody().getMessage());
    }

    /**
     * Verifica que una MethodArgumentNotValidException capture los errores de los campos
     * y genere una respuesta con estado HTTP 400 Bad Request.
     */
    @Test
    void deberiaRetornarBadRequest_CuandoLanzaMethodArgumentNotValidException() throws NoSuchMethodException {
        BindingResult resultadoSimulado = new BeanPropertyBindingResult(new Object(), "cliente");
        FieldError errorCampo = new FieldError("cliente", "email", "El formato no es válido");
        resultadoSimulado.addError(errorCampo);
        MethodParameter parametro = new MethodParameter(
                getClass().getDeclaredMethod("metodoDePrueba", String.class), 0);
        MethodArgumentNotValidException excepcionSimulada =
                new MethodArgumentNotValidException(parametro, resultadoSimulado);

        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/clientes");

        ResponseEntity<ErrorResponseDto> respuesta = globalExceptionHandler.handleValidationExceptions(excepcionSimulada, webRequest);

        assertNotNull(respuesta);
        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(400, respuesta.getBody().getStatus());
        assertEquals("Error en la validación de los datos enviados.", respuesta.getBody().getMessage());
    }

    private void metodoDePrueba(String email) {
    }
}
