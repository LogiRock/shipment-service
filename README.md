# shipment-service

Microservicio REST de LogiTrack para gestionar clientes, envíos y repartidores. Forma parte del Sprint 1 de Arquitecturas Avanzadas de Software y está implementado con Java, Spring Boot, Spring Data JPA y MySQL.

## Requisitos

- JDK 25 instalado y disponible en `PATH` (`java -version`).
- Git para clonar el repositorio.
- MySQL accesible desde el equipo, con una base de datos creada para el servicio.
- PowerShell en Windows para los comandos de esta guía. El repositorio incluye Maven Wrapper (`mvnw.cmd`), por lo que no hace falta instalar Maven por separado. La primera ejecución puede necesitar Internet para descargar Maven y dependencias.

## Preparar el entorno

1. Clonar el repositorio y entrar a la carpeta:

   ```powershell
   git clone <URL_DEL_REPOSITORIO>
   cd shipment-service
   ```

2. Crear una base de datos vacía en MySQL, por ejemplo `shipment_service`. El esquema se genera/actualiza al iniciar la aplicación mediante Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

3. Usar `.env.example` como referencia y definir estas variables en la sesión de PowerShell. Spring Boot **no carga automáticamente** un archivo `.env`; las variables deben estar en el entorno del proceso que inicia Java.

   ```powershell
   $env:DB_SUBPROTOCOL = "mysql"
   $env:DB_HOST = "localhost"
   $env:DB_PORT = "3306"
   $env:DB_DATABASE_NAME = "shipment_service"
   $env:DB_USERNAME = "<usuario_local>"
   $env:DB_PASSWORD = "<contraseña_local>"
   ```

   Estos valores duran mientras siga abierta esa sesión de PowerShell. En IntelliJ IDEA, se pueden cargar los mismos nombres y valores en la configuración de ejecución de `ShipmentServiceApplication`.

4. Verificar Java e iniciar el servicio:

   ```powershell
   java -version
   .\mvnw.cmd spring-boot:run
   ```

   La aplicación queda disponible en `http://localhost:8080`. Para compilar el artefacto ejecutable:

   ```powershell
   .\mvnw.cmd clean package
   java -jar .\target\shipment-service-0.0.1-SNAPSHOT.jar
   ```

Con la aplicación iniciada, Swagger UI está en `http://localhost:8080/swagger-ui.html` y el documento OpenAPI JSON en `http://localhost:8080/v3/api-docs`. No se necesita Docker para usar Swagger; sí se requiere que el servicio pueda conectarse a MySQL durante el inicio.

## Variables de entorno

| Variable | Uso | Ejemplo local |
| --- | --- | --- |
| `DB_SUBPROTOCOL` | Motor usado en la URL JDBC | `mysql` |
| `DB_HOST` | Servidor MySQL | `localhost` |
| `DB_PORT` | Puerto MySQL | `3306` |
| `DB_DATABASE_NAME` | Base de datos del servicio | `shipment_service` |
| `DB_USERNAME` | Usuario de MySQL | `<usuario_local>` |
| `DB_PASSWORD` | Contraseña del usuario de MySQL | `<contraseña_local>` |

No guardar credenciales reales en el README, `.env.example`, el código ni en commits. El archivo `.env` está actualmente bajo seguimiento de Git en este repositorio: agregarlo a `.gitignore` evita nuevos archivos locales sin seguimiento, pero no deja de versionar un archivo que ya está seguido. Antes de compartir cambios, el equipo debe revisar ese archivo y retirar credenciales reales del control de versiones y, si corresponde, rotarlas.

## Ejecutar pruebas

Ejecutar toda la suite:

```powershell
.\mvnw.cmd test
```

La prueba de contexto (`ShipmentServiceApplicationTests`) inicia Spring y requiere que MySQL y las variables de entorno estén configurados. Para correr una prueba unitaria puntual, por ejemplo:

```powershell
.\mvnw.cmd "-Dtest=EnvioServiceImplTest" test
```

Las pruebas existentes usan JUnit 5 y Mockito para servicios, controladores, validaciones y manejo de errores. La prueba de contexto valida el inicio de la aplicación.

## API disponible

Todos los cuerpos son JSON. Los identificadores son UUID. Los endpoints de clientes y repartidores usan el prefijo `/api/v1`; los de envíos usan `/api`.

### Clientes — `/api/v1/clientes`

| Método | Ruta | Descripción |
| --- | --- | --- |
| `POST` | `/api/v1/clientes` | Registrar cliente. Requiere `nombre`, `email` y `telefono`; responde `201`. |
| `GET` | `/api/v1/clientes/{id}` | Consultar por UUID. |
| `PUT` | `/api/v1/clientes/{id}` | Modificar nombre, email y teléfono. |
| `DELETE` | `/api/v1/clientes/{id}` | Eliminar; responde `204`. |

Ejemplo de alta:

