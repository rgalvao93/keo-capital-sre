FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY code/common code/common
COPY code/blockchain-mock code/blockchain-mock
RUN mvn clean package -pl code/blockchain-mock -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/code/blockchain-mock/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-javaagent:/app/dd-java-agent.jar", "-jar", "app.jar"]