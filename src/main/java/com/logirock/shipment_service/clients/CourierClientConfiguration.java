package com.logirock.shipment_service.clients;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

/** Configuración de Feign aislada para CourierClient. */
public class CourierClientConfiguration {

    @Bean
    ErrorDecoder courierErrorDecoder() {
        return new CourierErrorDecoder();
    }

    @Bean
    CourierRequestInterceptor courierRequestInterceptor() {
        return new CourierRequestInterceptor();
    }
}
