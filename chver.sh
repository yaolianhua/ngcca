#!/bin/bash

version=$1
if [ ! $1 ]; then
    echo "You can use chver.sh <version> to update pom version batch. e.g -> chver 1.0.0"
    exit 0
fi
mvn versions:set -DnewVersion="${version}" -DgenerateBackupPoms=false
