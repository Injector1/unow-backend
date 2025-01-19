FROM gradle:8.11.1-jdk21 AS builder

WORKDIR /app

COPY . /app

RUN gradle build -x test

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
