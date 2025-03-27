# Giai đoạn 1: Build ứng dụng Spring Boot
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
# Copy toàn bộ mã nguồn vào image
COPY . .
# Chạy lệnh build để tạo file .jar trong thư mục target
RUN mvn clean package -DskipTests

# Giai đoạn 2: Chạy ứng dụng
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copy file .jar từ giai đoạn build
COPY --from=build /app/target/FlowerShop-0.0.1-SNAPSHOT.jar app.jar
# Expose port
EXPOSE 8080
# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]