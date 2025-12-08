pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
    }

    stages {
        stage('Sparse Checkout') {
            steps {
                sh """
                    rm -rf signlink || true
                    git clone --no-checkout https://github.com/devanadindra/signlink-mobile.git signlink
                    cd signlink
                    git sparse-checkout init --cone

                    # include all, exclude some
                    git sparse-checkout set / "!mobile" "!ai"

                    git checkout main
                """
            }
        }

        stage('Deploy') {
            steps {
                sh """
                    cd signlink/back-end
                    docker compose up -d --build
                """
            }
        }
    }
}
