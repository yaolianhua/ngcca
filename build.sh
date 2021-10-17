#!/bin/bash

HOTCLOUD_VERSION="0.1"
BUILD_TIMESTAMP=$(date '+%Y%m%d%H%M%S')

echo "Using hotCloud version: ${HOTCLOUD_VERSION}"
# Build image

IMAGE="registry.local/hot-cloud/hotcloud:${HOTCLOUD_VERSION}.${BUILD_TIMESTAMP}"
docker build -f Dockerfile \
  --build-arg HOTCLOUD_VERSION="${HOTCLOUD_VERSION}" -t "${IMAGE}" .
docker push "${IMAGE}"
