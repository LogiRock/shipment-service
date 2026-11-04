package com.logirock.shipment_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.logirock.shipment_service.dto.RegistroUsuarioRequest;
import com.logirock.shipment_service.dto.UsuarioResponseDto;

@FeignClient(name = "SECURITY-SERVICE")
public interface SecurityClient {

    @PostMapping("/api/auth/register")
    UsuarioResponseDto registrarUsuario(
            @RequestBody RegistroUsuarioRequest request);
}