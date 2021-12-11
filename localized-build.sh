#!/bin/bash

TAG="latest"
echo "Using hotCloud tag: ${TAG}"
# Build image

IMAGE="registry.minikube.local:5000/hotcloud/hotcloud:${TAG}"
docker build -f localized.Dockerfile -t "${IMAGE}" .
docker push "${IMAGE}"

echo "${IMAGE} pushed successful!"
