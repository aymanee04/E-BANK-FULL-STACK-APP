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
        stage('Deploy') {
            steps {
                withCredentials([
                        sshUserPrivateKey(credentialsId: 'vm-ssh-key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER'),
                        string(credentialsId: 'db-password', variable: 'DB_PASSWORD'),
                        string(credentialsId: 'jwt-secret', variable: 'JWT_SECRET')
                ]) {
                    sh '''
                VM_HOST=192.168.11.118

                docker save ebank-backend:$BUILD_NUMBER ebank-frontend:$BUILD_NUMBER -o images.tar

                scp -o StrictHostKeyChecking=no -i $SSH_KEY images.tar $SSH_USER@$VM_HOST:~/
                scp -o StrictHostKeyChecking=no -i $SSH_KEY docker-compose.deploy.yml $SSH_USER@$VM_HOST:~/

                ssh -o StrictHostKeyChecking=no -i $SSH_KEY $SSH_USER@$VM_HOST "
                    docker load -i images.tar &&
                    rm images.tar &&
                    printf 'IMAGE_TAG=%s\\\\nDB_PASSWORD=%s\\\\nJWT_SECRET=%s\\\\nAPP_CORS_ALLOWED_ORIGIN=%s\\\\n' '$BUILD_NUMBER' '$DB_PASSWORD' '$JWT_SECRET' "http://$VM_HOST:4200" > .env &&
                    chmod 600 .env &&
                    docker compose -f docker-compose.deploy.yml --env-file .env up -d
                "
            '''
                }
            }
        }
        stage('Smoke Test') {
            steps {
                withCredentials([
                        sshUserPrivateKey(credentialsId: 'vm-ssh-key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')
                ]) {
                    sh '''
                VM_HOST=192.168.11.118

                echo "Waiting for backend to become healthy..."
                for i in $(seq 1 20); do
                    STATUS=$(ssh -o StrictHostKeyChecking=no -i $SSH_KEY $SSH_USER@$VM_HOST \
                        "curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/actuator/health")
                    if [ "$STATUS" = "200" ]; then
                        echo "Backend is healthy (HTTP 200)"
                        break
                    fi
                    echo "Not ready yet (got $STATUS), waiting..."
                    sleep 3
                    if [ "$i" = "20" ]; then
                        echo "Backend did not become healthy in time"
                        exit 1
                    fi
                done

                echo "Checking frontend..."
                FRONTEND_STATUS=$(ssh -o StrictHostKeyChecking=no -i $SSH_KEY $SSH_USER@$VM_HOST \
                    "curl -s -o /dev/null -w '%{http_code}' http://localhost:4200")
                if [ "$FRONTEND_STATUS" = "200" ]; then
                    echo "Frontend is up (HTTP 200)"
                else
                    echo "Frontend check failed (got $FRONTEND_STATUS)"
                    exit 1
                fi
            '''
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