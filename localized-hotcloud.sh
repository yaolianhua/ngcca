#!/bin/bash
echo -e "################################################################################"
echo -e "mvn clean package install -DskipTests"
mvn clean package -DskipTests

echo -e "################################################################################"
echo -e "docker build"
./localized-build.sh