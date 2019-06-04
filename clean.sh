#!/bin/bash
#SPDX-License-Identifier: Apache-2.0
#Author: kehm

#Tear down the network and remove generated artifacts and cryptographic material
#NOTE:Stops and removes ALL docker containers running on your system! Sudo required.

set -e
echo "Stopping all docker containers:Start"
docker stop $(docker ps -aq)
echo "Stopping all docker containers:Done"
echo "Removing all docker containers:Start"
docker rm $(docker ps -aq)
echo "Removing all docker containers:Done"
echo "Removing docker volumes:Start"
docker volume prune -f
echo "Removing docker volumes:Done"
echo "Removing artifacts:Start"
sudo rm -fr crypto-config
sudo rm -fr channel-artifacts
sudo rm certs.jks
echo "Removing artifacts:Done"
echo "Removing chaincode docker images:Start"
set +e
docker rmi $(docker images |grep 'dev') &> /dev/null
echo "Removing chaincode docker images:Done"
