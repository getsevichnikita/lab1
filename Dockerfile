FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

ENV PORT=8080
ENV DB_HOST=db
ENV DB_PORT=5432
ENV DB_NAME=library
ENV DB_USER=postgres
ENV DB_PASSWORD=postgres

EXPOSE ${PORT}

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT}/actuator/health || exit 1

CMD java -jar app.jar \
  --server.port=${PORT} \
  --spring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME} \
  --spring.datasource.username=${DB_USER} \
  --spring.datasource.password=${DB_PASSWORD}