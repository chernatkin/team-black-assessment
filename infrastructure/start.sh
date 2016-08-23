#!/usr/bin/env bash

OLD_DIR=$( pwd )
NEW_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )


echo "--> changing into directory '${NEW_DIR}'"
cd ${NEW_DIR}

echo "--> stopping previously running containers"
docker-compose kill

echo "--> removing stopped containers"
docker-compose rm -fv

echo "--> building new containers"
docker-compose build --no-cache

echo "--> starting new containers"
docker-compose up -d

echo "--> returning to previous directory '${OLD_DIR}'"
cd ${OLD_DIR}
