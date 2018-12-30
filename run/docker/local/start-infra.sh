#!/bin/sh
set -ex

docker container prune -f
docker image prune -f

docker-compose -f docker-compose-infra.yml down
docker-compose -f docker-compose-infra.yml up
