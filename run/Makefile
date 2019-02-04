.ONESHELL:
DOCKER_COMPOSE_LOCAL_DIR = ./docker/local
P = "you must send a valid project name"

start_infra: docker_clean
	cd $(DOCKER_COMPOSE_LOCAL_DIR);
	docker-compose -f infra.yml up -d
	sleep 10

start_services:
	cd $(DOCKER_COMPOSE_LOCAL_DIR);
	docker-compose -f services.yml up -d
	sleep 10

start_all: start_infra start_services

start_mvn:
	./start-mvnw.sh -p $(P)

stop_docker:
	docker-compose -f $(DOCKER_COMPOSE_LOCAL_DIR)/infra.yml down
	docker-compose -f $(DOCKER_COMPOSE_LOCAL_DIR)/services.yml down

# ===================
docker_build_all:
	cd ./docker; ./build.sh --all-projects

docker_build_all:
	cd ./docker; ./build.sh --all-projects --push

docker_build_all_push:
	cd ./docker; ./build.sh --all-projects --push

docker_build:
	cd ./docker; ./build.sh $(P)

docker_clean:
	docker container prune -f;
	docker image prune -f;
	docker volume ls -qf dangling=true | xargs -r docker volume rm;
