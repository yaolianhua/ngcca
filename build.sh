#!/bin/bash

mvn clean package

REPOSITORY="harbor.local:5000/ngcca"
BUILD_TIMESTAMP=$(date '+%Y%m%d%H%M%S')
IMAGE="$REPOSITORY/ngcca-server:$BUILD_TIMESTAMP"

docker build -t "${IMAGE}" .
docker push "${IMAGE}"