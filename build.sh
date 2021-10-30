#!/bin/bash

HOTCLOUD_VERSION="0.1"
BUILD_TIMESTAMP=$(date '+%Y%m%d%H%M%S')

echo "Using hotCloud version: ${HOTCLOUD_VERSION}"
# Build image

IMAGE="harbor.local:7000/hotcloud/hotcloud:${HOTCLOUD_VERSION}.${BUILD_TIMESTAMP}"
docker build -f Dockerfile --build-arg HOTCLOUD_VERSION="${HOTCLOUD_VERSION}" -t "${IMAGE}" .
docker login harbor.local:7000 -u admin -p Harbor12345
docker push "${IMAGE}"

echo "${IMAGE} pushed successful!"
