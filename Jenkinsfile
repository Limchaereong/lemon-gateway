pipeline {
    agent any
    environment {
        REPO = "https://github.com/Barsoup-Tensor/gateway.git"
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
                    sh "docker stop gateway || true"
                    sh "docker rm gateway || true"
                }
            }
        }
        stage('Remove Old Images') {
            steps {
                script {
                    sh "docker images gateway -q | xargs -r docker rmi || true"
                    sh "docker images -f 'dangling=true' -q | xargs -r docker rmi || true"
                }
            }
        }
        stage("Build") {
            steps {
                script {
                    sh "docker build -t gateway ."
                    }
            }
        }
        stage('Up') {
            steps {
                script {
                    sh "docker run -d --rm --name gateway -p 8085:8085 gateway"
            }
        }
    }
}