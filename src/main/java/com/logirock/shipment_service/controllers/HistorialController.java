package com.logirock.shipment_service.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logirock.shipment_service.dto.EnvioResponseDto;
import com.logirock.shipment_service.models.HistorialEstadoEnvio;
import com.logirock.shipment_service.repositories.HistorialEstadoEnvioRepository;
import com.logirock.shipment_service.services.EnvioService;

import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("/api/historial")
@RequiredArgsConstructor 
public class HistorialController {

    private final HistorialEstadoEnvioRepository historialEstadoEnvioRepository;

    @GetMapping
    public List<HistorialEstadoEnvio> listarTodos() {
        return historialEstadoEnvioRepository.findAll();
    }
}
