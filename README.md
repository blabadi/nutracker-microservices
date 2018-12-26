
How to run:

infrastructure:
```./run/docker/start.sh```

services:


Notes and steps:

1- started with the config server
    - created 'native' configs for local development
    - created the test local configs
    - add maven docker plugin, docker file and a script to build and create the image and run docker container

2- moved to food-catalog created the crud flows
    - copied few classes from nutracker-api
    - converted code to be reactive (reactive mongo and arguments/parameters)
    - added custom search method to extend the reactive repo
    - add junit 5 integration test for food service using web client

3- service discovery
    - Eurka server needed xml dependencies (see pom) because jdk 11 misses them
    - added bootstrap.yml for config server
    - added configs in configs resources in config server

spent more time in mongo db security and properties to make running app easier