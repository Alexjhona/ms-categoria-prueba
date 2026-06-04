environment {
    PROJECT_KEY = 'ms-categoria-prueba'
    PROJECT_NAME = 'ms-categoria-prueba'
    JACOCO_XML = 'target/site/jacoco/jacoco.xml'
}

stages {

    stage('Checkout') {
        steps {
            checkout scm
        }
    }

    stage('Test') {
        steps {
            sh '''
                echo "Ejecutando pruebas unitarias, pruebas de controlador y pruebas integrales..."
                if [ -f "./mvnw" ]; then
                  chmod +x ./mvnw
                  ./mvnw -B clean test
                else
                  mvn -B clean test
                fi
            '''
        }
    }

    stage('Verify and JaCoCo') {
        steps {
            sh '''
                echo "Ejecutando verificación Maven y generando reporte de cobertura JaCoCo..."
                if [ -f "./mvnw" ]; then
                  chmod +x ./mvnw
                  ./mvnw -B verify
                else
                  mvn -B verify
                fi

                echo "Verificando existencia del reporte JaCoCo..."
                if [ -f "target/site/jacoco/jacoco.xml" ]; then
                  echo "Reporte JaCoCo generado correctamente."
                  ls -lh target/site/jacoco/jacoco.xml
                else
                  echo "ERROR: No se encontró target/site/jacoco/jacoco.xml"
                  exit 1
                fi
            '''
        }
    }

    stage('SonarQube Analysis') {
        steps {
            withSonarQubeEnv('sonarqube') {
                sh '''
                    echo "Ejecutando análisis SonarQube para ms-categoria-prueba..."
                    if [ -f "./mvnw" ]; then
                      chmod +x ./mvnw
                      ./mvnw -B sonar:sonar \
                        -Dsonar.projectKey=$PROJECT_KEY \
                        -Dsonar.projectName=$PROJECT_NAME \
                        -Dsonar.coverage.jacoco.xmlReportPaths=$JACOCO_XML
                    else
                      mvn -B sonar:sonar \
                        -Dsonar.projectKey=$PROJECT_KEY \
                        -Dsonar.projectName=$PROJECT_NAME \
                        -Dsonar.coverage.jacoco.xmlReportPaths=$JACOCO_XML
                    fi
                '''
            }
        }
    }

    stage('Quality Gate') {
        steps {
            timeout(time: 2, unit: 'MINUTES') {
                script {
                    def qg = waitForQualityGate abortPipeline: false
                    echo "Resultado Quality Gate: ${qg.status}"
                }
            }
        }
    }

    stage('Package') {
        steps {
            sh '''
                echo "Empaquetando microservicio sin volver a ejecutar pruebas..."
                echo "No se usa clean aquí para conservar los reportes generados previamente."

                if [ -f "./mvnw" ]; then
                  chmod +x ./mvnw
                  ./mvnw -B package -DskipTests
                else
                  mvn -B package -DskipTests
                fi
            '''
        }
    }

    stage('Archive Artifacts') {
        steps {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            archiveArtifacts artifacts: 'target/site/jacoco/**', allowEmptyArchive: true
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
        }
    }
}

post {
    always {
        echo 'Pipeline backend ms-categoria-prueba finalizado.'
    }

    success {
        echo 'Microservicio ms-categoria-prueba compilado, probado, analizado en SonarQube y empaquetado correctamente.'
    }

    failure {
        echo 'El pipeline falló. Revisar Console Output para identificar la etapa exacta.'
    }
}