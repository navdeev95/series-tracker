FROM ibm-semeru-runtimes:open-25-jre
WORKDIR /app

# Копируем JAR (должен быть создан через bootJar)
COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-XX:+UseSerialGC", "-XX:MaxRAMPercentage=70", "-jar", "app.jar"]