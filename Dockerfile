# Use an official Gradle image to build the application
FROM gradle:8.8-jdk17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the entire project to the container
COPY . .

# Build the application
RUN gradle build

# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /app/build/libs/*.jar /app/databroker-sync.jar

# Expose the port your application runs on (if applicable)
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "/app/databroker-sync.jar"]