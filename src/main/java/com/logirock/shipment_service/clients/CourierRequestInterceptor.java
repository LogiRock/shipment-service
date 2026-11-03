package com.logirock.shipment_service.clients;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/** Reenvía el token del usuario que originó la llamada entre servicios. */
public class CourierRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        var attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            String authorization = servletAttributes.getRequest().getHeader("Authorization");
            if (authorization != null && !authorization.isBlank()) {
                template.header("Authorization", authorization);
            }
        }
    }
}
