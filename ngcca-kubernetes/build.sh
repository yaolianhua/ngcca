#!/bin/bash

namespace=$1
if [[ -z $namespace ]]; then
    namespace="harbor.local:5000"
    echo "used registry url '$namespace'"
fi

mvn --batch-mode --errors --fail-fast --threads 1C clean package

IMAGE="$namespace/ngcca/k8s-agent:$(date '+%Y%m%d%H%M%S')"

docker build -t "${IMAGE}" .
docker push "${IMAGE}"