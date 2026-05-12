pipeline {
    agent any

    tools {
        maven 'MAVEN_HOME'
    }

    environment {
        SONAR_TOKEN = credentials('Sonarqube')

        POM_PATH = 'pom.xml'

        DOCKER_HOST = 'unix:///var/run/docker.sock'
        TESTCONTAINERS_RYUK_DISABLED = 'true'
        TESTCONTAINERS_CHECKS_DISABLE = 'true'
        TESTCONTAINERS_HOST_OVERRIDE = 'host.docker.internal'
    }

    stages {

        stage('Build') {
            steps {
                timeout(time: 8, unit: 'MINUTES') {
                    sh "mvn -DskipTests clean package -f ${POM_PATH}"
                }
            }
        }

        stage('Test') {
            steps {
                timeout(time: 15, unit: 'MINUTES') {
                    sh "mvn clean verify -f ${POM_PATH}"
                }
            }

            post {
                always {
                    junit allowEmptyResults: true,
                          testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                timeout(time: 8, unit: 'MINUTES') {
                    withSonarQubeEnv('sonarqube') {
                        sh """
                            mvn sonar:sonar \
                                -Dsonar.projectKey=ms-categoria \
                                -Dsonar.projectName=ms-categoria \
                                -Dsonar.token=${SONAR_TOKEN} \
                                -f ${POM_PATH}
                        """
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 4, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo 'Pipeline ejecutado correctamente para ms-categoria.'
        }

        failure {
            echo 'Pipeline falló. Revisar los logs de Jenkins.'
        }

        always {
            cleanWs()
        }
    }
}