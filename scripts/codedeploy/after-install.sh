#!/bin/bash

AWS_REGION=
ECR_REPOSITORY_URI=
GIT_COMMIT=

$(aws ecr get-login --region $AWS_REGION --registry-ids ${ECR_REPOSITORY_URI:0:12}) && docker pull $ECR_REPOSITORY_URI/performance-reporter:$GIT_COMMIT
