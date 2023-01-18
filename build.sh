#!/bin/bash

namespace=$1
if [[ -z $namespace ]]; then
    namespace="harbor.local:5000"
    echo "used local default registry '$namespace'"
fi

mvn --batch-mode --errors --fail-fast --threads 1C clean package

IMAGE="$namespace/ngcca/ngcca-server:$(date '+%Y%m%d%H%M%S')"

docker build -t "${IMAGE}" .
docker push "${IMAGE}"