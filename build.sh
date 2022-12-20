#!/bin/bash

namespace=$1
if [[ -z $namespace ]]; then
    namespace="harbor.local:5000"
    echo "used registry url '$namespace'"
fi

mvn clean package

IMAGE="$namespace/ngcca/ngcca-server:$(date '+%Y%m%d%H%M%S')"

docker build -t "${IMAGE}" .
docker push "${IMAGE}"