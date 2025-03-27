# Use official OpenJDK image
FROM openjdk:17
WORKDIR /app

# Copy jar file
COPY target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
