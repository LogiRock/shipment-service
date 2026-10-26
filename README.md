# Shipment Service

Microservicio REST de LogiTrack para administrar clientes y envíos. Mantiene el identificador lógico del courier asignado, actualiza el estado de los envíos y consulta el historial de esos cambios.

## Requisitos

- JDK 25.
- MySQL disponible localmente o en un servidor accesible.
- Git para clonar el repositorio.
- PowerShell en Windows para los comandos de esta guía. El repositorio incluye Maven Wrapper, así que no hace falta instalar Maven por separado.

La primera ejecución puede necesitar conexión a Internet para descargar Maven y dependencias. Para esta ejecución local manual no se necesita Docker; la consigna del Sprint también solicita los archivos Docker indicados en la sección de estado.

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
| `COURIER_SERVICE_URL` | URL base de Courier Service | `http://localhost:8082` |

No guardar contraseñas ni secretos reales en el repositorio. En esta revisión se quitó `.env` del índice de Git sin borrar la copia local; `.gitignore` lo excluye de futuros commits. `.env.example` contiene solo nombres de variables y valores ficticios. Si el archivo versionado anteriormente incluía credenciales reales, deben rotarse: quitarlo del commit actual no elimina las versiones históricas del repositorio.

Para ejecutar shipment fuera de Docker, Courier Service debe estar disponible en `http://localhost:8082` o se debe definir `COURIER_SERVICE_URL`. En Docker Compose se usa por defecto `http://host.docker.internal:8082`, por lo que Courier Service debe exponer ese puerto en el host.

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
| `PUT` | `/api/envios/{envioId}/courier/{courierId}` | Asigna el ID lógico de un courier (`200`). |
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

La respuesta de envío contiene `id`, `codigoSeguimiento`, dimensiones, `estado`, `clienteId`, `clienteNombre`, `origen`, `destino` y `courierId`. `courierId` es un UUID y permanece `null` hasta que se asigne un courier.

#### Asignar un courier

Ejemplo: `PUT /api/envios/9b715c47-1a23-4b70-a146-fbf0ff47b001/courier/1b6e8c5a-64f9-4e2a-ae1e-4e5da04533fd`. No requiere cuerpo. Shipment Service consulta `GET /api/v1/repartidores/{id}` en Courier Service y solo asigna el UUID si el courier existe y está `ACTIVO`. El envío debe estar `PENDIENTE` y no puede cambiar a otro courier una vez asignado. Si no existe responde `404`, si está inactivo `400`, y si Courier Service no está disponible `503`; en todos esos casos no se persiste la asignación.

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
| `PENDIENTE` | `EN_RUTA` | Debe haber un courier asignado. |
| `PENDIENTE` | `CANCELADO` | No requiere courier. |
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
| `404` | No existe el cliente o envío solicitado. |
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
- `enums`: estados de envíos.
- `exceptions`: formato centralizado de respuestas de error.
- Los clientes, envíos y couriers usan UUID. El envío tiene un cliente obligatorio y guarda opcionalmente el `courierId` como identificador lógico, sin relación de base de datos con Courier Service.
- Hibernate actualiza el esquema con `ddl-auto=update`; las migraciones no están configuradas.
- La configuración actual de `pom.xml` usa Spring Boot `4.1.1` y Java `25`. La consigna del Sprint compartida anteriormente indica Spring Boot `3.x`; el equipo debería confirmar esa diferencia antes de la entrega.
