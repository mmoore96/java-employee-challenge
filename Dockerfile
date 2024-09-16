# ARM-compatible JDK 11 image
FROM openjdk:11-jdk-slim AS build

# Install Gradle manually
RUN apt-get update && apt-get install -y wget unzip && \
    wget https://services.gradle.org/distributions/gradle-7.6-bin.zip -O /tmp/gradle.zip && \
    unzip /tmp/gradle.zip -d /opt && \
    ln -s /opt/gradle-7.6/bin/gradle /usr/bin/gradle && \
    rm /tmp/gradle.zip

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle wrapper and related files first to leverage caching
COPY gradlew gradlew.bat build.gradle settings.gradle /app/
COPY gradle /app/gradle/

# Download dependencies before copying the entire project
RUN ./gradlew --no-daemon clean build || true

# Copy the rest of the project files
COPY . /app/

# Run the tests and stop the build if they fail
RUN ./gradlew test || false

# Keep the container running
CMD tail -f /dev/null

# Build the project
RUN ./gradlew --no-daemon build

# Use an OpenJDK runtime image to run the application
FROM openjdk:11-jre-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/build/libs/*.jar /app/app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
