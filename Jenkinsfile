pipeline {
    agent { label '' }
    environment {
            REGISTRY_CRED = credentials('FILL_IN_CREDENTIALS')
            CONTAINER_REGISTRY = "FILL_IN_CONTAINER_REGISTRY"
            IMAGE_ID = "$CONTAINER_REGISTRY/FILL_IN_IMAGE"
    }
    stages {
        stage ('Initialise') {
            steps {
                script {
                    sh 'git rev-parse HEAD > commit'
                    env.GIT_COMMIT = readFile('commit').trim()

                    sh 'git rev-parse --short HEAD > short_commit'
                    env.GIT_COMMIT_SHORT = readFile('short_commit').trim().toLowerCase()

                    sh '''
                        echo ${GIT_BRANCH}|sed 's#feature/##' > branch_name
                      '''
                    env.BRANCH_NAME_CLEAN = readFile('branch_name').trim().toLowerCase()
                    sh '''
                        echo "PATH = ${PATH}"
                        echo "M2_HOME = ${M2_HOME}"
                       '''

                }
            }
        }
        stage('Decrypt config file'){
          steps {
            withCredentials([file(credentialsId: 'jenkins.pgp', variable: 'FILE')]) {
                sh '''
                    pwd
                    cat $FILE | gpg --import || true # This is a problem on dockeredge
                    git-crypt unlock
                  '''
             }
          }
        }
        stage ('Run unit test'){
            steps {
                sh './mvnw test'
               step( [$class: 'JacocoPublisher',
                              exclusionPattern: '**/*Exception*,**/*Configuration*,**/ApiApplication*,**/*Test*'] )

            }
        }

        stage ('SonarQube analysis') {
            steps {
                script {
                    scannerHome = tool name: 'sonarqube', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
                }
                withSonarQubeEnv('sonarqube') {
                    sh "${scannerHome}/bin/sonar-scanner -X"
                }
            }
        }

        stage("Quality Gate") {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    // Parameter indicates whether to set pipeline to UNSTABLE if Quality Gate fails
                    // true = set pipeline to UNSTABLE, false = don't
                    waitForQualityGate abortPipeline: true
                }
            }
        }


        stage ('Build JAR and upload to nexus') {
            steps {
                sh './mvnw -Dmaven.test.failure.ignore=false -Dsettings.security=settings-security.xml -s settings.xml clean install deploy'
            }
        }


        stage ('Build container') {
            steps {
                sh '''
                    pwd
                    make build -e "NAME=$IMAGE_ID" "VERSION=${GIT_COMMIT}"
                   '''
            }
        }
        stage ('Push to registry') {
            steps {
                sh '''
                    docker login -u $REGISTRY_CRED_USR -p $REGISTRY_CRED_PSW $CONTAINER_REGISTRY
                    docker push "$IMAGE_ID:${GIT_COMMIT}"
                    '''
            }
        }
        stage ('Apply helmchart and deploy AKS') {
            agent { label 'docker' }



            steps {
                sh '''
                    echo "Process started for branch" ${GIT_BRANCH}
                    if  [[ ${GIT_BRANCH} = "develop" || ${GIT_BRANCH} = "master"  ]]; then
                        cd ${WORKSPACE}
                        ls -la
                        ls -l $(pwd)/aks/
                        kubectl apply -f $(pwd)/aks/00_namespace.yml || true
                        cat $(pwd)/aks/10_deployment.yml | sed -e 's|__COMMIT_ID__|'${GIT_COMMIT}'|g' >> $(pwd)/aks/10_deployment_2.yml
                        kubectl apply -f $(pwd)/aks/10_deployment_2.yml
                    fi
                    '''
            }
        }
    }
}