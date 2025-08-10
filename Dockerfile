# Stage 1: Build
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Maven wrapper & pom.xml trước
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Tải dependencies trước
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build jar bỏ qua test
RUN ./mvnw clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY --from=build /app/target/taskManager-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
