
package com.logirock.shipment_service.controllers;

import com.logirock.shipment_service.dto.EnvioRequestDto;
import com.logirock.shipment_service.dto.EnvioResponseDto;
import com.logirock.shipment_service.models.Envio;
import com.logirock.shipment_service.services.EnvioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/envios")
@RequiredArgsConstructor
public class EnvioController {

    private final EnvioService envioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnvioResponseDto crear(
            @Valid @RequestBody EnvioRequestDto request) {

        Envio envio = new Envio();
        
        envio.setPeso(request.getPeso());
        envio.setAncho(request.getAncho());
        envio.setAlto(request.getAlto());
        envio.setLargo(request.getLargo());

        Envio creado = envioService.crear(envio);

        return convertirAResponse(creado);
    }

    @GetMapping
    public List<EnvioResponseDto> listarTodos() {
        return envioService.listarTodos()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public EnvioResponseDto buscarPorId(@PathVariable UUID id) {
        return convertirAResponse(envioService.buscarPorId(id));
    }

    @GetMapping("/seguimiento/{codigo}")
    public EnvioResponseDto buscarPorCodigoSeguimiento(
            @PathVariable String codigo) {
        return convertirAResponse(
                envioService.buscarPorCodigoSeguimiento(codigo));
    }

    @PutMapping("/{id}")
    public EnvioResponseDto actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody EnvioRequestDto request) {

        Envio datos = new Envio();
        datos.setPeso(request.getPeso());
        datos.setAncho(request.getAncho());
        datos.setAlto(request.getAlto());
        datos.setLargo(request.getLargo());

        return convertirAResponse(envioService.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        envioService.eliminar(id);
    }

    private EnvioResponseDto convertirAResponse(Envio envio) {
        return new EnvioResponseDto(
                envio.getId(),
                envio.getCodigoSeguimiento(),
                envio.getPeso(),
                envio.getAncho(),
                envio.getAlto(),
                envio.getLargo(),
                envio.getEstado()
        );
    }
}
