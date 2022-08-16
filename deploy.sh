#!/bin/bash

ENV_FILE=$1
NAMESPACE="hotcloud-system"
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
    SERVICE=$1
    if [ -z "$SERVICE" ]; then
        print "deploy all"
        envsubst < hotcloud.yaml | kubectl apply -f -
        sleep 3

        envsubst < hotcloud-web.yaml | kubectl apply -f -
        sleep 3

        return
    fi

    if [ "$SERVICE" = "server" ]; then
        print "deploy $SERVICE"
        envsubst < hotcloud.yaml | kubectl apply -f -
        sleep 3

        return
    fi

    if [ "$SERVICE" = "web" ]; then
        print "deploy $SERVICE"
        envsubst < hotcloud-web.yaml | kubectl apply -f -
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
      envsubst < hotcloud.yaml | kubectl delete -f -
      sleep 3

      envsubst < hotcloud-web.yaml | kubectl delete -f -
      sleep 3

      return
  fi

  if [ "$SERVICE" = "server" ]; then
      print "delete $SERVICE"
      envsubst < hotcloud.yaml | kubectl delete -f -
      sleep 3

      return
  fi

  if [ "$SERVICE" = "web" ]; then
      print "delete $SERVICE"
      envsubst < hotcloud-web.yaml | kubectl delete -f -
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