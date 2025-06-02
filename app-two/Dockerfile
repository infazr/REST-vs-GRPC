# Use a slim Java 17 image as the base
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the Maven/Gradle build file and source code
COPY pom.xml ./
COPY src ./src

# Copy the Maven wrapper (if using Maven)
COPY mvnw ./
COPY .mvn ./.mvn

# Build the application (assumes Maven; adjust for Gradle if needed)
RUN ./mvnw clean package -DskipTests

# Copy the built JAR file to the final location
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app-two-0.0.1-SNAPSHOT.jar.original

# Expose ports for REST (8080) and gRPC (9090)
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app-two-0.0.1-SNAPSHOT.jar.original"]