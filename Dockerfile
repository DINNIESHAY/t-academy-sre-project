# Stage 1: Build
FROM eclipse-temurin:27_35-jdk-alpine AS build
WORKDIR /app

# Copy maven files and download dependencies
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

# Copy source code and build
COPY src ./src
RUN ./mvnw -B clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:27_35-jre-alpine
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
