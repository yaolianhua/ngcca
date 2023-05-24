#!/bin/bash

show_help_and_exit() {
  cat << EOF
Usage: $0 <project>

projects:
  kubernetes-agent                kubernetes-agent server service
  core-server                     core server service
  web                             web server service

demo：
  $0 kubernetes-agent
  $0 core-server
  $0 web

EOF
  exit 0
}


build_kubernetes_agent(){
    echo "------ commit id ------"
    printf "%s$(git rev-parse HEAD) \n"

    echo "------ jar build ------"
    mvn --batch-mode --errors --fail-fast --threads 1C --projects "io.hotcloud:ngcca-kubernetes-starter" --also-make clean package

    IMAGE="harbor.local:5000/ngcca/kubernetes-agent:$(date '+%Y.%m.%d.%H%M%S')"

    echo "------ docker build ------"
    docker build -f ngcca-kubernetes/Dockerfile -t "${IMAGE}" .

    echo "------ docker push ------"
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
*)
  printf "unsupported project '%s$project' \n"
  show_help_and_exit
  ;;
esac