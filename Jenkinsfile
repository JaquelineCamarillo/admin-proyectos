// Pipeline de Admin Proyectos: compila los 2 microservicios con JDK 21 y Maven 3.9
pipeline {
    agent any

    // Herramientas configuradas en: Administrar Jenkins → Tools
    tools {
        jdk   'jdk21'
        maven 'maven-3.9'
    }

    options {
        timestamps()
    }

    stages {
        stage('Verificar versiones') {
            steps {
                sh 'java -version'
                sh 'mvn -version'
            }
        }

        stage('API · Ejercicio5MyAPIGestor') {
            steps {
                dir('Ejercicio5MyAPIGestor') {
                    // Las pruebas de la API necesitan MariaDB; en Jenkins solo se compila y empaqueta
                    sh 'mvn -B clean package -DskipTests'
                }
            }
        }

        stage('Cliente · Ejercicio5Cliente') {
            steps {
                dir('Ejercicio5Cliente') {
                    // Las pruebas del cliente simulan la API (MockRestServiceServer), no necesitan servidor
                    sh 'mvn -B clean package'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'Ejercicio5Cliente/target/surefire-reports/*.xml'
                }
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            echo 'Los 2 microservicios compilaron con Java 21.'
        }
    }
}