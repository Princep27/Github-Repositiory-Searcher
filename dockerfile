# Stage 1: Build Spring Boot application
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Generate executable JAR
RUN mvn clean package -DskipTests

# Stage 2: Run Spring Boot application
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]