#!/bin/bash

AWS_REGION=
CONFIG_BUCKET=
ECR_REPOSITORY_URI=
GIT_COMMIT=

INSTANCE=$(curl -s http://instance-data/latest/meta-data/instance-id)
CONFIG=$(aws --region $AWS_REGION ec2 describe-tags --filters "Name=resource-id,Values=$INSTANCE" "Name=key,Values=Configuration" --output text | awk '{print $5}')

aws s3 cp s3://$CONFIG_BUCKET/performance-reporter/$CONFIG . || exit $?

source $CONFIG && docker run -d                                \
  --env=AWS_BUCKET_NAME=$PERFORMANCE_BUCKET                    \
  --env=GOOGLE_APPLICATION_CREDENTIALS=$GOOGLE_APP_CREDENTIALS \
  --env=GOOGLE_PROFILE_ID=$GOOGLE_PROFILE_ID                   \
  --env=INFLUXDB_URL=$INFLUXDB_URL                             \
  --env=PINGDOM_APPLICATION_KEY=$PINGDOM_APPLICATION_KEY       \
  --env=PINGDOM_PASSWORD=$PINGDOM_PASSWORD                     \
  --env=PINGDOM_USERNAME=$PINGDOM_USERNAME                     \
  --name=performance-reporter                                  \
  --net=influx                                                 \
  --restart=always                                             \
  $ECR_REPOSITORY_URI/performance-reporter:$GIT_COMMIT
