pipeline {
    agent {
        docker {
            image 'maven:3.8.8-eclipse-temurin-17'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    environment {
        GIT_REPOSITORY     = "https://github.com/SOUFIANE-BOUSHABA/MoneyMinder.git"
        DOCKER_IMAGE_NAME  = "moneyminder-app"
        RECIPIENT_EMAIL    = "soufianboushaba12@gmail.com"
        POSTGRES_CONTAINER_NAME = "MoneyMinder-devops"
        POSTGRES_HOST      = "host.docker.internal"
        POSTGRES_PORT      = "5433"
        POSTGRES_DB        = "moneyminder"
        POSTGRES_USER      = "admin"
        POSTGRES_PASSWORD  = "admin"
    }

    stages {
        stage('Checkout Code') {
            steps {
                echo "Checking out code from GitHub..."
                git branch: 'master', url: "${GIT_REPOSITORY}"
            }
        }

        stage('Build and Run Tests') {
            steps {
                echo "Building project and running tests..."
                sh '''
                    mvn clean package test \
                      -Dspring.datasource.url=jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB} \
                      -Dspring.datasource.username=${POSTGRES_USER} \
                      -Dspring.datasource.password=${POSTGRES_PASSWORD}
                '''
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "Building Docker image for MoneyMinder app..."
                sh '''
                    # Find the JAR file in the target directory
                    JAR_FILE=$(ls target/*.jar | head -n 1)
                    if [ -z "$JAR_FILE" ]; then
                      echo "JAR file not found in target directory! Exiting..."
                      exit 1
                    fi
                    docker build --build-arg JAR_FILE=$JAR_FILE -t ${DOCKER_IMAGE_NAME} .
                '''
            }
        }

        stage('Deploy Docker Container') {
            steps {
                echo "Deploying Docker container..."
                sh '''
                    docker stop ${DOCKER_IMAGE_NAME}-container || true
                    docker rm ${DOCKER_IMAGE_NAME}-container || true
                    docker run -d -p 8080:8080 --name ${DOCKER_IMAGE_NAME}-container ${DOCKER_IMAGE_NAME}
                '''
            }
        }
    }

    post {
        success {
            echo "Build and deployment succeeded!"
            mail to: "${RECIPIENT_EMAIL}",
                 subject: "✅ SUCCESS: Build #${env.BUILD_NUMBER} - ${env.JOB_NAME}",
                 body: "The build and deployment for MoneyMinder were successful.\n\nDetails: ${env.BUILD_URL}"
        }
        failure {
            echo "Build or deployment failed!"
            mail to: "${RECIPIENT_EMAIL}",
                 subject: "❌ FAILURE: Build #${env.BUILD_NUMBER} - ${env.JOB_NAME}",
                 body: "The build or deployment for MoneyMinder failed.\n\nDetails: ${env.BUILD_URL}"
        }
    }
}
