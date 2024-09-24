# 빌드 스테이지
FROM gradle:8.5.0-jdk21-alpine AS build
USER root
WORKDIR /gateway

# 소스 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# 빌드 시에 환경 변수를 사용하는 경우 필요에 따라 아래와 같이 사용
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar --no-build-cache

# 실행 스테이지
FROM azul/zulu-openjdk:21-jre

# 애플리케이션 jar 파일 복사
COPY --from=build /gateway/build/libs/*.jar app.jar

# 스프링 부트 애플리케이션 실행
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]

# VOLUME 설정
VOLUME /tmp