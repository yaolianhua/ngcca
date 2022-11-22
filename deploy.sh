#!/bin/bash

ENV_FILE=$1
NAMESPACE="ngcca-system"
if [ -z "$ENV_FILE" ]; then
    echo "no env file specified. use default env file ngcca.env"
    ENV_FILE="ngcca.env"
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
    SERVICE=$1
    if [ -z "$SERVICE" ]; then
        print "deploy all"
        envsubst < ngcca.yaml | kubectl apply -f -
        sleep 3

        envsubst < ngcca-web.yaml | kubectl apply -f -
        sleep 3

        return
    fi

    if [ "$SERVICE" = "server" ]; then
        print "deploy $SERVICE"
        envsubst < ngcca.yaml | kubectl apply -f -
        sleep 3

        return
    fi

    if [ "$SERVICE" = "web" ]; then
        print "deploy $SERVICE"
        envsubst < ngcca-web.yaml | kubectl apply -f -
        sleep 3

        return
    fi

    echo "[deploy service] wrong service param [server|web]"
}
function print() {
    V=$1
    echo "*************************************************"
    echo "** $V **"
    echo "*************************************************"
}

function delete() {
  SERVICE=$1
  if [ -z "$SERVICE" ]; then
      print "delete all"
      envsubst < ngcca.yaml | kubectl delete -f -
      sleep 3

      envsubst < ngcca-web.yaml | kubectl delete -f -
      sleep 3

      return
  fi

  if [ "$SERVICE" = "server" ]; then
      print "delete $SERVICE"
      envsubst < ngcca.yaml | kubectl delete -f -
      sleep 3

      return
  fi

  if [ "$SERVICE" = "web" ]; then
      print "delete $SERVICE"
      envsubst < ngcca-web.yaml | kubectl delete -f -
      sleep 3

      return
  fi

  echo "[delete service] wrong service param [server|web]"
}

function deploy() {
    SERVICE=$1
    loadEnv

    createNamespace

    delete "$SERVICE"

    sleep 3

    apply "$SERVICE"
}

function createNamespace() {
    V=$(kubectl get ns |grep "$NAMESPACE"|awk '{print $1}')
    if [ -z "$V" ]; then
        kubectl create ns "$NAMESPACE"
    fi
}

deploy "$2"