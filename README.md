# Shipment Service

Microservicio REST de LogiTrack para administrar clientes, envíos y repartidores. También permite asignar repartidores, actualizar el estado de los envíos y consultar el historial de esos cambios.

## Requisitos

- JDK 25.
- MySQL disponible localmente o en un servidor accesible.
- Git para clonar el repositorio.
- PowerShell en Windows para los comandos de esta guía. El repositorio incluye Maven Wrapper, así que no hace falta instalar Maven por separado.

La primera ejecución puede necesitar conexión a Internet para descargar Maven y dependencias. No se necesita Docker.

## Configuración y ejecución

1. Clonar el repositorio y abrir la carpeta:

   ```powershell
   git clone https://github.com/LogiRock/shipment-service.git
   cd shipment-service
   ```

2. Crear en MySQL una base vacía, por ejemplo `shipment_service`.

3. Configurar las variables antes de iniciar la aplicación. Spring Boot no carga automáticamente el archivo `.env`; `.env.example` sirve como referencia. En PowerShell:

   ```powershell
   $env:DB_SUBPROTOCOL = "mysql"
   $env:DB_HOST = "localhost"
   $env:DB_PORT = "3306"
   $env:DB_DATABASE_NAME = "shipment_service"
   $env:DB_USERNAME = "<usuario_local>"
   $env:DB_PASSWORD = "<contraseña_local>"
   ```

   Los valores se conservan mientras siga abierta esa sesión. En IntelliJ IDEA, agregar los mismos nombres y valores en **Run → Edit Configurations → Environment variables** para la configuración de `ShipmentServiceApplication`.

4. Iniciar el servicio:

   ```powershell
   java -version
   .\mvnw.cmd spring-boot:run
   ```

   La aplicación se inicia en `http://localhost:8080`. Hibernate actualiza el esquema al arrancar (`spring.jpa.hibernate.ddl-auto=update`).

También se puede compilar y ejecutar el JAR:

```powershell
.\mvnw.cmd clean package
java -jar .\target\shipment-service-0.0.1-SNAPSHOT.jar
```

## Variables de entorno

| Variable | Uso | Ejemplo local |
| --- | --- | --- |
| `DB_SUBPROTOCOL` | Motor de la URL JDBC | `mysql` |
| `DB_HOST` | Servidor MySQL | `localhost` |
| `DB_PORT` | Puerto MySQL | `3306` |
| `DB_DATABASE_NAME` | Base de datos | `shipment_service` |
| `DB_USERNAME` | Usuario local de la base | `<usuario_local>` |
| `DB_PASSWORD` | Contraseña local de la base | `<contraseña_local>` |

No guardar contraseñas ni secretos reales en el repositorio. En esta revisión se quitó `.env` del índice de Git sin borrar la copia local; `.gitignore` lo excluye de futuros commits. `.env.example` contiene solo nombres de variables y valores ficticios. Si el archivo versionado anteriormente incluía credenciales reales, deben rotarse: quitarlo del commit actual no elimina las versiones históricas del repositorio.

## Swagger / OpenAPI

Con la aplicación iniciada y conectada a MySQL:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Especificación OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Swagger permite ver los parámetros, cuerpos, respuestas y probar las operaciones. Los identificadores son UUID y los cuerpos usan JSON con `Content-Type: application/json`.

## API REST

### Clientes — `/api/v1/clientes`

| Método | Ruta | Resultado principal |
| --- | --- | --- |
| `POST` | `/api/v1/clientes` | Registra y devuelve un cliente (`201`). |
| `GET` | `/api/v1/clientes/{id}` | Devuelve el cliente (`200`). |
| `PUT` | `/api/v1/clientes/{id}` | Actualiza y devuelve el cliente (`200`). |
| `DELETE` | `/api/v1/clientes/{id}` | Elimina el cliente (`204`). |

Crear cliente:

```json
{
  "nombre": "Ana Pérez",
  "email": "ana@example.com",
  "telefono": "+5491100000000"
}
```

Nombre, email y teléfono son obligatorios; el email debe tener formato válido. El email y el teléfono deben ser únicos. Una solicitud inválida responde `400`, una duplicada `409` y un UUID inexistente `404`.

### Repartidores — `/api/v1/repartidores`

| Método | Ruta | Resultado principal |
| --- | --- | --- |
| `POST` | `/api/v1/repartidores` | Registra y devuelve un repartidor (`201`). |
| `GET` | `/api/v1/repartidores` | Devuelve todos los repartidores (`200`). |
| `GET` | `/api/v1/repartidores/{id}` | Devuelve un repartidor (`200`). |
| `PUT` | `/api/v1/repartidores/{id}` | Actualiza y devuelve el repartidor (`200`). |
| `DELETE` | `/api/v1/repartidores/{id}` | Elimina el repartidor (`204`). |

Crear repartidor:

```json
{
  "nombre": "Juan López",
  "tipoVehiculo": "Motocicleta",
  "estado": "ACTIVO"
}
```

`estado` admite `ACTIVO` o `INACTIVO`. Nombre, vehículo y estado son obligatorios. Una solicitud inválida responde `400`; un UUID inexistente responde `404`.

### Envíos — `/api/envios`

