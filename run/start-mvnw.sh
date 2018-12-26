#!/bin/sh
# runs the the provided module

set -ex
cd ../$1
./mvnw spring-boot:run
cd ../run
