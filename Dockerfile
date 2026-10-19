FROM eclipse-temurin:8-jre
WORKDIR /app
COPY target/triang07-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["3", "4", "5"]
