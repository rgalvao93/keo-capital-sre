FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY code/traffic-simulator code/traffic-simulator
RUN mvn clean package -pl code/traffic-simulator -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/code/traffic-simulator/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-javaagent:/app/dd-java-agent.jar", "-jar", "app.jar"]