FROM eclipse-temurin:23-jdk
WORKDIR /app

# Копируем сначала файлы для зависимостей (для лучшего кеширования)
COPY build.gradle .
COPY gradlew .
COPY gradle gradle
RUN chmod +x gradlew

# Копируем исходный код
COPY src src

# Собираем проект
RUN ./gradlew clean build -x test

# Проверяем структуру
RUN ls -la build/libs/

ENTRYPOINT ["java","-jar","app.jar"]