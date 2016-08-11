#!groovy

node {
    stage 'Checkout'
    checkout scm
    sh 'git clean -dfx'
    sh 'git rev-parse --short HEAD > git-commit'
    sh 'set +e && (git describe --exact-match HEAD || true) > git-tag'

    stage 'Build'
    sh "${tool 'm3'}/bin/mvn clean package dependency:copy-dependencies"

    stage 'Image'
    def revision = revision()
    docker.withRegistry("https://${env.ECR_REPOSITORY_URI}", { ->
        sh '$(aws ecr get-login)'
        docker.build('performance-reporter').push(revision)
    })

    stage 'Bundle'
    sh sprintf('sed -i -e %s -e %s -e %s -e %s -e %s appspec.yml scripts/codedeploy/*', [
        "s/\\\${CODEDEPLOY_USER}/${env.CODEDEPLOY_USER}/g",
        "s/^CONFIG_BUCKET=.*/CONFIG_BUCKET=${env.S3_CONFIGURATIONS_BUCKET}/",
        "s/^ECR_REPOSITORY_URI=.*/ECR_REPOSITORY_URI=${env.ECR_REPOSITORY_URI}/",
        "s/^GIT_COMMIT=.*/GIT_COMMIT=${revision}/",
        "s/^AWS_REGION=.*/AWS_REGION=${env.AWS_DEFAULT_REGION}/",
    ])
    sh "tar -cvzf performance-reporter-${revision}.tar.gz appspec.yml scripts/codedeploy"
    sh "aws s3 cp performance-reporter-${revision}.tar.gz s3://${env.S3_REVISIONS_BUCKET}/"

    if (env.JOB_NAME.replaceFirst('.+/', '') != 'develop') return

    stage 'Deploy'
    sh sprintf('aws deploy create-deployment %s %s %s,bundleType=tgz,key=%s', [
        '--application-name performance-reporter',
        "--deployment-group-name ${env.CODEDEPLOY_METRICS_DEPLOYMENT_GROUP}",
        "--s3-location bucket=${env.S3_REVISIONS_BUCKET}",
        "performance-reporter-${revision}.tar.gz",
    ])
}

def revision() {
    def matcher = (readFile('git-tag').trim() =~ /^release\/(\d+\.\d+\.\d+(?:-rc\d+)?)$/)
    matcher.matches() ? matcher[0][1] : readFile('git-commit').trim()
}
