FROM openjdk:17-jdk-slim as build

WORKDIR /app

# 그래들 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
RUN apt-get update && apt-get install -y dos2unix && dos2unix gradlew
RUN ./gradlew dependencies --no-daemon

COPY src src

# 권한 설정 및 빌드

RUN chmod +x ./gradlew
RUN ./gradlew bootJar --no-daemon

# 실행 단계
FROM openjdk:17-jdk-slim

WORKDIR /app

# 빌드 단계에서 생성된 jar 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=default
ENV JAVA_OPTS=default
ENV DB_HOST=host.docker.internal

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]