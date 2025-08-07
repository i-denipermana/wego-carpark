# ---- Stage 1: Build ----
FROM maven:3.9.7-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven files first for better Docker cache usage
COPY pom.xml .
COPY src ./src

# Build the application (skip tests for speed; remove -DskipTests to run them)
RUN mvn clean package -DskipTests

# ---- Stage 2: Run ----
FROM openjdk:21-jdk-slim

# Set environment variables
ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS=""

# Set working directory
WORKDIR /app

# Copy built jar into container
COPY --from=build /app/target/*.jar app.jar

# Expose the app port (default Spring Boot port)
EXPOSE 8080

# Run the app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