```json
{
  "nombre": "Ana Pérez",
  "email": "ana@example.com",
  "telefono": "+5491100000000"
}
```

### Envíos — `/api/envios`

| Método | Ruta | Descripción |
| --- | --- | --- |
| `POST` | `/api/envios` | Crear envío; responde `201`. El código de seguimiento se genera en el servicio. |
| `GET` | `/api/envios` | Listar envíos. |
| `GET` | `/api/envios/{id}` | Consultar por UUID. |
| `GET` | `/api/envios/seguimiento/{codigo}` | Consultar por código de seguimiento. |
| `GET` | `/api/envios/estado/{estado}` | Filtrar por `PENDIENTE`, `EN_RUTA`, `ENTREGADO` o `CANCELADO`. |
| `PUT` | `/api/envios/{envioId}/repartidor/{repartidorId}` | Asignar un repartidor activo a un envío pendiente; responde con el envío y el UUID del repartidor. |
| `PUT` | `/api/envios/{id}` | Actualizar dimensiones y peso. |
| `DELETE` | `/api/envios/{id}` | Eliminar físicamente; responde `204`. |
| `POST` | `/api/envios/{id}/cancelacion` | Cancelar solo si el estado actual es `PENDIENTE`; responde con el envío actualizado. |

Ejemplo del cuerpo de creación/actualización:

```json
{
  "peso": 2.5,
  "ancho": 10.0,
  "alto": 20.0,
  "largo": 30.0
}
```

La regla de cancelación es `PENDIENTE → CANCELADO`. Si el envío no existe, se responde `404`. El servicio rechaza otros estados con `IllegalStateException`, pero el manejador global actual no define un caso específico para esa excepción: cae en su respuesta genérica `500`. Conviene agregar el mapeo de negocio que acuerde el equipo (por ejemplo, `409` o `422`) para que el rechazo tenga un código HTTP específico. La entidad también contiene direcciones de origen y destino, pero el DTO/controlador actual no las recibe; el flujo de alta puede requerir completar esa parte del contrato/modelo para persistir envíos con la configuración actual de la base.

La asignación usa la relación muchos-a-uno `Envio → Repartidor`: un envío admite como máximo un repartidor y un repartidor puede estar vinculado a varios envíos. Solo se asignan envíos `PENDIENTE` y repartidores `ACTIVO`. Repetir la asignación al mismo repartidor es idempotente; intentar reemplazarlo por otro se rechaza con `409`.

### Repartidores — `/api/v1/repartidores`

| Método | Ruta | Descripción |
| --- | --- | --- |
| `POST` | `/api/v1/repartidores` | Registrar; responde `201`. |
| `GET` | `/api/v1/repartidores` | Listar. |
| `GET` | `/api/v1/repartidores/{id}` | Consultar por UUID. |
| `PUT` | `/api/v1/repartidores/{id}` | Modificar. |
| `DELETE` | `/api/v1/repartidores/{id}` | Eliminar; responde `204`. |

El DTO de repartidor requiere `nombre`, `tipoVehiculo` y `estado` (`ACTIVO` o `INACTIVO`).

## Errores

`GlobalExceptionHandler`, anotado con `@RestControllerAdvice`, centraliza respuestas de error. Entre los casos contemplados están `404` para entidades no encontradas, `400` para errores de validación, `409` para conflictos de reglas de negocio y `500` para excepciones no controladas. La estructura estándar es `timestamp`, `status`, `error`, `message` y `path`; los errores de validación pueden incluir `validationErrors`.

## Organización y decisiones técnicas

- `controllers`: contrato HTTP y validación de entrada.
- `services` / `services.impl`: operaciones y reglas del negocio.
- `repositories`: persistencia con Spring Data JPA.
- `models`: entidades JPA y objetos embebidos.
- `dto`: modelos de entrada y salida para la API.
- `enums`: estados del dominio.
- `exceptions`: manejo centralizado de errores.
- Los identificadores de entidades son UUID; `Auditable` aporta fechas de creación y última actualización.
- El `pom.xml` fija Spring Boot `4.1.1` y Java `25`; la consigna del Sprint solicita Spring Boot `3.x`, por lo que la versión de Spring requiere confirmación del equipo docente/equipo antes de la entrega.
- El estado inicial del envío es `PENDIENTE`. La cancelación está permitida únicamente antes de entrar en ruta.
- Hibernate mantiene el esquema con `update`; para entornos compartidos o productivos se debe acordar una estrategia de migraciones antes de cambiarlo.

## Alcance actual y siguientes pasos

El repositorio no incluye actualmente `docker-compose.yml` ni Dockerfile. La ejecución documentada usa MySQL instalado o accesible localmente. La documentación interactiva OpenAPI/Swagger se publica desde la misma aplicación. El historial de cambios de estado sigue pendiente; la asignación de repartidores está disponible mediante `PUT /api/envios/{envioId}/repartidor/{repartidorId}`.

