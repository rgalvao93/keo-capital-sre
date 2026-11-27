FROM maven:3.9-eclipse-temurin-26 AS builder
WORKDIR /app
COPY pom.xml .
COPY code code
RUN mvn clean package -pl code/credit-service -am -DskipTests

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /app/code/credit-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-javaagent:/app/dd-java-agent.jar", "-jar", "app.jar"]