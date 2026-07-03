# Step 1: Build the Maven application using a maintained Maven/Java image
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run using Eclipse Temurin (the official OpenJDK replacement)
FROM eclipse-temurin:17-jre-jammy
COPY --from=build /target/backendsystem-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
