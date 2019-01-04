#!/usr/bin/env bash
cd ..
./build.sh $2
cd ./local
echo "done building"
docker-compose -f $1 up -d --build $2
