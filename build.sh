#!/bin/bash

REPOSITORY_SERVER=$1
REPOSITORY_WEB=$2

if [ -z "$1" ]; then
    REPOSITORY_SERVER="yaolianhua/hotcloud"
fi

if [ -z "$2" ]; then
    REPOSITORY_WEB="yaolianhua/hotcloud-web"
fi

BUILD_TIMESTAMP=$(date '+%Y%m%d%H%M%S')
TAG="${BUILD_TIMESTAMP}"
echo "Using tag: ${TAG}"

SERVER_IMAGE="$REPOSITORY_SERVER:${TAG}"

echo "start build server image ..."
docker build -f Dockerfile  -t "${SERVER_IMAGE}" .

echo "build server image end ..."
echo "start push server image ..."
docker push "${SERVER_IMAGE}"

# ----------------------------------------------
WEB_IMAGE="$REPOSITORY_WEB:${TAG}"

echo "start build web image ..."
docker build -f hotcloud-web/Dockerfile  -t "${WEB_IMAGE}" .

echo "build web image end ..."
echo "start push web image ..."
docker push "${WEB_IMAGE}"