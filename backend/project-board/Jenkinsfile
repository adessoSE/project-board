#!groovy
def BACKEND_DIR = 'backend/project-board'
def DOCKER_ARTIFACT_DIR = 'docker/temp'

pipeline {

    agent any

    tools {
        gradle '5.0'
        jdk 'JDK-11.0.1 (OpenJDK)'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {

            steps {
                dir(BACKEND_DIR) {
                    sh 'gradle clean test'
                }
            }

        }

        stage('Build Artifacts') {
            environment {
                JIRA_CREDS = credentials('jira-creds')
                LDAP_CREDS = credentials('ldap-creds')
                DATA_SRC_CREDS = credentials('data-src-creds')
                MAIL_CREDS = credentials('mail-creds')
            }

            steps {
                dir(BACKEND_DIR) {
                    sh 'gradle dockerCopy'

                    dir(DOCKER_ARTIFACT_DIR) {
                        stash includes: '*', name: 'dockerArtifacts'
                    }
                }
            }
        }

        stage('Build Docker Image') {
            agent {
                label 'docker'
            }

            steps {
                dir(DOCKER_ARTIFACT_DIR) {
                    unstash 'dockerArtifacts'

                    writeFile file: "publicCert.pem", text: env.PUBLIC_CERT

                    sh 'sudo docker build -t pb-backend .'
                    sh 'sudo docker save pb-backend -o pb-backend.tar'
                    sh 'sudo chmod 777 pb-backend.tar'

                    sshagent(['jenkins-username-ssh-key']) {
                        sh 'scp -o StrictHostKeyChecking=no pb-backend.tar jenkins@$REMOTE_HOST:~'
                    }
                }
            }
        }

    }

}
