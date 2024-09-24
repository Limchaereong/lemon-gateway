# 빌드 스테이지
FROM gradle:8.5.0-jdk21-alpine AS build
USER root
WORKDIR /auth

# 빌드 시 전달받을 변수 선언
ARG JWT_SECRET
ARG EUREKA_SERVER_HOSTNAME
ARG EUREKA_SERVER_PORT

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

# 실행 시 필요한 환경 변수 설정
ENV JWT_SECRET=$JWT_SECRET
ENV EUREKA_SERVER_HOSTNAME=$EUREKA_SERVER_HOSTNAME
ENV EUREKA_SERVER_PORT=$EUREKA_SERVER_PORT

COPY --from=build /auth/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]
VOLUME /tmp