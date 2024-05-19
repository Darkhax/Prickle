#!/usr/bin/env groovy

pipeline {

    agent any
    
    tools {
        jdk "jdk-21"
    }
    
    stages {
        
        stage('Build') {
        
            steps {
            
                withCredentials([ file(credentialsId: 'gradle_secrets', variable: 'ORG_GRADLE_PROJECT_secretFile') ]) {
            
                    echo 'Building project.'
                    sh 'chmod +x gradlew'
                    sh './gradlew clean build publish --stacktrace --warn'
                }
            }
        }
    }
}