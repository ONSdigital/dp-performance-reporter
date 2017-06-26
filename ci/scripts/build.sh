#!/bin/bash -eux

pushd dp-performance-reporter
  mvn clean package dependency:copy-dependencies -DskipTests=true
popd

cp -r dp-performance-reporter/target/* target/
