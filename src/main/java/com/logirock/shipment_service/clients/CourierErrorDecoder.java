package com.logirock.shipment_service.clients;

import feign.Response;
import feign.codec.ErrorDecoder;

import com.logirock.shipment_service.exceptions.CourierServiceNoDisponibleException;
import com.logirock.shipment_service.exceptions.RepartidorNoEncontradoException;

public class CourierErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();
    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404) {
            return new RepartidorNoEncontradoException("El repartidor solicitado no existe.");
        }
        if (response.status() >= 500) {
            return new CourierServiceNoDisponibleException("El repartidor no está disponible (HTTP " + response.status() + ").");
        }
        return defaultDecoder.decode(methodKey, response);
    }
}
