#!/usr/bin/env bash

echo
echo "#############################################"
echo "# building 'shared'                         #"
echo "#############################################"
./shared/gradlew -p shared install

echo
echo "#############################################"
echo "# building 'catalog'                        #"
echo "#############################################"
./catalog/gradlew -p catalog bootRepackage -x test
docker-compose build --no-cache catalog

echo
echo "#############################################"
echo "# building 'order'                          #"
echo "#############################################"
./order/gradlew -p order bootRepackage -x test
docker-compose build --no-cache order

echo
echo "#############################################"
echo "# building 'bakery'                         #"
echo "#############################################"
./bakery/gradlew -p bakery bootRepackage -x test
docker-compose build --no-cache bakery

echo
echo "#############################################"
echo "# building 'delivery'                       #"
echo "#############################################"
./delivery/gradlew -p delivery bootRepackage -x test
docker-compose build --no-cache delivery

echo
echo "#############################################"
echo "# building 'order-ui'                       #"
echo "#############################################"
./order-ui/gradlew -p order-ui bootRepackage -x test
docker-compose build --no-cache orderui
