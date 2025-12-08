pipeline {
    agent any

    stages {
        stage('Deploy to VPS') {
            steps {
                sh '''
                cd ~/signlink
                git pull origin main
                docker compose up -d --build
                '''
            }
        }
    }
}
