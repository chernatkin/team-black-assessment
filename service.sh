#!/usr/bin/env bash

echo "--> stopping previously running containers"
docker-compose kill

echo "--> removing stopped containers"
docker-compose rm -fv

echo "--> building microservice"

echo
echo "#############################################"
echo "# building 'shared'                         #"
echo "#############################################"
./shared/gradlew -p shared install

echo
echo "#############################################"
echo "# building 'catalog'                        #"
echo "#############################################"
./catalog/gradlew -p catalog bootRepackage

echo
echo "#############################################"
echo "# building 'order'                          #"
echo "#############################################"
./order/gradlew -p order bootRepackage

echo
echo "#############################################"
echo "# building 'bakery'                         #"
echo "#############################################"
./bakery/gradlew -p bakery bootRepackage

echo
echo "#############################################"
echo "# building 'delivery'                       #"
echo "#############################################"
./delivery/gradlew -p delivery bootRepackage

echo
echo "#############################################"
echo "# building 'order-ui'                       #"
echo "#############################################"
./order-ui/gradlew -p order-ui bootRepackage

docker-compose build --no-cache orderui

echo "--> building new containers"
docker-compose build --no-cache

echo "--> starting new containers"
docker-compose up -d
docker-compose ps
