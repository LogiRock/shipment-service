package com.logirock.shipment_service.exceptions;

public class CourierServiceNoDisponibleException extends RuntimeException {
    public CourierServiceNoDisponibleException(String message) { super(message); }
    public CourierServiceNoDisponibleException(String message, Throwable cause) { super(message, cause); }
}
