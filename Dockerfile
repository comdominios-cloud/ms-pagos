# ---------- build ----------
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- runtime ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

# Puerto interno del contenedor. Se publica como 9002 en la VM de produccion.
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
