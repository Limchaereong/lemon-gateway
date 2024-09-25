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
                checkout scm
            }
        }
        stage('Setup Environment') {
            steps {
                script {
                    sh "ls -al"
                    sh "chmod +x ./gradlew"
                }
            }
        }
        stage('Stop and Remove Container') {
            steps {
                script {
                    sh "docker stop ${DOCKER_CONTAINER} || true"
                    sh "docker rm ${DOCKER_CONTAINER} || true"
                }
            }
        }
        stage('Remove Old Images') {
            steps {
                script {
                    sh "docker images ${DOCKER_IMAGE} -q | xargs -r docker rmi || true"
                    sh "docker images -f 'dangling=true' -q | xargs -r docker rmi || true"
                }
            }
        }
        stage('Build') {
            steps {
                script {
                    withCredentials([
                        string(credentialsId: 'JWT_SECRET', variable: 'JWT_SECRET'),
                        string(credentialsId: 'EUREKA_SERVER_HOSTNAME', variable: 'EUREKA_SERVER_HOSTNAME'),
                        string(credentialsId: 'EUREKA_SERVER_PORT', variable: 'EUREKA_SERVER_PORT')
                    ]) {
                        sh '''
                        docker build --build-arg JWT_SECRET=$JWT_SECRET \
                                     --build-arg EUREKA_SERVER_HOSTNAME=$EUREKA_SERVER_HOSTNAME \
                                     --build-arg EUREKA_SERVER_PORT=$EUREKA_SERVER_PORT \
                                     -t ${DOCKER_IMAGE} .
                        '''
                    }
                }
            }
        }
        stage('Up') {
            steps {
                script {
                    // 환경 변수를 출력 및 컨테이너 실행
                    sh '''
                    echo "JWT_SECRET: $JWT_SECRET"
                    echo "EUREKA_SERVER_HOSTNAME: $EUREKA_SERVER_HOSTNAME"
                    echo "EUREKA_SERVER_PORT: $EUREKA_SERVER_PORT"

                    docker run -d --name ${DOCKER_CONTAINER} -p 8085:8085 \
                    -e JWT_SECRET=$JWT_SECRET \
                    -e EUREKA_SERVER_HOSTNAME=$EUREKA_SERVER_HOSTNAME \
                    -e EUREKA_SERVER_PORT=$EUREKA_SERVER_PORT \
                    ${DOCKER_IMAGE}
                    '''
                }
            }
        }
    }
}