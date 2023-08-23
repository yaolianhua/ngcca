#!/bin/bash

show_help_and_exit() {
  cat << EOF
Usage: $0 <project>

projects:
  kubernetes-agent                kubernetes-agent server service
  ngcca-server                     core server service
  web-server                      web server service

demo：
  $0 kubernetes-agent
  $0 ngcca-server
  $0 web-server

EOF
  exit 0
}


build_kubernetes_agent(){
    echo "------------------------ commit id ------------------------"
    printf "%s$(git rev-parse HEAD) \n"

    echo "------------------------ jar build ------------------------"
    mvn --batch-mode --errors --fail-fast --threads 1C --projects "io.hotcloud:ngcca-kubernetes-server" --also-make clean package

    IMAGE="harbor.local:5000/ngcca/kubernetes-agent:$(date '+%Y.%m.%d.%H%M%S')"

    echo "------------------------ docker build ------------------------"
    docker build -f ngcca-kubernetes/Dockerfile -t "${IMAGE}" .

    echo "------------------------ docker push ------------------------"
    docker push "${IMAGE}"
}

build_ngcca_server(){
    echo "------------------------ commit id ------------------------"
    printf "%s$(git rev-parse HEAD) \n"

    echo "------------------------ jar build ------------------------"
    mvn --batch-mode --errors --fail-fast --threads 1C --projects "io.hotcloud:ngcca-server" --also-make clean package

    IMAGE="harbor.local:5000/ngcca/ngcca-server:$(date '+%Y.%m.%d.%H%M%S')"

    echo "------------------------ docker build ------------------------"
    docker build -f ngcca-server/Dockerfile -t "${IMAGE}" .

    echo "------------------------ docker push ------------------------"
    docker push "${IMAGE}"
}

build_web_server(){
    echo "------------------------ commit id ------------------------"
    printf "%s$(git rev-parse HEAD) \n"

    echo "------------------------ jar build ------------------------"
    mvn --batch-mode --errors --fail-fast --threads 1C --projects "io.hotcloud:ngcca-web" --also-make clean package

    IMAGE="harbor.local:5000/ngcca/web-server:$(date '+%Y.%m.%d.%H%M%S')"

    echo "------------------------ docker build ------------------------"
    docker build -f ngcca-web/Dockerfile -t "${IMAGE}" .

    echo "------------------------ docker push ------------------------"
    docker push "${IMAGE}"
}

project=$1
if [ -z "$project" ]; then
    show_help_and_exit
fi

case $project in
kubernetes-agent)
  build_kubernetes_agent
  ;;
ngcca-server)
  build_ngcca_server
  ;;
web-server)
  build_web_server
  ;;
*)
  printf "unsupported project '%s$project' \n"
  show_help_and_exit
  ;;
esac