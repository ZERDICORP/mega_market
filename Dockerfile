FROM openjdk:17

MAINTAINER ZERDICORP

COPY target/mega_market-0.0.1-SNAPSHOT.jar mega_market-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/mega_market-0.0.1-SNAPSHOT.jar"]
