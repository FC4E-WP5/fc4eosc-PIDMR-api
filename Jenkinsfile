pipeline {
    agent none
    options {
        checkoutToSubdirectory('pidmr.api')
        newContainerPerStage()
    }
    environment {
        PROJECT_DIR='pidmr.api'
        GH_USER = 'newgrnetci'
        GH_EMAIL = '<argo@grnet.gr>'
    }
    stages {
        stage('PIDMR API Packaging & Testing') {
            agent {
                docker {
                    image 'argo.registry:5000/epel-7-java11-mvn384'
                    args '-v $HOME/.m2:/root/.m2 -v /var/run/docker.sock:/var/run/docker.sock -u root:root'
                }
            }
            steps {
                echo 'PIDMR API Packaging & Testing'
                sh """
                cd ${WORKSPACE}/${PROJECT_DIR}
                mvn clean package -Dquarkus.package.type=uber-jar
                """
                junit '**/target/surefire-reports/*.xml'
                archiveArtifacts artifacts: '**/target/*.jar'
                step( [ $class: 'JacocoPublisher' ] )
            }
            post {
                always {
                    cleanWs()
                }
            }
        }
    }
    post {
        success {
            script{
                if ( env.BRANCH_NAME == 'main' || env.BRANCH_NAME == 'devel' ) {
                    slackSend( message: ":rocket: New version for <$BUILD_URL|$PROJECT_DIR>:$BRANCH_NAME Job: $JOB_NAME !")
                }
            }
        }
        failure {
            script{
                if ( env.BRANCH_NAME == 'main' || env.BRANCH_NAME == 'devel' ) {
                    slackSend( message: ":rain_cloud: Build Failed for <$BUILD_URL|$PROJECT_DIR>:$BRANCH_NAME Job: $JOB_NAME")
                }
            }
        }
    }
}