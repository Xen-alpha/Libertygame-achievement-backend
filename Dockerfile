FROM openjdk:17-ea-slim-buster
LABEL authors="Xenα"

ADD ./build/libs/LibertyAchievement-0.1.0-SNAPSHOT.jar /app.jar
EXPOSE 8080

CMD ["java", "-jar", "/app.jar"]