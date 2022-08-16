#!/bin/bash

REPOSITORY=$1
if [ -z "$REPOSITORY" ]; then
    echo "please specify the repository e.g. 127.0.0.1:5000/app"
    exit 0
fi

echo -e "********************************************************"
echo -e "**                     mvn package                    **"
echo -e "********************************************************"
mvn clean package -Dmaven.test.skip=true

#BUILD_TIMESTAMP=$(date '+%Y%m%d%H%M%S')
#TAG="${BUILD_TIMESTAMP}"
TAG="latest"
SERVER_IMAGE="$REPOSITORY/hotcloud:$TAG"
WEB_IMAGE="$REPOSITORY/hotcloud-web:$TAG"


echo -e "********************************************************"
echo -e "**                  build server image                **"
echo -e "********************************************************"
docker build -f localized.Dockerfile  -t "${SERVER_IMAGE}" .

echo -e "********************************************************"
echo -e "**                   push server image                **"
echo -e "********************************************************"
docker push "${SERVER_IMAGE}"

echo -e "********************************************************"
echo -e "**                   build web image                  **"
echo -e "********************************************************"
docker build -f hotcloud-web/Dockerfile  -t "${WEB_IMAGE}" .

echo -e "********************************************************"
echo -e "**                   push web image                **"
echo -e "********************************************************"
docker push "${WEB_IMAGE}"