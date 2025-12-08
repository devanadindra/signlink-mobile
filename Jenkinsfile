pipeline {
    agent any

    stages {
        stage('Deploy') {
            steps {
                sh '''
                    cd /home/depaaa/signlink

                    echo ">>> Pulling latest code..."
                    git pull

                    cd /back-end

                    echo ">>> Restarting Docker..."
                    docker compose down || true
                    docker compose up -d --build
                '''
            }
        }
    }
}
