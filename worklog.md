

Notes and steps:

- started with the config server
    - created 'native' configs for local development
    - created the test local configs
    - add maven docker plugin, docker file and a script to build and create the image and run docker container

- moved to food-catalog created the crud flows
    - copied few classes from nutracker-api
    - converted code to be reactive (reactive mongo and arguments/parameters)
    - added custom search method to extend the reactive repo
    - add junit 5 integration test for food service using web client

- service discovery
    - Eurka server needed xml dependencies (see pom) because jdk 11 misses them
    - added bootstrap.yml for config server
    - added configs in configs resources in config server

- spent more time in mongo db security and properties to make running app easier

- added identity service (without db initially)

- added docker containers hosts to etc/hosts automatically to allow quick seamless communication between local running and docker

- added post man collection export to run directory

- added entry service
    - added call to food service
    - added resilience4j

- updated run/docker scripts with options









- next:
    - identity service
    - api gateway
    - oauth2
    - elastic search
    - log aggregation and request trace
    - spring admin
    - k8s
    - jenkins
    -
