# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory to /app
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . /app

COPY src/main/resources/serviceAccountKey.json /app

# Install any needed dependencies specified in pom.xml
RUN chmod +x mvnw && \
    ./mvnw dependency:go-offline

# Build the project
RUN ./mvnw package

# Expose port 8080 for the application
EXPOSE 8080

# Set the default command to run when the container starts
CMD ["java", "-jar", "./target/amogus-0.0.1-SNAPSHOT.jar"]
