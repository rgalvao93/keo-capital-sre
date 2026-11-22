FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY code/common code/common
COPY code/cross-border-fx code/cross-border-fx
RUN mvn clean package -pl code/cross-border-fx -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/code/cross-border-fx/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-javaagent:/app/dd-java-agent.jar", "-jar", "app.jar"]