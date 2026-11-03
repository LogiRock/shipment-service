package com.logirock.shipment_service.clients;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import feign.Request;
import feign.Response;
import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.logirock.shipment_service.exceptions.CourierServiceNoDisponibleException;
import com.logirock.shipment_service.exceptions.RepartidorNoEncontradoException;

class CourierClientResilienceTest {

    private final CourierErrorDecoder errorDecoder = new CourierErrorDecoder();

    @AfterEach
    void limpiarRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void errorDecoderMapea404ARepartidorInexistente() {
        Exception exception = errorDecoder.decode("CourierClient#obtenerRepartidor", response(404));

        assertInstanceOf(RepartidorNoEncontradoException.class, exception);
        assertEquals("El repartidor solicitado no existe.", exception.getMessage());
    }

    @Test
    void errorDecoderMapea5xxAServiceUnavailable() {
        Exception exception = errorDecoder.decode("CourierClient#obtenerRepartidor", response(503));

        assertInstanceOf(CourierServiceNoDisponibleException.class, exception);
        assertTrue(exception.getMessage().contains("HTTP 503"));
    }

    @Test
    void fallbackMantiene404YConvierteCaidasA503() {
        CourierFallbackFactory factory = new CourierFallbackFactory();
        UUID id = UUID.randomUUID();

        CourierClient notFoundFallback = factory.create(new RepartidorNoEncontradoException("No existe"));
        assertEquals("No existe", assertThrows(RepartidorNoEncontradoException.class,
                () -> notFoundFallback.obtenerRepartidor(id)).getMessage());

        CourierClient unavailableFallback = factory.create(new IllegalStateException("connection refused"));
        assertInstanceOf(CourierServiceNoDisponibleException.class,
                assertThrows(CourierServiceNoDisponibleException.class,
                        () -> unavailableFallback.obtenerRepartidor(id)));
    }

    @Test
    void interceptorReenviaAuthorizationDeLaPeticionEntrante() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-de-prueba");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        RequestTemplate template = new RequestTemplate();

        new CourierRequestInterceptor().apply(template);

        assertEquals("Bearer token-de-prueba", template.headers().get("Authorization").iterator().next());
    }

    private Response response(int status) {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/api/v1/repartidores/" + UUID.randomUUID(),
                Map.of(),
                null,
                StandardCharsets.UTF_8,
                null);
        return Response.builder().status(status).reason("test").request(request).build();
    }
}
