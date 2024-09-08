# Use an OpenJDK 17 base image
FROM openjdk:17-jdk

# Set the working directory inside the container
WORKDIR /app

# Define a build argument for the JAR file
ARG JAR_FILE=target/FleenFeen-0.0.1-SNAPSHOT.jar

# Copy the compiled Spring Boot JAR file into the container
COPY ${JAR_FILE} /app/fleen-feen.jar

# Expose the port that your Spring Boot application runs on
EXPOSE 8080

# Specify the command to run your Spring Boot application
ENTRYPOINT ["java", "-jar", "-Duser.timezone=Africa/Lagos", "fleen-feen.jar"]
