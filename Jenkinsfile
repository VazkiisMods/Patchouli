#!/usr/bin/env groovy

pipeline {
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    agent any
    tools {
        jdk "jdk-21.0.2"
    }
    stages {
        stage('Clean') {
            steps {
                echo 'Cleaning Project'
                sh 'chmod +x gradlew'
                sh './gradlew clean --no-daemon'
            }
        }
        stage('Build and Deploy Release') {
            when {
                tag 'release-*'
            }
            environment {
                RELEASE_MODE = '1'
            }
            steps {
                sh './gradlew build publish --no-daemon'
            }
        }
        stage('Build and Deploy Snapshot') {
            when {
                not {
                    tag 'release-*'
                }
            }
            steps {
                sh './gradlew build publish --no-daemon'
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: 'NeoForge/build/libs/*.jar, Fabric/build/libs/*.jar'
        }
    }
}
