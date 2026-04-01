# ---- Build Stage ----
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
# Download dependencies first (layer caching)
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package -DskipTests -q

# ---- Run Stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Create uploads directory
RUN mkdir -p uploads

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
