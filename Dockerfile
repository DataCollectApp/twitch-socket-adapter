FROM maven:3.6.2-jdk-13 AS build

COPY settings.xml /usr/share/maven/conf
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src src
COPY .git .git
RUN mvn package

FROM openjdk:13
COPY --from=build /target/twitch-socket-adapter.jar twitch-socket-adapter.jar
EXPOSE 8080
CMD java -Djava.security.egd=file:/dev/./urandom -jar twitch-socket-adapter.jar