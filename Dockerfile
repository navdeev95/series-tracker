FROM eclipse-temurin:23-jdk
WORKDIR /app

# Копируем JAR (должен быть создан через bootJar)
COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]