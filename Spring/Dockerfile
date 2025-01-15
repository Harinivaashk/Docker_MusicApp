FROM openjdk:17-jdk-slim AS build
WORKDIR /app
RUN apt-get update && apt-get install -y maven
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src /app/src
RUN mvn clean install
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/music_system-0.0.1-SNAPSHOT.jar /app/music-backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/music-backend.jar"]
