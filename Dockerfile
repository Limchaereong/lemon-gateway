# 빌드 스테이지
FROM gradle:8.5.0-jdk21-alpine AS build
USER root
WORKDIR /gateway

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

# 빌드 시 전달된 ARG를 ENV로 설정 (실행 단계에서도 사용 가능하게 하기 위해)
ENV JWT_SECRET=${JWT_SECRET}
ENV EUREKA_SERVER_HOSTNAME=${EUREKA_SERVER_HOSTNAME}
ENV EUREKA_SERVER_PORT=${EUREKA_SERVER_PORT}

# 필요한 경우 환경 변수 값을 확인하기 위한 디버그 용도
RUN echo "JWT_SECRET: ${JWT_SECRET}"
RUN echo "EUREKA_SERVER_HOSTNAME: ${EUREKA_SERVER_HOSTNAME}"
RUN echo "EUREKA_SERVER_PORT: ${EUREKA_SERVER_PORT}"

COPY --from=build /gateway/build/libs/*.jar app.jar

# 실행 명령
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]
VOLUME /tmp