#!/usr/bin/env bash

echo
echo "#############################################"
echo "# building 'shared'                         #"
echo "#############################################"
./shared/gradlew -p shared clean install

echo
echo "#############################################"
echo "# building 'catalog'                        #"
echo "#############################################"
./catalog/gradlew -p catalog clean check bootRepackage
docker-compose build --no-cache catalog

echo
echo "#############################################"
echo "# building 'order'                          #"
echo "#############################################"
./order/gradlew -p order clean check bootRepackage
docker-compose build --no-cache order

echo
echo "#############################################"
echo "# building 'bakery'                         #"
echo "#############################################"
./bakery/gradlew -p bakery clean check bootRepackage
docker-compose build --no-cache bakery

echo
echo "#############################################"
echo "# building 'delivery'                       #"
echo "#############################################"
./delivery/gradlew -p delivery clean check bootRepackage
docker-compose build --no-cache delivery

echo
echo "#############################################"
echo "# building 'order-ui'                       #"
echo "#############################################"
./order-ui/gradlew -p order-ui clean check bootRepackage
docker-compose build --no-cache orderui
