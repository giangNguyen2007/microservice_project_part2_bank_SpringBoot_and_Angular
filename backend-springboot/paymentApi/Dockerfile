# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app

# build context = project root
COPY pom.xml .
COPY sharedLibrary ./sharedLibrary
COPY paymentApi ./paymentApi

RUN ls -R /app

#// build the parent module first
RUN mvn clean install -N -DskipTests

WORKDIR /app/sharedLibrary

#// build sharedLibrary next
RUN mvn clean install -DskipTests

WORKDIR /app/paymentApi

#mvn -N -pl userApi clean package -DskipTests
RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine


WORKDIR /app

COPY --from=build /app/paymentApi/target/paymentApi-0.0.1-SNAPSHOT.jar app.jar
# ^ Copy the prebuilt JAR from host into container

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
# ^ Run the JAR directly