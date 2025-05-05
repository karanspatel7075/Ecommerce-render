# Use Jelastic Maven + OpenJDK 21 image as the base image
FROM jelastic/maven:3.9.5-openjdk-21 AS build

# Copy all project files into the container
COPY . .

# Run Maven to build the Spring Boot app (skip tests for now)
RUN mvn clean package -DskipTests


# Second stage: Use a smaller base image for running the application
FROM openjdk:21-slim

# Copy the JAR file from the build stage
COPY --from=build /target/Shopping_Cart-0.0.1-SNAPSHOT.jar /app/shopping_cart.jar

# Expose port 8080
EXPOSE 8080


# Run the JAR file with ENTRYPOINT
ENTRYPOINT ["java", "-jar", "/app/shopping_cart.jar"]