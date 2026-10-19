pipeline {
    agent any

    tools {
        // Nom exact configuré dans Manage Jenkins > Global Tool Configuration > Maven
        maven 'Maven_nom_sur_Jenkins'
    }

    environment {
        // Remplacez par votre compte / nom d'image DockerHub
        DOCKER_USER = 'badre09'
        IMAGE_NAME  = 'triang7'
        IMAGE_TAG   = '1.0.0'
    }

    stages {
        stage('git checkout') {
            steps {
                // Remplacez l'URL par celle de VOTRE dépôt GitHub
                git credentialsId: 'git_credentials',
                    url: 'https://github.com/VOTRE_COMPTE/Triang07.git',
                    branch: 'master'
            }
        }

        stage('Build the application') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('Unit Test Execution') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build the docker image') {
            steps {
                sh "docker build --tag ${DOCKER_USER}/${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }

        stage('Push the docker image to DockerHub') {
            steps {
                // 'dockerhubpass' : credential de type "Secret text" créé dans Jenkins
                withCredentials([string(credentialsId: 'dockerhubpass', variable: 'dockerHubPass')]) {
                    // --password-stdin évite d'exposer le mot de passe dans les logs / la liste des processus
                    sh 'echo "$dockerHubPass" | docker login -u "$DOCKER_USER" --password-stdin'
                    sh "docker push ${DOCKER_USER}/${IMAGE_NAME}:${IMAGE_TAG}"
                }
            }
        }
    }

    post {
        failure {
            emailext body: "Ce Build ${BUILD_NUMBER} a échoué : ${BUILD_URL}",
                     recipientProviders: [requestor()],
                     subject: "Build ${JOB_NAME} #${BUILD_NUMBER} : ÉCHEC",
                     to: 'votre.email@exemple.com'
        }
    }
}
