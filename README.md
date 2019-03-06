
# About

Nutracker is a macro nutritions and calories log and tracking application.
a user can search for a food, or add a portion of it and many other foods as daily entries in their daily log of meals.

## Backend Architecture

- Micro services
    - Domain services: entries, food-catalog, identity
    - Support services: config-server, eurka discovery, boot admin, OAuth2 auth-server
    - frameworks: spring boot 2 stack, webflux (reactive).
    - data sources: mongo, elastic search (tbd)
    - message bus: kafka, currently used by zipkin to log http traces
    - docker for containers
    - concerns and design goals:
        - Isolated and autonomous
        - Resilient, Fault tolerant
        - Responsive
        - Efficient
        - Scalable/ Elastic/ Highly available 
        - Monitored & traceable
        - Developer quality of life (strong tooling and fast workflow)
    - patterns:
        - service registry & discovery (Eurka)
        - external runtime-changable configurations (spring config server)
        - circuit breakers (resilience4j)
        - client side load balancing (Ribbon)
        - reactive async I/O flow (reactor)
        - stateless token based authentication (OAuth2 + jwt)
        - Api gateway as single point of entry (spring cloud gateway)
        - Monitoring:
            - health checking
            - logs aggregation
    - Technologies:
        - Spring boot 2: spring data, webflux, test
        - Netflix OSS: eurka, ribbon
        - resilience4j, circuit breaker
        - Monitoring: ELK , spring actuator & admin, Zipkin, slueth
        - containerization: docker
        - Elastic search server as search engine (tbd)
        - spring security 5, oauth2 + jwt tokens
        - data stores: mongo db
        - junit 5, mockito, embedded dbs
        - maven, git, shell scripts
        - java 11

# How to run

I went with a docker first approach where the configs are optimized to run faster with docker but
we can still run it without docker at all but will need manual work to get dbs up etc.. most the properties are overridable
and can be configured

infrastructure, config eurka mongo, etc execute:

```make start_infra```

services:

```make start_services```
(you need to start infra first)

if you want to work on specific service and run it without docker
example:
```./run/start-mvnw.sh -p food-catalog```
