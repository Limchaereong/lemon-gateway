pipeline {
    agent any
    environment {
        REPO = "https://github.com/Barsoup-Tensor/gateway.git"  // Github repository URL
        DOCKER_IMAGE = "gateway"  // 이미지 이름
        DOCKER_CONTAINER = "gateway"  // 컨테이너 이름
    }
    stages {
        stage('Checkout') {
            steps {
                // Git에서 소스 코드 가져오기
                checkout scm
            }
        }
        stage('Setup Environment') {
            steps {
                script {
                    // 현재 디렉토리 내의 파일을 나열하고 gradlew 파일에 실행 권한을 부여
                    sh "ls -al"
                    sh "chmod +x ./gradlew"
                }
            }
        }
        stage('Stop and Remove Container') {
            steps {
                script {
                    // 컨테이너 중지 및 제거 (이미 실행 중인 경우)
                    sh "docker stop ${DOCKER_CONTAINER} || true"
                    sh "docker rm ${DOCKER_CONTAINER} || true"
                }
            }
        }
        stage('Remove Old Images') {
            steps {
                script {
                    // 오래된 이미지를 제거
                    sh "docker images ${DOCKER_IMAGE} -q | xargs -r docker rmi || true"
                    sh "docker images -f 'dangling=true' -q | xargs -r docker rmi || true"
                }
            }
        }
        stage("Build") {
            steps {
                script {
                    // Credentials를 사용하여 환경 변수 설정
                    withCredentials([
                        string(credentialsId: 'JWT_SECRET', variable: 'JWT_SECRET'),
                        string(credentialsId: 'EUREKA_SERVER_HOSTNAME', variable: 'EUREKA_SERVER_HOSTNAME'),
                        string(credentialsId: 'EUREKA_SERVER_PORT', variable: 'EUREKA_SERVER_PORT')
                    ]) {
                        // 시크릿 변수를 직접 사용하는 대신 환경 변수로 전달
                        sh '''
                        echo "Building Docker image with the following environment variables:"
                        echo "JWT_SECRET length: ${#JWT_SECRET}"  # 비밀키의 길이만 출력하여 보안 유지
                        echo "EUREKA_SERVER_HOSTNAME: $EUREKA_SERVER_HOSTNAME"
                        echo "EUREKA_SERVER_PORT: $EUREKA_SERVER_PORT"
                        docker build --build-arg JWT_SECRET=$JWT_SECRET \
                                     --build-arg EUREKA_SERVER_HOSTNAME=$EUREKA_SERVER_HOSTNAME \
                                     --build-arg EUREKA_SERVER_PORT=$EUREKA_SERVER_PORT \
                                     -t gateway .
                        '''
                    }
                }
            }
        }
        stage('Up') {
            steps {
                script {
                    // 컨테이너 실행 (포트 매핑 포함)
                    try {
                        sh "docker run -d --name ${DOCKER_CONTAINER} -p 8085:8085 -e JWT_SECRET=$JWT_SECRET -e EUREKA_SERVER_HOSTNAME=$EUREKA_SERVER_HOSTNAME -e EUREKA_SERVER_PORT=$EUREKA_SERVER_PORT ${DOCKER_IMAGE}"
                    } catch(Exception e) {
                        sh "docker restart ${DOCKER_CONTAINER} || true"
                    }
                }
            }
        }
    }
}
