pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
    }

    stages {
        stage('Sparse Checkout') {
            steps {
                sh '''
                    if [ ! -d signlink/.git ]; then
                        echo "== First time clone =="
                        git clone --no-checkout https://github.com/devanadindra/signlink-mobile.git signlink
                        cd signlink
                        git sparse-checkout init --cone
                        git sparse-checkout set / "!mobile" "!ai"
                        git checkout main
                    else
                        echo "== Updating existing sparse clone =="
                        cd signlink
                        git fetch --all
                        git checkout main
                        git reset --hard origin/main
                    fi
                '''
            }
        }

        stage('Copy ENV') {
            steps {
                withCredentials([file(credentialsId: 'env-backend', variable: 'ENV_FILE')]) {
                    sh """
                cp \$ENV_FILE signlink/.env
                cp \$ENV_FILE signlink/back-end/.env
            """
                }
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    cd signlink
                    docker compose up -d --build
                '''
            }
        }
    }
}
