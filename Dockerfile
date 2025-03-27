FROM maven:3.9.9-eclipse-temurin-17 AS build

COPY . /app
WORKDIR /app

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-slim
COPY --from=build /app/target/FlowerShop-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
