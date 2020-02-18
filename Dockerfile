FROM openjdk:11-jre-slim-sid

COPY build/libs/*.jar app.jar
EXPOSE 8080
CMD java -jar app.jar

