#!/bin/bash

version=$1
mvn versions:set -DnewVersion="${version}" -DgenerateBackupPoms=false
