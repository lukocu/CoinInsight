# Use the official OpenJDK image as a base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY build/libs/UserService-0.0.1-SNAPSHOT.jar app.jar

# Expose the port that the application will run on
EXPOSE 7575

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]
