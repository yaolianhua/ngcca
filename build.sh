#!/bin/bash

HOTCLOUD_VERSION="0.1"
BUILD_TIMESTAMP=$(date '+%Y%m%d%H%M%S')
TAG="${HOTCLOUD_VERSION}.${BUILD_TIMESTAMP}"
echo "Using hotCloud tag: ${TAG}"
# Build image
IMAGE="yaolianhua/hotcloud:${TAG}"
docker build -f Dockerfile --build-arg HOTCLOUD_VERSION="${HOTCLOUD_VERSION}" -t "${IMAGE}" .

docker push "${IMAGE}"

echo "${IMAGE} pushed successful!"
