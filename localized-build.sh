#!/bin/bash

TAG="latest"
echo "Using hotcloud tag: ${TAG}"
# Build image

IMAGE="yaolianhua/hotcloud:${TAG}"
docker build -f localized.Dockerfile -t "${IMAGE}" .
docker push "${IMAGE}"

echo "${IMAGE} pushed successful!"
