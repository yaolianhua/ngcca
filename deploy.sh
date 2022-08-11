#!/bin/bash

ENV_FILE=$1

if [ -z "$ENV_FILE" ]; then
    echo "no env file specified. use default env file hotcloud.env"
    ENV_FILE="hotcloud.env"
fi

if [ ! -f "$ENV_FILE" ]; then
     echo "env file '$ENV_FILE' not exist."
     exit 0
fi

function loadEnv() {
    while IFS='=' read -r key value; do
      if [ -z "$value" ]; then
          echo "env $key value is empty" value="$value"
          exit 0
      fi
      export "$key"="$value"
    done < $ENV_FILE
}

function apply() {
    echo "deploy hotcloud server ..."
    envsubst < hotcloud.yaml | kubectl apply -f -
    sleep 3

    echo "deploy hotcloud web ..."
    envsubst < hotcloud-web.yaml | kubectl apply -f -
}

function delete() {
  echo "delete hotcloud server ..."
  envsubst < hotcloud.yaml | kubectl delete -f -
  sleep 3

  echo "delete hotcloud web ..."
  envsubst < hotcloud-web.yaml | kubectl delete -f -
}

function deploy() {
    loadEnv

    delete

    sleep 3

    apply
}

deploy