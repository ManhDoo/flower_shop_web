# Use official OpenJDK image
FROM openjdk:17
WORKDIR /app

# Copy jar file
COPY target/FlowerShop-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
