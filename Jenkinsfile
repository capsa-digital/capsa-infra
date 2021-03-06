pipeline {
    agent { node { label 'agent' } }
    stages {
        stage('Build, Unit test, Static code analysis') {
            steps {
                sh './gradlew build --stacktrace --scan'
            }
        }
        stage('Create `Command` Docker Image') {
            steps {
                sh 'docker build \
                              --file capsa-archetypes-command/Dockerfile \
                              --build-arg JAR_FILE=capsa-archetypes-command/build/libs/capsa-archetypes-command-latest.jar \
                              --build-arg INFO_APP_BUILD=$BUILD_NUMBER \
                              --tag gcr.io/capsa-digital/capsa-archetypes-command:$BUILD_NUMBER \
                              --tag gcr.io/capsa-digital/capsa-archetypes-command:latest .'
                sh 'docker push gcr.io/capsa-digital/capsa-archetypes-command:$BUILD_NUMBER'
                sh 'docker push gcr.io/capsa-digital/capsa-archetypes-command:latest'
            }
        }
        stage('Create `Query` Docker Image') {
            steps {
                sh 'docker build \
                              --file capsa-archetypes-query/Dockerfile \
                              --build-arg JAR_FILE=capsa-archetypes-query/build/libs/capsa-archetypes-query-latest.jar \
                              --build-arg INFO_APP_BUILD=$BUILD_NUMBER \
                              --tag gcr.io/capsa-digital/capsa-archetypes-query:$BUILD_NUMBER \
                              --tag gcr.io/capsa-digital/capsa-archetypes-query:latest .'
                sh 'docker push gcr.io/capsa-digital/capsa-archetypes-query:$BUILD_NUMBER'
                sh 'docker push gcr.io/capsa-digital/capsa-archetypes-query:latest'
            }
        }
        stage('Create K8s Cluster') {
            steps {
                sh 'gcloud container clusters create capsa-cluster-$GIT_BRANCH-$BUILD_NUMBER \
                --num-nodes 2 \
                --zone us-central1-a \
                --release-channel regular'
            }
        }
        stage('Deploy `Command` Docker Container') {
            steps {
                sh 'kubectl create deployment command-app --image=gcr.io/capsa-digital/capsa-archetypes-command:$BUILD_NUMBER'
                sh 'kubectl scale deployment command-app --replicas=1'
                sh 'kubectl autoscale deployment command-app --cpu-percent=80 --min=1 --max=1'
                sh 'kubectl set env deployment/command-app --env SPRING_PROFILES_ACTIVE=dev'
                sh 'kubectl expose deployment command-app --name=command-app-service --type=LoadBalancer --port 80 --target-port 8080'
                sh 'kubectl describe pods'
                sh 'kubectl get service'
            }
        }
        stage('Deploy `Query` Docker Container') {
            steps {
                sh 'kubectl create deployment query-app --image=gcr.io/capsa-digital/capsa-archetypes-query:$BUILD_NUMBER'
                sh 'kubectl scale deployment query-app --replicas=1'
                sh 'kubectl autoscale deployment query-app --cpu-percent=80 --min=1 --max=1'
                sh 'kubectl set env deployment/query-app --env SPRING_PROFILES_ACTIVE=dev'
                sh 'kubectl expose deployment query-app --name=query-app-service --type=LoadBalancer --port 80 --target-port 8080'
                sh 'kubectl describe pods'
                sh 'kubectl get service'
            }
        }
        stage('Component Test') {
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    sh './gradlew integrationTest -Pprofiles=dev'
                }
            }
        }
        stage('Delete K8s Cluster') {
            input {
                message "Delete K8s Cluster?"
            }
            steps {
                sh 'gcloud container clusters delete capsa-cluster-$GIT_BRANCH-$BUILD_NUMBER \
                --zone us-central1-a --quiet'
            }
        }
        stage('Release') {
            when {
                expression {
                    currentBuild.currentResult == "SUCCESS"
                }
            }
            steps {
               sh 'echo TODO Release'
            }
        }
    }
}