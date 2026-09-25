pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'ls -la'
            }
        }
        stage('Docker sanity check') {
            steps {
                sh 'docker --version'
                sh 'docker ps'
            }
        }
    }
}