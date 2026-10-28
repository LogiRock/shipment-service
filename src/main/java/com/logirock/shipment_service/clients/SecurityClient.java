package com.logirock.shipment_service.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.logirock.shipment_service.dto.RegistroUsuarioRequest;
import com.logirock.shipment_service.dto.UsuarioResponseDto;

@Component
public class SecurityClient {

    private final RestClient restClient;

    public SecurityClient(
            RestClient.Builder builder,
            @Value("${security.service.url}") String url) {

        this.restClient = builder
                .baseUrl(url)
                .build();
    }

    public UsuarioResponseDto registrarUsuario(
            RegistroUsuarioRequest request) {

        return restClient.post()
                .uri("/api/auth/register")
                .body(request)
                .retrieve()
                .body(UsuarioResponseDto.class);
    }
}