#!/bin/sh
set -ex

docker container prune -f
docker image prune -f

docker-compose -f infra.yml down
docker-compose -f infra.yml up
