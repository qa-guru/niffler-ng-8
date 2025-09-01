#!/bin/bash
source ./docker.properties
export COMPOSE_PROFILES=test
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"

export ALLURE_DOCKER_API=http://allure:5050/
export HEAD_COMMIT_MESSAGE="local build"
export ARCH=$(uname -m)

docker compose down
docker_containers=$(docker ps -a -q)
docker_images=$(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'niffler')

if [ ! -z "$docker_containers" ]; then
  echo "### Stop containers: $docker_containers ###"
  docker stop $docker_containers
  docker rm $docker_containers
fi

if [ "$1" = "firefox" ]; then
  export BROWSER="firefox"
  docker pull selenoid/vnc_firefox:125.0
fi

for image in "postgres:15.1" "confluentinc/cp-zookeeper:7.3.2" "confluentinc/cp-kafka:7.3.2" "${PREFIX}/niffler-auth-docker:latest" "${PREFIX}/niffler-currency-docker:latest" "${PREFIX}/niffler-gateway-docker:latest" "${PREFIX}/niffler-spend-docker:latest" "${PREFIX}/niffler-userdata-docker:latest" "${PREFIX}/niffler-ng-client-docker:latest" "aerokube/selenoid:1.11.3" "aerokube/selenoid-ui:1.10.11" "${PREFIX}/niffler-e-2-e-tests:latest" "frankescobar/allure-docker-service:2.27.0" "frankescobar/allure-docker-service-ui:7.0.3"; do
  if [[ "$(docker images -q "$image" 2> /dev/null)" == "" ]]; then
    echo "### image $image doesn't exist locally ###"
    echo "### Building images ###"
    echo '### Java version ###'
    java --version
    bash ./gradlew clean
    bash ./gradlew jibDockerBuild -x :niffler-e-2-e-tests:test
    break 2
  fi
done

docker pull selenoid/vnc_chrome:127.0
docker compose up -d
docker ps -a