pipeline {
    agent any

    tools {
        nodejs 'node20'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Backend Build') {
            steps {
                dir('eBank-backend') {
                    sh './mvnw clean compile -DskipTests'
                }
            }
        }

        stage('Backend Test') {
            steps {
                dir('eBank-backend') {
                    sh './mvnw test'
                }
            }
            post {
                always {
                    junit 'eBank-backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Frontend Install') {
            steps {
                dir('eBank-frontend') {
                    sh 'npm ci'
                }
            }
        }

        stage('Frontend Test') {
            steps {
                dir('eBank-frontend') {
                    sh 'npm test -- --watch=false --browsers=ChromeHeadless'
                }
            }
        }

        stage('Frontend Build') {
            steps {
                dir('eBank-frontend') {
                    sh 'npm run build'
                }
            }
        }

        stage('Dockerize Backend') {
            steps {
                dir('eBank-backend') {
                    sh 'docker build -t ebank-backend:$BUILD_NUMBER .'
                }
            }
        }

        stage('Dockerize Frontend') {
            steps {
                dir('eBank-frontend') {
                    sh 'docker build -t ebank-frontend:$BUILD_NUMBER .'
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline finished: ${currentBuild.currentResult}"
        }
    }
}