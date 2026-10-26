
package com.logirock.shipment_service.controllers;

import com.logirock.shipment_service.dto.EnvioRequestDto;
import com.logirock.shipment_service.dto.EnvioResponseDto;
import com.logirock.shipment_service.dto.ErrorResponseDto;
import com.logirock.shipment_service.dto.EstadoEnvioRequestDto;
import com.logirock.shipment_service.dto.HistorialEstadoEnvioResponseDto;
import com.logirock.shipment_service.enums.EstadoEnvio;
import com.logirock.shipment_service.models.Cliente;
import com.logirock.shipment_service.models.Envio;
import com.logirock.shipment_service.services.EnvioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/envios")
@RequiredArgsConstructor
@Tag(name = "Envíos", description = "Creación, seguimiento, consulta y cancelación de envíos")
public class EnvioController {

        private final EnvioService envioService;

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        @Operation(summary = "Crear envío", description = "Registra un envío. El código de seguimiento se genera automáticamente.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Envío creado"),
                        @ApiResponse(responseCode = "400", description = "Peso o dimensiones inválidos", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
                        @ApiResponse(responseCode = "404", description = "Cliente inexistente", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
                        @ApiResponse(responseCode = "500", description = "No fue posible guardar el envío", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        public EnvioResponseDto crear(
                        @Valid @RequestBody EnvioRequestDto request) {

                Envio envio = new Envio();

                envio.setPeso(request.getPeso());
                envio.setAncho(request.getAncho());
                envio.setAlto(request.getAlto());
                envio.setLargo(request.getLargo());
                envio.setOrigen(request.getOrigen());
                envio.setDestino(request.getDestino());

                Envio creado = envioService.crear(envio, request.getClienteId());

                return convertirAResponse(creado);
        }

        @GetMapping
        @Operation(summary = "Listar envíos")
        @ApiResponse(responseCode = "200", description = "Listado de envíos")
        public List<EnvioResponseDto> listarTodos() {
                return envioService.listarTodos();
        }

        @GetMapping("/{id}")
        @Operation(summary = "Consultar envío por UUID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Envío encontrado"),
                        @ApiResponse(responseCode = "404", description = "Envío inexistente", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        public EnvioResponseDto buscarPorId(
                        @Parameter(description = "UUID del envío", required = true) @PathVariable UUID id) {
                return convertirAResponse(envioService.buscarPorId(id));
        }

        @GetMapping("/seguimiento/{codigo}")
        @Operation(summary = "Consultar envío por código de seguimiento")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Envío encontrado"),
                        @ApiResponse(responseCode = "404", description = "No existe un envío con ese código", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        public EnvioResponseDto buscarPorCodigoSeguimiento(
                        @Parameter(description = "Código de seguimiento generado para el envío", example = "ENV-A1B2C3D4", required = true) @PathVariable String codigo) {
                return convertirAResponse(
                                envioService.buscarPorCodigoSeguimiento(codigo));
        }

        @PutMapping("/{id}")
        @Operation(summary = "Actualizar envío", description = "Actualiza el peso, las dimensiones, las direcciones y el cliente del envío.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Envío actualizado"),
                        @ApiResponse(responseCode = "400", description = "Peso o dimensiones inválidos", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
                        @ApiResponse(responseCode = "404", description = "Envío o cliente inexistente", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        public EnvioResponseDto actualizar(
                        @Parameter(description = "UUID del envío", required = true) @PathVariable UUID id,
                        @Valid @RequestBody EnvioRequestDto request) {

                Envio datos = new Envio();
                datos.setPeso(request.getPeso());
                datos.setAncho(request.getAncho());
                datos.setAlto(request.getAlto());
                datos.setLargo(request.getLargo());
                datos.setOrigen(request.getOrigen());
                datos.setDestino(request.getDestino());

                Cliente cliente = new Cliente();
                cliente.setId(request.getClienteId());
                datos.setCliente(cliente);

                return convertirAResponse(envioService.actualizar(id, datos));
        }

        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        @Operation(summary = "Eliminar envío", description = "Elimina físicamente un envío.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Envío eliminado"),
                        @ApiResponse(responseCode = "404", description = "Envío inexistente", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        public void eliminar(@Parameter(description = "UUID del envío", required = true) @PathVariable UUID id) {
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
                                envio.getEstado(),

                                envio.getCourierId(),
                                envio.getCliente().getId(),
                                envio.getCliente().getNombre(),
                                envio.getOrigen(),
                                envio.getDestino());
        }

        @GetMapping("/estado/{estado}")
        @Operation(summary = "Filtrar envíos por estado", description = "Estados permitidos: PENDIENTE, EN_RUTA, ENTREGADO y CANCELADO.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Envíos del estado solicitado"),
                        @ApiResponse(responseCode = "400", description = "Estado no reconocido", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        public List<EnvioResponseDto> buscarPorEstado(
                        @Parameter(description = "Estado del envío", example = "PENDIENTE", required = true) @PathVariable EstadoEnvio estado) {

                return envioService.findByEstado(estado)
                                .stream()
                                .map(this::convertirAResponse)
                                .toList();
        }

        @PostMapping("/{id}/cancelacion")
        @Operation(summary = "Cancelar envío", description = "Cancela un envío únicamente si su estado actual es PENDIENTE.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Envío cancelado; el estado de respuesta es CANCELADO"),
                        @ApiResponse(responseCode = "404", description = "Envío inexistente", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
                        @ApiResponse(responseCode = "409", description = "El estado actual no permite cancelar el envío", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        public ResponseEntity<EnvioResponseDto> cancelarEnvio(
                        @Parameter(description = "UUID del envío", required = true) @PathVariable UUID id) {
                return ResponseEntity.ok(envioService.cancelarEnvio(id));
        }

        @PutMapping("/{envioId}/courier/{courierId}")
        @Operation(summary = "Asignar courier", description = "Asigna el ID lógico de un courier a un envío PENDIENTE.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Courier asignado", content = @Content(schema = @Schema(implementation = EnvioResponseDto.class), examples = @ExampleObject(value = "{\"id\":\"9b715c47-1a23-4b70-a146-fbf0ff47b001\",\"codigoSeguimiento\":\"ENV-A1B2C3D4\",\"peso\":2.5,\"ancho\":10.0,\"alto\":20.0,\"largo\":30.0,\"estado\":\"PENDIENTE\",\"courierId\":123}"))),
                        @ApiResponse(responseCode = "404", description = "Envío inexistente", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
                        @ApiResponse(responseCode = "409", description = "El envío no está pendiente o ya tiene otro courier", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        public ResponseEntity<EnvioResponseDto> asignarCourier(
                        @Parameter(description = "UUID del envío", required = true) @PathVariable UUID envioId,
                        @Parameter(description = "Identificador Long del courier", required = true) @PathVariable Long courierId) {
                Envio envio = envioService.asignarCourier(envioId, courierId);
                return ResponseEntity.ok(convertirAResponse(envio));
        }

        @PatchMapping("/{id}/estado")
        @Operation(summary = "Cambiar estado del envío", description = "Transiciones permitidas: PENDIENTE a EN_RUTA (requiere courier asignado), PENDIENTE a CANCELADO y EN_RUTA a ENTREGADO.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Estado actualizado y cambio registrado en el historial"),
                        @ApiResponse(responseCode = "400", description = "Falta el estado o el valor no es válido", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
                        @ApiResponse(responseCode = "404", description = "Envío inexistente", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
                        @ApiResponse(responseCode = "409", description = "Transición no permitida o falta asignar un courier antes de iniciar el recorrido", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        public EnvioResponseDto cambiarEstado(
                        @Parameter(description = "UUID del envío", required = true) @PathVariable UUID id,
                        @Valid @RequestBody EstadoEnvioRequestDto request) {

                return convertirAResponse(
                                envioService.cambiarEstado(id, request.getEstado()));
        }

        @GetMapping("/{id}/historial")
        @Operation(summary = "Consultar historial de un envío", description = "Devuelve los cambios de estado registrados, del más reciente al más antiguo.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Historial del envío"),
                        @ApiResponse(responseCode = "404", description = "Envío inexistente", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        public List<HistorialEstadoEnvioResponseDto> consultarHistorial(
                        @Parameter(description = "UUID del envío", required = true) @PathVariable UUID id) {

                return envioService.consultarHistorial(id)
                                .stream()
                                .map(historial -> new HistorialEstadoEnvioResponseDto(
                                                historial.getId(),
                                                historial.getEstadoAnterior(),
                                                historial.getEstadoNuevo(),
                                                historial.getFechaCambio()))
                                .toList();
        }

}
