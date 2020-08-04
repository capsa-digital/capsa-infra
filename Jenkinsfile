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
                withCredentials([
                    string(credentialsId: 'my-first-secret', variable: 'FIRST_SECRET')
                ])   {
                    sh '# echo "$FIRST_SECRET" | sed -E "s/\\W+/\\n/g" | hexdump -C'
                    sh 'docker build \
                                  --file capsa-infra-command/Dockerfile \
                                  --build-arg JAR_FILE=capsa-infra-command/build/libs/capsa-infra-command-latest.jar \
                                  --build-arg INFO_APP_BUILD=$BUILD_NUMBER \
                                  --tag gcr.io/capsa-digital/capsa-infra-command:$BUILD_NUMBER \
                                  --tag gcr.io/capsa-digital/capsa-infra-command:latest .'
                    sh 'docker push gcr.io/capsa-digital/capsa-infra-command:$BUILD_NUMBER'
                    sh 'docker push gcr.io/capsa-digital/capsa-infra-command:latest'
                }
            }
        }
        stage('Create `Query` Docker Image') {
            steps {
                    sh '# echo "$FIRST_SECRET" | sed -E "s/\\W+/\\n/g" | hexdump -C'
                    sh 'docker build \
                                  --file capsa-infra-query/Dockerfile \
                                  --build-arg JAR_FILE=capsa-infra-query/build/libs/capsa-infra-query-latest.jar \
                                  --build-arg INFO_APP_BUILD=$BUILD_NUMBER \
                                  --tag gcr.io/capsa-digital/capsa-infra-query:$BUILD_NUMBER \
                                  --tag gcr.io/capsa-digital/capsa-infra-query:latest .'
                    sh 'docker push gcr.io/capsa-digital/capsa-infra-query:$BUILD_NUMBER'
                    sh 'docker push gcr.io/capsa-digital/capsa-infra-query:latest'
            }
        }
        stage('Create K8s Cluster') {
            steps {
                sh 'gcloud container clusters create capsa-cluster \
                --zone us-central1-a \
                --release-channel regular'
            }
        }
        stage('Deploy `Command` Docker Container') {
            steps {
                sh 'kubectl create deployment command-app --image=gcr.io/capsa-digital/capsa-infra-command:$BUILD_NUMBER'
                sh 'kubectl scale deployment command-app --replicas=3'
                sh 'kubectl autoscale deployment command-app --cpu-percent=80 --min=1 --max=5'
                sh 'kubectl set env deployment/command-app --env SPRING_PROFILES_ACTIVE=dev'
                sh 'kubectl expose deployment command-app --name=command-app-service --type=LoadBalancer --port 80 --target-port 8080'
                sh 'kubectl describe pods'
                sh 'kubectl get service'
            }
        }
        stage('Component test `Command` Docker Container') {
            steps {
               sh 'echo TODO'
            }
        }
        stage('Deploy `Query` Docker Container') {
            steps {
                sh 'kubectl create deployment query-app --image=gcr.io/capsa-digital/capsa-infra-query:$BUILD_NUMBER'
                sh 'kubectl scale deployment query-app --replicas=3'
                sh 'kubectl autoscale deployment query-app --cpu-percent=80 --min=1 --max=5'
                sh 'kubectl set env deployment/query-app --env SPRING_PROFILES_ACTIVE=dev'
                sh 'kubectl expose deployment query-app --name=query-app-service --type=LoadBalancer --port 80 --target-port 8080'
                sh 'kubectl describe pods'
                sh 'kubectl get service'
            }
        }
        stage('Component test `Query` Docker Container') {
            steps {
               sh 'echo TODO'
            }
        }
        stage('Integration test') {
            steps {
               sh 'echo TODO'
            }
        }
        stage('Delete K8s Cluster') {
            input {
                message "Delete K8s Cluster?"
            }
            steps {
                sh 'gcloud container clusters delete capsa-cluster \
                --zone us-central1-a --quiet'
            }
        }
        stage('Release') {
            steps {
               sh 'echo TODO'
            }
        }
    }
}