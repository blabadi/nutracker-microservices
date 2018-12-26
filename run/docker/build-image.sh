#!/bin/sh
# Builds the docker image of the provided module

set -ex
cd ../../$1
./mvnw -Dmaven.test.skip=true clean package dockerfile:build
cd ../run/docker
