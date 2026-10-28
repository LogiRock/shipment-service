package com.logirock.shipment_service.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logirock.shipment_service.dto.ClienteRequestDto;
import com.logirock.shipment_service.dto.ClienteResponseDto;
import com.logirock.shipment_service.dto.ErrorResponseDto;
import com.logirock.shipment_service.dto.RegistroClienteRequestDto;
import com.logirock.shipment_service.services.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Registro, consulta, actualización y eliminación de clientes")
public class ClienteController {

        private final ClienteService clienteService;

        // @PostMapping
        // @Operation(summary = "Registrar cliente", description = "Crea un cliente con nombre, email y teléfono únicos.")
        // @ApiResponses({
        //                 @ApiResponse(responseCode = "201", description = "Cliente registrado"),
        //                 @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
        //                 @ApiResponse(responseCode = "409", description = "Email o teléfono ya registrado", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        // })
        // public ResponseEntity<ClienteResponseDto> registrarCliente(@Valid @RequestBody ClienteRequestDto request) {
        //         ClienteResponseDto response = clienteService.registrarCliente(request);
        //         return ResponseEntity.status(HttpStatus.CREATED).body(response);
        // }

        //nuevo registrar
        @PostMapping
        @Operation(summary = "Registrar cliente", description = "Crea un cliente y su cuenta de acceso.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Cliente registrado"),
                        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                        @ApiResponse(responseCode = "409", description = "Email o teléfono ya registrado")
        })
        public ResponseEntity<ClienteResponseDto> registrarCliente(
                        @Valid @RequestBody RegistroClienteRequestDto request) {

                ClienteResponseDto response = clienteService.registrarCliente(request);

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Consultar cliente", description = "Busca un cliente por su UUID.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
                        @ApiResponse(responseCode = "404", description = "Cliente inexistente", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        public ResponseEntity<ClienteResponseDto> obtenerCliente(
                        @Parameter(description = "UUID del cliente", required = true) @PathVariable UUID id) {
                ClienteResponseDto response = clienteService.obtenerCliente(id);
                return ResponseEntity.ok(response);
        }

        @PutMapping("/{id}")
        @Operation(summary = "Modificar cliente", description = "Actualiza los datos de un cliente existente.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Cliente actualizado"),
                        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
                        @ApiResponse(responseCode = "404", description = "Cliente inexistente", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
                        @ApiResponse(responseCode = "409", description = "Email ya registrado por otro cliente", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        public ResponseEntity<ClienteResponseDto> modificarCliente(
                        @Parameter(description = "UUID del cliente", required = true) @PathVariable UUID id,
                        @Valid @RequestBody ClienteRequestDto request) {
                ClienteResponseDto response = clienteService.modificarCliente(id, request);
                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Eliminar cliente", description = "Elimina un cliente existente.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Cliente eliminado"),
                        @ApiResponse(responseCode = "404", description = "Cliente inexistente", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        public ResponseEntity<Void> eliminarCliente(
                        @Parameter(description = "UUID del cliente", required = true) @PathVariable UUID id) {
                clienteService.eliminarCliente(id);
                return ResponseEntity.noContent().build();
        }
}
