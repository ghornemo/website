FROM amazoncorretto:8

WORKDIR /app

COPY target/gemal-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]