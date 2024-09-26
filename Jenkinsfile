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
                       sh '''
                        docker build -t ${DOCKER_IMAGE} .
                       '''
                }
            }
        }
        stage('Up') {
            steps {
                script {
                    withCredentials([
                        string(credentialsId: 'JWT_SECRET', variable: 'CRED_JWT_SECRET'),
                        string(credentialsId: 'EUREKA_SERVER_HOSTNAME', variable: 'CRED_EUREKA_SERVER_HOSTNAME'),
                        string(credentialsId: 'EUREKA_SERVER_PORT', variable: 'CRED_EUREKA_SERVER_PORT')
                    ]) {

                    sh '''
                    docker run -d --name ${DOCKER_CONTAINER} -p 8085:8085 \
                        -e "JWT_SECRET=${CRED_JWT_SECRET}" \
                        -e "EUREKA_SERVER_HOSTNAME=${CRED_EUREKA_SERVER_HOSTNAME}" \
                        -e "EUREKA_SERVER_PORT=${CRED_EUREKA_SERVER_PORT}" \
                        ${DOCKER_IMAGE}
                    '''
                    }
                }
            }
        }
    }
}