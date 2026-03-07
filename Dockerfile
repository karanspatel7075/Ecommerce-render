# ---------- Build Stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /build

# Copy only pom first for dependency caching
COPY pom.xml .

RUN mvn -B -q -e -C -T 1C dependency:go-offline

# Copy source
COPY src ./src

# Build application
RUN mvn clean package -DskipTests


# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy built jar
COPY --from=build /build/target/*.jar app.jar

# JVM optimization for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]



# Use Jelastic Maven + OpenJDK 21 image as the base image
# FROM jelastic/maven:3.9.5-openjdk-21 AS build

# Copy all project files into the container
# COPY . .

# Run Maven to build the Spring Boot app (skip tests for now)
# RUN mvn clean package -DskipTests


# Second stage: Use a smaller base image for running the application
# FROM openjdk:21-slim
# FROM eclipse-temurin:21-jre-jammy

# Copy the JAR file from the build stage
# COPY --from=build /target/Shopping_Cart-0.0.1-SNAPSHOT.jar /app/shopping_cart.jar
# COPY --from=build /app/target/Shopping_Cart-0.0.1-SNAPSHOT.jar /app/shopping_cart.jar
# COPY --from=build /app/target/*.jar /app/app.jar

# WORKDIR /app

# Expose port 8080
# EXPOSE 8080


# Run the JAR file with ENTRYPOINT
# ENTRYPOINT ["java", "-jar", "/app/shopping_cart.jar"]
# ENTRYPOINT ["java", "-jar", "/app/app.jar"]