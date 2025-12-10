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

                        # Sparse checkout exclude mobile & ai
                        git sparse-checkout init --cone
                        echo "/*" > .git/info/sparse-checkout
                        echo "!mobile/" >> .git/info/sparse-checkout
                        echo "!ai/" >> .git/info/sparse-checkout

                        git checkout main
                        git sparse-checkout reapply

                    else
                        echo "== Updating existing sparse clone =="
                        cd signlink
                        git fetch origin main
                        git reset --hard origin/main
                        git sparse-checkout reapply
                    fi
                '''
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    cd signlink
                    export DOCKER_BUILDKIT=1
                    docker-compose up -d --build
                '''
            }
        }
    }
}
