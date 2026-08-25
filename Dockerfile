FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/similar-products-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 5000

HEALTHCHECK --interval=30s --timeout=3s --start-period=15s --retries=3 \
    CMD wget -qO- http://localhost:5000/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
