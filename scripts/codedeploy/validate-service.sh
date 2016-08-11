#!/bin/bash

if [[ $(docker inspect --format="{{ .State.Running }}" performance-reporter) == "false" ]]; then
  exit 1;
fi
