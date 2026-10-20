package com.logirock.shipment_service.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logirock.shipment_service.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.logirock.shipment_service.models.HistorialEstadoEnvio;
import com.logirock.shipment_service.repositories.HistorialEstadoEnvioRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/historial")
@RequiredArgsConstructor
@Tag(name = "Historial", description = "Consulta global del historial de cambios de estado")
public class HistorialController {

    private final HistorialEstadoEnvioRepository historialEstadoEnvioRepository;

    @GetMapping
    @Operation(summary = "Listar todos los cambios de estado", description = "Devuelve los registros de historial de todos los envíos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado del historial"),
            @ApiResponse(responseCode = "500", description = "No fue posible consultar el historial", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public List<HistorialEstadoEnvio> listarTodos() {
        return historialEstadoEnvioRepository.findAll();
    }
}
