#!/bin/bash

AWS_REGION=
CONFIG_BUCKET=
ECR_REPOSITORY_URI=
GIT_COMMIT=

INSTANCE=$(curl -s http://instance-data/latest/meta-data/instance-id)
CONFIG=$(aws --region $AWS_REGION ec2 describe-tags --filters "Name=resource-id,Values=$INSTANCE" "Name=key,Values=Configuration" --output text | awk '{print $5}')

(aws s3 cp s3://$CONFIG_BUCKET/performance-reporter/$CONFIG.asc . && gpg --decrypt $CONFIG.asc > $CONFIG) || exit $?

source $CONFIG && docker run -d                          \
  --env=AWS_BUCKET_NAME=$PERFORMANCE_BUCKET              \
  --env=GOOGLE_ACCOUNT_ID=$GOOGLE_ACCOUNT_ID             \
  --env=GOOGLE_PRIVATE_KEY=$GOOGLE_PRIVATE_KEY           \
  --env=GOOGLE_PROFILE_ID=$GOOGLE_PROFILE_ID             \
  --env=PINGDOM_APPLICATION_KEY=$PINGDOM_APPLICATION_KEY \
  --env=PINGDOM_PASSWORD=$PINGDOM_PASSWORD               \
  --env=PINGDOM_USERNAME=$PINGDOM_USERNAME               \
  --env=SPLUNK_HOST=$SPLUNK_HOST                         \
  --env=SPLUNK_PASSWORD=$SPLUNK_PASSWORD                 \
  --env=SPLUNK_PORT=$SPLUNK_PORT                         \
  --env=SPLUNK_USERNAME=$SPLUNK_USERNAME                 \
  --name=performance-reporter                            \
  --net=metrics                                          \
  --restart=always                                       \
  $ECR_REPOSITORY_URI/performance-reporter:$GIT_COMMIT
