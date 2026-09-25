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

        stage('Start MySQL') {
            steps {
                sh '''
                    docker run -d --name mysql-db --network jenkins-net \
                        -e MYSQL_ROOT_PASSWORD=2004 \
                        -e MYSQL_DATABASE=BANK_DB \
                        mysql:8.0

                    echo "Waiting for MySQL to become ready..."
                    for i in $(seq 1 30); do
                        if docker exec mysql-db mysqladmin ping -h localhost -uroot -p2004 --silent; then
                            echo "MySQL is ready"
                            exit 0
                        fi
                        echo "Not ready yet, waiting..."
                        sleep 2
                    done
                    echo "MySQL did not become ready in time"
                    exit 1
                '''
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
                    sh 'npm test -- --watch=false'
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
            sh 'docker rm -f mysql-db || true'
            echo "Pipeline finished: ${currentBuild.currentResult}"
        }
    }
}