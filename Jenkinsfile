pipeline {
    agent any
    stages {
        stage('create cluster') {
            steps {
                sh 'gcloud container clusters create glome-capsa-cluster --zone us-central1-a --release-channel regular'
            }
        }
        stage('delete cluster') {
            steps {
                sh 'gcloud container clusters create glome-capsa-cluster --zone us-central1-a --release-channel regular'
            }
        }
    }
}