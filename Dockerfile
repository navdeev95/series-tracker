FROM ibm-semeru-runtimes:open-25-jre
WORKDIR /app

# Копируем JAR (должен быть создан через bootJar)
COPY /tracker/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-XX:+UseSerialGC", "-XX:MaxRAMPercentage=70", "-jar", "app.jar"]