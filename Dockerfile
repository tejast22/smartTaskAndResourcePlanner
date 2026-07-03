# Step 1: Build the Maven application inside a Java image
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run the compiled package jar file
FROM openjdk:17-jdk-slim
COPY --from=build /target/backendsystem-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]