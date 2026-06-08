pipeline {
    agent any

    tools {
        nodejs "NodeJS18"
    }

    stages {

        stage("Checkout Code") {
            steps {
                git branch: 'main',
                    url: 'https://github.com/mailtojosh5/TypeScriptHybridAllureFramework.git'
            }
        }

        stage("Install Dependencies") {
            steps {
                bat "npm install"
            }
        }

        stage("Install Playwright Browsers") {
            steps {
                bat "npx playwright install --with-deps"
            }
        }

        stage("Run Playwright Tests") {
            steps {
                bat "npx playwright test"
            }
        }

    }

    post {
        always {

            // Publish Allure Report
            allure([
                includeProperties: false,
                jdk: '',
                results: [[path: 'allure-results']]
            ])

            // Archive Playwright HTML Report
            archiveArtifacts artifacts: 'playwright-report/**', allowEmptyArchive: true

            // Archive Allure results for debugging
            archiveArtifacts artifacts: 'allure-results/**', allowEmptyArchive: true
        }
    }
}
