# Step 1: Build using Maven with Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run using Eclipse Temurin Java 21 JRE
FROM eclipse-temurin:21-jre-jammy
COPY --from=build /target/backendsystem-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
