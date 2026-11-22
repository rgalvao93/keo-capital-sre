FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY code/common code/common
COPY code/travel-card-service code/travel-card-service
RUN mvn clean package -pl code/travel-card-service -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/code/travel-card-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-javaagent:/app/dd-java-agent.jar", "-jar", "app.jar"]