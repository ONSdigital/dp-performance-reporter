#!/bin/bash -eux

pushd dp-performance-reporter
  mvn clean surefire:test
popd
