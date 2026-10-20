# Etapa 1: compilación
FROM maven:3.10.0-eclipse-temurin-25-noble AS builder

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

COPY src ./src

RUN ./mvnw -B clean package -DskipTests

# Etapa 2: ejecución
FROM eclipse-temurin:25-jre

WORKDIR /app

RUN useradd --system --uid 1001 appuser

COPY --from=builder /app/target/*.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
