# Multi-stage Dockerfile for the lms-backend
# Stage 1: build with Maven
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace/app
COPY pom.xml .
COPY src ./src
# Use Maven to package the application (skip tests for faster builds in CI)
RUN mvn -DskipTests package -DskipTests -e

# Stage 2: runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Copy the built jar (adjust artifact name if your pom changes)
COPY --from=build /workspace/app/target/lms-backend-1.0-SNAPSHOT.jar ./app.jar

# Recommended environment variables (set at runtime)
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar"]