| Método | Ruta | Resultado principal |
| --- | --- | --- |
| `POST` | `/api/envios` | Crea un envío en estado `PENDIENTE` y genera su código de seguimiento (`201`). |
| `GET` | `/api/envios` | Lista envíos como DTOs (`200`). |
| `GET` | `/api/envios/{id}` | Consulta por UUID (`200`, o `404` si no existe). |
| `GET` | `/api/envios/seguimiento/{codigo}` | Consulta por código de seguimiento (`200`, o `404`). |
| `GET` | `/api/envios/estado/{estado}` | Filtra por estado (`200`; `400` si el estado no es válido). |
| `PUT` | `/api/envios/{id}` | Actualiza peso, dimensiones, direcciones y cliente (`200`). |
| `DELETE` | `/api/envios/{id}` | Elimina físicamente el envío (`204`). |
| `PUT` | `/api/envios/{envioId}/repartidor/{repartidorId}` | Asigna un repartidor (`200`). |
| `POST` | `/api/envios/{id}/cancelacion` | Cancela un envío pendiente (`200`). |
| `PATCH` | `/api/envios/{id}/estado` | Cambia el estado según el ciclo de vida (`200`). |
| `GET` | `/api/envios/{id}/historial` | Consulta los cambios de estado de un envío (`200`). |

Crear o actualizar envío:

```json
{
  "peso": 2.5,
  "ancho": 10.0,
  "alto": 20.0,
  "largo": 30.0,
  "origen": {
    "calle": "San Martín",
    "numero": "123",
    "ciudad": "Córdoba",
    "codigoPostal": "5000"
  },
  "destino": {
    "calle": "Belgrano",
    "numero": "456",
    "ciudad": "Rosario",
    "codigoPostal": "2000"
  },
  "clienteId": "2e0d43cc-4fa3-4dc8-9b0d-67f57bb3b001"
}
```

Las dimensiones y el peso deben ser mayores que cero. Origen, destino y un UUID de cliente existente son obligatorios. El código de seguimiento y el estado inicial se generan en el servicio.

La respuesta de envío contiene `id`, `codigoSeguimiento`, dimensiones, `estado`, `clienteId`, `clienteNombre`, `origen`, `destino` y `repartidorId`. `repartidorId` es `null` hasta que se asigne un repartidor.

#### Asignar un repartidor

Ejemplo: `PUT /api/envios/9b715c47-1a23-4b70-a146-fbf0ff47b001/repartidor/7f7a2b5b-51aa-4ce8-9c6e-345aaabf0d7b`. No requiere cuerpo. El envío debe estar `PENDIENTE`, el repartidor debe existir y estar `ACTIVO`, y un envío no puede cambiar a un repartidor distinto una vez asignado. La respuesta incluye el UUID del repartidor. Los IDs inexistentes responden `404`; las reglas incompatibles, `409`.

#### Cambiar el estado

`PATCH /api/envios/{id}/estado` recibe:

```json
{
  "estado": "EN_RUTA"
}
```

Transiciones admitidas:

| Estado actual | Estado solicitado | Condición |
| --- | --- | --- |
| `PENDIENTE` | `EN_RUTA` | Debe haber un repartidor asignado. |
| `PENDIENTE` | `CANCELADO` | No requiere repartidor. |
| `EN_RUTA` | `ENTREGADO` | El envío debe estar en ruta. |

Las demás transiciones responden `409`. El cambio se persiste y se agrega al historial. La operación `POST /api/envios/{id}/cancelacion` aplica la misma regla que `PENDIENTE → CANCELADO`.

El historial individual devuelve los registros del envío ordenados del más reciente al más antiguo. Un envío recién creado todavía no tiene registros: se guarda cada transición posterior.

### Historial global — `/api/historial`

| Método | Ruta | Resultado principal |
| --- | --- | --- |
| `GET` | `/api/historial` | Lista los registros de historial de todos los envíos (`200`). |

## Errores

Las respuestas de error usan el formato `timestamp`, `status`, `error`, `message` y `path`. En errores de validación puede incluirse `validationErrors`.

| Código HTTP | Significado habitual |
| --- | --- |
| `400` | Cuerpo inválido, validación fallida o valor de ruta que no se puede convertir. |
| `404` | No existe el cliente, envío o repartidor solicitado. |
| `409` | Conflicto con una regla de negocio o transición de estado. |
| `500` | Error interno no contemplado. |

## Ejecutar las pruebas

Las pruebas usan una base H2 en memoria configurada en `src/test/resources/application.properties`, así que no requieren MySQL ni modifican la base local:

```powershell
.\mvnw.cmd test
```

La suite incluye pruebas unitarias de servicios, controladores y manejo de errores, pruebas de repositorio y persistencia, y una prueba de inicio del contexto Spring.

## Organización y decisiones técnicas

- `controllers`: endpoints HTTP y validación de solicitudes.
- `services` / `services.impl`: reglas y operaciones del negocio.
- `repositories`: persistencia con Spring Data JPA.
- `models`: entidades; `Direccion` es un objeto embebido.
- `dto`: contratos de entrada y salida de la API; las respuestas no exponen directamente la mayoría de las entidades.
- `enums`: estados de envíos y repartidores.
- `exceptions`: formato centralizado de respuestas de error.
- Los IDs son UUID. El envío tiene un cliente obligatorio y hasta un repartidor; un repartidor puede asociarse a varios envíos.
- Hibernate actualiza el esquema con `ddl-auto=update`; las migraciones no están configuradas.
- La configuración actual de `pom.xml` usa Spring Boot `4.1.1` y Java `25`. La consigna del Sprint compartida anteriormente indica Spring Boot `3.x`; el equipo debería confirmar esa diferencia antes de la entrega.
