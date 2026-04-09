# Etapa 1: build
FROM gradle:8.5-jdk17 AS build

WORKDIR /app
COPY . .

RUN gradle build -x test

# Etapa 2: runtime
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]