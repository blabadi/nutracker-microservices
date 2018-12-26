#!/bin/sh
set -ex

docker-compose -f docker-compose-local.yml down
docker image prune
docker rm $(docker ps -aq) || echo "nothing started"
docker-compose -f docker-compose-local.yml up
