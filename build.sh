#!/bin/bash

HOTCLOUD_VERSION="0.1"
#BUILD_TIMESTAMP=$(date '+%Y%m%d%H%M%S')
TAG="latest"
echo "Using hotCloud version: ${TAG}"
# Build image
IMAGE="registry.local:5000/hotcloud/hotcloud:${TAG}"
docker build -f Dockerfile --build-arg HOTCLOUD_VERSION="${HOTCLOUD_VERSION}" -t "${IMAGE}" .
# docker login harbor.local:7000 -u admin -p Harbor12345
docker push "${IMAGE}"

echo "${IMAGE} pushed successful!"
