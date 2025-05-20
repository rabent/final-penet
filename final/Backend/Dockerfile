FROM openjdk:17-jdk-slim as build

WORKDIR /app

# 그래들 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# 권한 설정 및 빌드
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

# 실행 단계
FROM openjdk:17-jdk-slim

WORKDIR /app

# 빌드 단계에서 생성된 jar 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]