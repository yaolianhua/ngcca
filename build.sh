#!/bin/bash

HOTCLOUD_VERSION=$1
if [ ! "$1" ]; then
    echo "You can use build.sh <version>"
    exit 0
fi
BUILD_TIMESTAMP=$(date '+%Y%m%d%H%M%S')
TAG="${HOTCLOUD_VERSION}.${BUILD_TIMESTAMP}"
echo "Using hotcloud tag: ${TAG}"
# Build image
IMAGE="yaolianhua/hotcloud:${TAG}"
docker build -f Dockerfile  -t "${IMAGE}" .

docker push "${IMAGE}"

echo "${IMAGE} pushed successful!"
